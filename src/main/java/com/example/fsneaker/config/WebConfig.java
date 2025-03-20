package com.example.fsneaker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/product/**")
                .addResourceLocations("file:Fsneaker/src/main/resources/static/images/product/")
                .setCachePeriod(0); // Tắt cache
    }
}