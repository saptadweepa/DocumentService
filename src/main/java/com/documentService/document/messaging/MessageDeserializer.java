package com.documentService.document.messaging;

import com.documentService.document.messaging.events.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This service consumes kafka events as string, and using this class, the kafka events are converted to
 * respective service events
 */
@Service
@AllArgsConstructor
public class MessageDeserializer {

    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(MessageDeserializer.class);

    private DocumentServiceEventType getType(String message){

        try {
            JsonNode parsedData = objectMapper.readTree(message);

            return DocumentServiceEventType.valueOf(parsedData.get("eventType").textValue());

        } catch (Exception e){
            logger.error("error during message deserialization", e);
            return null;
        }

    }

    public AbstractDocumentServiceEvent parseEvent(String message){

        DocumentServiceEventType type = getType(message);
        if(type == null) {
            logger.error("unable to parse event");
            return null;
        }

        try {
            return switch (type){
                case CREATE_AUTHOR_EVENT -> objectMapper.readValue(message, CreateAuthorEvent.class);
                case DELETE_AUTHOR_EVENT -> objectMapper.readValue(message, DeleteAuthorEvent.class);
                case SERVICE_UPDATE_EVENT -> objectMapper.readValue(message, ServiceUpdateEvent.class);
                case CREATE_DOCUMENT_EVENT -> objectMapper.readValue(message, CreateDocumentEvent.class);
                case DELETE_DOCUMENT_EVENT -> objectMapper.readValue(message, DeleteDocumentEvent.class);
            };
        } catch (Exception e){
            logger.error("unable to parse event", e);
            return null;
        }

    }

}
