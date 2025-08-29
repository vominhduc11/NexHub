package com.devwonder.product_service.config;

import com.devwonder.common.config.NexHubCommonConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration to enable NexHub Common AOP aspects
 */
@Configuration
@Import(NexHubCommonConfig.class)
public class AspectConfig {
}