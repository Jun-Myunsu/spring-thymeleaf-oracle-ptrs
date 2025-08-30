// src/main/java/com/posco/ptrs/global/config/ETagConfig.java
package com.posco.ptrs.global.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import jakarta.servlet.DispatcherType;

// @Configuration
public class ETagConfig {
    
    // @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> etagFilter() {
        var filter = new ShallowEtagHeaderFilter();
        var reg = new FilterRegistrationBean<>(filter);
        reg.setName("shallowEtagFilter");
        reg.addUrlPatterns("/api/categories/*");
        reg.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        reg.setOrder(1);
        return reg;
    }
}