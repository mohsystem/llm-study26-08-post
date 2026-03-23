package com.um.springbootprojstructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity // enables @PreAuthorize on repositories/services
public class MethodSecurityConfig {
}
