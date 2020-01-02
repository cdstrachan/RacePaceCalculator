package com.cds.paceservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class PaceCalculatorAppConfig implements WebMvcConfigurer {
    @Autowired
    PaceCalculatorInceptor paceCalculatorInceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(paceCalculatorInceptor);
    }
}