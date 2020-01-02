package com.cds.paceservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class ConfigProperties {

    @Autowired
    private Environment env;

    @Value("$ {com.cds.pacecalculator}")
    private String appTitle;

    public String getConfigValue(String configKey) {
        // TODO: fix this - it crashes getting properties
        String keyValue = env.getProperty(configKey);
        return keyValue;
    }
}