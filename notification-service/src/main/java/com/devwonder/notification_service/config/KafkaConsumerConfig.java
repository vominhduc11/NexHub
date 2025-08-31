package com.devwonder.notification_service.config;

import com.devwonder.notification_service.dto.NotificationEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> getBaseConsumerConfig(String groupId) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.devwonder.auth_service.dto,com.devwonder.notification_service.dto");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
        configProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, 30000);
        return configProps;
    }

    @Bean
    public ConsumerFactory<String, NotificationEvent> emailNotificationConsumerFactory() {
        Map<String, Object> configProps = getBaseConsumerConfig("email-notification-group");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NotificationEvent.class.getName());
        configProps.put(JsonDeserializer.TYPE_MAPPINGS, "com.devwonder.auth_service.dto.NotificationEvent:com.devwonder.notification_service.dto.NotificationEvent");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> notificationEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(emailNotificationConsumerFactory());
        return factory;
    }
    
    @Bean
    public ConsumerFactory<String, NotificationEvent> websocketNotificationConsumerFactory() {
        Map<String, Object> configProps = getBaseConsumerConfig("websocket-notification-group");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NotificationEvent.class.getName());
        configProps.put(JsonDeserializer.TYPE_MAPPINGS, "com.devwonder.auth_service.dto.NotificationEvent:com.devwonder.notification_service.dto.NotificationEvent");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> websocketNotificationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(websocketNotificationConsumerFactory());
        return factory;
    }
}