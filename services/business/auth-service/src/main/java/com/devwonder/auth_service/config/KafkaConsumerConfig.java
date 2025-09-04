package com.devwonder.auth_service.config;

import com.devwonder.auth_service.event.ResellerApprovedEvent;
import com.devwonder.auth_service.event.ResellerDeletedEvent;
import com.devwonder.auth_service.event.ResellerRejectedEvent;
import com.devwonder.auth_service.event.ResellerRestoredEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer Configuration for auth-service
 * Configures consumer settings for receiving reseller events with robust error handling
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:kafka1:9092,kafka2:9093,kafka3:9094}")
    private String bootstrapServers;

    /**
     * Base consumer configuration with error handling and timeouts
     */
    private Map<String, Object> getBaseConsumerConfig(String groupId) {
        Map<String, Object> configProps = new HashMap<>();
        
        // Basic Kafka configuration
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Error-handling deserializers for resilience
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        
        // JSON deserializer configuration
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.devwonder.auth_service.event,com.devwonder.user_service.event");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        
        // Performance and reliability configuration
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        configProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
        configProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, 30000);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        
        return configProps;
    }

    /**
     * Consumer factory configuration for ResellerDeletedEvent
     */
    @Bean
    public ConsumerFactory<String, ResellerDeletedEvent> resellerDeletedEventConsumerFactory() {
        Map<String, Object> configProps = getBaseConsumerConfig("auth-service-reseller-deleted-group");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ResellerDeletedEvent.class.getName());
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Consumer factory configuration for ResellerRestoredEvent
     */
    @Bean
    public ConsumerFactory<String, ResellerRestoredEvent> resellerRestoredEventConsumerFactory() {
        Map<String, Object> configProps = getBaseConsumerConfig("auth-service-reseller-restored-group");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ResellerRestoredEvent.class.getName());
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Kafka listener container factory for ResellerDeletedEvent processing
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ResellerDeletedEvent> resellerDeletedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ResellerDeletedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(resellerDeletedEventConsumerFactory());
        
        // Concurrency configuration
        factory.setConcurrency(1); // Single consumer for ordered processing
        
        // Error handling - continue processing other messages on error
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        
        return factory;
    }

    /**
     * Kafka listener container factory for ResellerRestoredEvent processing
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ResellerRestoredEvent> resellerRestoredKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ResellerRestoredEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(resellerRestoredEventConsumerFactory());
        
        // Concurrency configuration
        factory.setConcurrency(1); // Single consumer for ordered processing
        
        // Error handling - continue processing other messages on error
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        
        return factory;
    }

    /**
     * Consumer factory configuration for ResellerApprovedEvent
     */
    @Bean
    public ConsumerFactory<String, ResellerApprovedEvent> resellerApprovedEventConsumerFactory() {
        Map<String, Object> configProps = getBaseConsumerConfig("auth-service-reseller-approved-group");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ResellerApprovedEvent.class.getName());
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Kafka listener container factory for ResellerApprovedEvent processing
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ResellerApprovedEvent> resellerApprovedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ResellerApprovedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(resellerApprovedEventConsumerFactory());
        factory.setConcurrency(1);
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        return factory;
    }

    /**
     * Consumer factory configuration for ResellerRejectedEvent
     */
    @Bean
    public ConsumerFactory<String, ResellerRejectedEvent> resellerRejectedEventConsumerFactory() {
        Map<String, Object> configProps = getBaseConsumerConfig("auth-service-reseller-rejected-group");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ResellerRejectedEvent.class.getName());
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Kafka listener container factory for ResellerRejectedEvent processing
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ResellerRejectedEvent> resellerRejectedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ResellerRejectedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(resellerRejectedEventConsumerFactory());
        factory.setConcurrency(1);
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        return factory;
    }
}