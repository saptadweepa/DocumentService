package com.documentService.document;

import com.documentService.document.messaging.events.AbstractDocumentServiceEvent;
import com.documentService.document.messaging.events.DocumentServiceEventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaEventPublisher {

    public static final String AUTHOR_TOPIC = "author-events";
    public static final String DOCUMENT_TOPIC = "document-events";
    public static final String SERVICE_UPDATE_TOPIC = "service-updates";

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);

    public void publish(AbstractDocumentServiceEvent event){

        String topic = getTopic(event);

        try{
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, payload);
            logger.info("successfully published {}", event.getEventType());
        } catch (Exception e){
            logger.error("Got error during publish", e);
        }

    }

    private String getTopic(AbstractDocumentServiceEvent event){

        if(isAuthorEvent(event)){
            return AUTHOR_TOPIC;
        } else if(isDocumentEvent(event)){
            return DOCUMENT_TOPIC;
        } else if (isServiceUpdateEvent(event)) {
            return SERVICE_UPDATE_TOPIC;
        } else {
            throw new IllegalStateException("Unknown Event");
        }
    }

    private boolean isDocumentEvent(AbstractDocumentServiceEvent event){
        return event.getEventType() == DocumentServiceEventType.CREATE_DOCUMENT_EVENT ||
                event.getEventType() == DocumentServiceEventType.DELETE_DOCUMENT_EVENT;
    }

    private boolean isAuthorEvent(AbstractDocumentServiceEvent event){
        return event.getEventType() == DocumentServiceEventType.CREATE_AUTHOR_EVENT ||
                event.getEventType() == DocumentServiceEventType.DELETE_AUTHOR_EVENT;
    }

    private boolean isServiceUpdateEvent(AbstractDocumentServiceEvent event){
        return event.getEventType() == DocumentServiceEventType.SERVICE_UPDATE_EVENT;
    }

    private String convertEventToJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert event to JSON", e);
        }
    }

}
