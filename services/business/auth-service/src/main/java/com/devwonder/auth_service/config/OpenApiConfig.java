package com.devwonder.auth_service.config;

import com.devwonder.common.config.BaseOpenApiConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig extends BaseOpenApiConfig {

    @Override
    protected String getServiceDescription() {
        return "Authentication and authorization service";
    }
}