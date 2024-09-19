package com.documentService.document.messaging;

import com.documentService.document.messaging.events.*;
import com.documentService.document.model.Author;
import com.documentService.document.model.Document;
import com.documentService.document.model.Role;
import com.documentService.document.service.AuthorService;
import com.documentService.document.service.DocumentService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.documentService.document.messaging.KafkaEventPublisher.*;
import static com.documentService.document.messaging.configuration.KafkaConsumerConfig.GROUP_ID;

/**
 * This class polls various kafka topics and processes messages received on those topics
 */
@Service
@AllArgsConstructor
public class KafkaEventListener {

    private MessageDeserializer messageDeserializer;
    private AuthorService authorService;
    private DocumentService documentService;

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventListener.class);

    @PostConstruct
    public void startup(){
        logger.info("Kafka consumer is ready to consumer events");
    }

    @KafkaListener(topics = SERVICE_UPDATE_TOPIC, groupId = GROUP_ID)
    public void listenServiceUpdates(String message) {
        logger.info("received service update {}" , message);
        handleEvent(message);
    }

    @KafkaListener(topics = AUTHOR_TOPIC, groupId = GROUP_ID)
    public void listenAuthorEvents(String message) {
        logger.info("received author-event {}" , message);
        handleEvent(message);
    }

    @KafkaListener(topics = DOCUMENT_TOPIC, groupId = GROUP_ID)
    public void listenDocumentEvents(String message) {
        logger.info("received document-event {}" , message);
        handleEvent(message);
    }

    private void handleEvent(String message) {
        try {

            AbstractDocumentServiceEvent event = messageDeserializer.parseEvent(message);
            if(event == null){
                logger.error("unable to parse event {}", message);
            }

            switch (Objects.requireNonNull(event).getEventType()) {
                case CREATE_AUTHOR_EVENT:
                    createAuthor(event);
                    break;
                case DELETE_AUTHOR_EVENT:
                    deleteAuthor(event);
                    break;
                case CREATE_DOCUMENT_EVENT:
                    createDocument(event);
                    break;
                case DELETE_DOCUMENT_EVENT:
                    deleteDocument(event);
                    break;
                case SERVICE_UPDATE_EVENT:
                    logger.info("received service update: {}", event);
                    break;
            }
        } catch (Exception e) {
            logger.error("error during kafka message processing", e);
        }
    }

    private void createAuthor(AbstractDocumentServiceEvent event) {
        CreateAuthorEvent createAuthorEvent = (CreateAuthorEvent) event;

        Author author = new Author();

        author.setUsername(createAuthorEvent.getUsername());
        author.setPassword(createAuthorEvent.getPassword());
        author.setRole(Role.ROLE_USER);
        author.setFirstName(createAuthorEvent.getFirstName());
        author.setLastName(createAuthorEvent.getLastName());

        author = authorService.saveAuthor(author);

        logger.info("author created {}", author.getId());
    }

    private void deleteAuthor(AbstractDocumentServiceEvent event){

        DeleteAuthorEvent deleteAuthorEvent = (DeleteAuthorEvent) event;
        logger.warn("deleting author {}, all related document will be deleted", deleteAuthorEvent.getAuthorId());
        authorService.deleteAuthor(deleteAuthorEvent.getAuthorId());
        logger.info("Deleted author {}", deleteAuthorEvent.getAuthorId());
    }

    private void createDocument(AbstractDocumentServiceEvent event){
        CreateDocumentEvent createDocumentEvent = (CreateDocumentEvent) event;


        Document document = new Document();
        document.setTitle(createDocumentEvent.getTitle());
        document.setAuthorId(createDocumentEvent.getAuthorId());
        document.setBody(createDocumentEvent.getBody());
        document.setReferenceIds(createDocumentEvent.getReferenceIds());

        document = documentService.saveDocument(document);
        logger.info("created document {}", document.getId());
    }

    private void deleteDocument(AbstractDocumentServiceEvent event){

        DeleteDocumentEvent deleteDocumentEvent = (DeleteDocumentEvent) event;

        documentService.deleteDocument(deleteDocumentEvent.getDocumentId());
        logger.info("deleted document {}", deleteDocumentEvent.getDocumentId());
    }
}
