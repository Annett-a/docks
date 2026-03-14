package com.example.documents.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "com.example.documents.service",
        "com.example.documents.repo"
})
@Import(DataSourceConfig.class)
public class SpringRootConfig {
}