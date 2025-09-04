package com.devwonder.auth_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Topic Configuration for auth-service
 * Defines topic creation and configuration for auth-related events
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * Configuration for reseller-deleted topic
     * Used for cross-service reseller lifecycle management
     */
    @Bean
    public NewTopic resellerDeletedTopic() {
        return TopicBuilder.name("reseller-deleted")
                .partitions(3) // Multiple partitions for parallel processing
                .replicas(3) // High availability with 3 replicas
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, "delete")
                .config(TopicConfig.RETENTION_MS_CONFIG, "2592000000") // 30 days retention for audit
                .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2") // Minimum 2 replicas in sync
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "gzip") // Compression for efficiency
                .build();
    }

    /**
     * Configuration for reseller-restored topic
     * Used for cross-service reseller restoration management
     */
    @Bean
    public NewTopic resellerRestoredTopic() {
        return TopicBuilder.name("reseller-restored")
                .partitions(3)
                .replicas(3)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, "delete")
                .config(TopicConfig.RETENTION_MS_CONFIG, "2592000000") // 30 days retention for audit
                .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2")
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "gzip")
                .build();
    }

    /**
     * Configuration for reseller-approved topic
     * Used for account activation when reseller is approved
     */
    @Bean
    public NewTopic resellerApprovedTopic() {
        return TopicBuilder.name("reseller-approved")
                .partitions(3)
                .replicas(3)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, "delete")
                .config(TopicConfig.RETENTION_MS_CONFIG, "2592000000") // 30 days retention for audit
                .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2")
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "gzip")
                .build();
    }

    /**
     * Configuration for reseller-rejected topic
     * Used for account deactivation when reseller is rejected
     */
    @Bean
    public NewTopic resellerRejectedTopic() {
        return TopicBuilder.name("reseller-rejected")
                .partitions(3)
                .replicas(3)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, "delete")
                .config(TopicConfig.RETENTION_MS_CONFIG, "2592000000") // 30 days retention for audit
                .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2")
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "gzip")
                .build();
    }

    /*
     * Note: email-notifications and websocket-notifications topics
     * are managed by notification-service as the primary consumer.
     * Auth-service acts as producer only for these topics.
     */
}