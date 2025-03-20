package com.example.fsneaker.config;

import com.example.fsneaker.utils.ReadFileUtils;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DispatcherServletConfig {

    @Bean
    public ServletRegistrationBean exampleServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(
                new ReadFileUtils(), "/repository/*");
        bean.setLoadOnStartup(1);
        return bean;
    }
}
