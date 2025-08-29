package com.devwonder.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration class for NexHub Common library
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.devwonder.common")
public class NexHubCommonConfig {
}