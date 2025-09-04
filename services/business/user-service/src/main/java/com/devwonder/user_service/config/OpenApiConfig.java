package com.devwonder.user_service.config;

import com.devwonder.common.config.BaseOpenApiConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig extends BaseOpenApiConfig {

    @Override
    protected String getServiceDescription() {
        return "User management service for customers, resellers and admins";
    }
}