package com.devwonder.nexhub_common;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.devwonder.common")
public class NexhubCommonApplication {
	// This is a library module - no main method needed for production use
}
