package com.patrick.delivery.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author patrick on 6/15/20
 * @project sprintel-delivery
 */
public class ApplicationMessages  {

    private static Logger logger = LoggerFactory.getLogger(ApplicationMessages.class);

    private static Properties _props;
    static {
        try {
            Properties props = new Properties();
            File configDir = new File(System.getProperty("catalina.base"), "conf");
            File configFile = new File(configDir, "messages.properties");
            if (configFile.exists()) {
                props.load(new FileInputStream(configFile));
            } else {
                // Get the properties file to get the default properties assigned to the app
                props.load((new ClassPathXmlApplicationContext()).getResource("classpath:messages.properties").getInputStream());
            }
            // Set the properties object
            _props = props;
        } catch (Exception ex) {
            logger.error("Error getting application messages", ex);
        }
    }

    /**
     * Called to read a given property from the properties file
     *
     * @param key
     * @return String
     */
    public static String get(String key) {
        return (_props.containsKey(key)) ? _props.get(key).toString() : "";
    }
}
