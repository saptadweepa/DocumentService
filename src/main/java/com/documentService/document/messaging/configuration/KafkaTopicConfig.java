package com.documentService.document.messaging.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Properties;

import static com.documentService.document.messaging.KafkaEventPublisher.*;

/**
 * This class is used to create Topics on Kafka.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public AdminClient kafkaAdminClient() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return AdminClient.create(properties);
    }

    @Bean
    public CommandLineRunner createTopics(AdminClient adminClient) {
        return args -> {
            NewTopic serviceUpdatesTopic = new NewTopic(SERVICE_UPDATE_TOPIC, 1, (short) 1);
            NewTopic authorEventsTopic = new NewTopic(AUTHOR_TOPIC, 1, (short) 1);
            NewTopic documentEventsTopic = new NewTopic(DOCUMENT_TOPIC, 1, (short) 1);

            adminClient.createTopics(Collections.singletonList(serviceUpdatesTopic));
            adminClient.createTopics(Collections.singletonList(authorEventsTopic));
            adminClient.createTopics(Collections.singletonList(documentEventsTopic));
        };

    }
}
