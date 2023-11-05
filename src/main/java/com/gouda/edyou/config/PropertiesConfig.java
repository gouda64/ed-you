package com.gouda.edyou.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-secrets.properties")
public class PropertiesConfig {
}
