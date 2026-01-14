package com.kgisl.todosrestclient.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration(proxyBeanMethods=false)
@ImportHttpServices(group = "todos", 
    basePackages = "com.kgisl.todosrestclient.service"
)
public class HttpClientConfig {
    // further configure that
}
