package com.patrick.delivery.utils.templates;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author SamKyalo
 */

public class Params {
    private static Properties _props;

    static {
        try {
            Properties props = new Properties();
            File configDir = new File(System.getProperty("catalina.base"), "conf");
            File configFile = new File(configDir, "application.properties");
            if (configFile.exists()) {
                props.load(new FileInputStream(configFile));
            } else {
                // Get the properties file to get the default properties assigned to the app
                props.load((new ClassPathXmlApplicationContext()).getResource("classpath:application.properties").getInputStream());
            }
            // Set the properties object
            _props = props;
        } catch (Exception ex) {
            Console.printStackTrace(ex);
        }
    }

    /**
     * Called to read a given property from the properties file
     *
     * @param key
     * @return String
     */
    public static String get(String key) {
        return (_props.containsKey(key)) ? _props.get(key).toString() : null;
    }
}
