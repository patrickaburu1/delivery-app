package com.patrick.delivery.configs;

import com.antkorwin.xsync.XSync;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class XSyncConfig {
    @Bean
    public XSync<Integer> intXSync(){
        return new XSync<>();
    }



    @Bean
    public XSync<String> xSync(){
        return new XSync<>();
    }


}
