package com.documentService.document.service;

import com.documentService.document.messaging.KafkaEventPublisher;
import com.documentService.document.messaging.events.ServiceUpdateEvent;
import com.documentService.document.messaging.events.ServiceUpdateType;
import com.documentService.document.model.Author;
import com.documentService.document.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final KafkaEventPublisher publisher;

    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    public Optional<Author> findAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    @Transactional
    public Author saveAuthor(Author author) {
        boolean isNewAuthor = author.getId() == null;

        Author savedAuthor = authorRepository.save(author);

        ServiceUpdateEvent event = new ServiceUpdateEvent();
        event.setUpdateType(isNewAuthor ? ServiceUpdateType.AUTHOR_CREATED : ServiceUpdateType.AUTHOR_UPDATED);
        event.setAuthorId(savedAuthor.getId());
        publisher.publish(event);

        logger.info("created author {}", savedAuthor.getId());

        return savedAuthor;
    }

    @Transactional
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);

        ServiceUpdateEvent event = new ServiceUpdateEvent();
        event.setUpdateType(ServiceUpdateType.AUTHOR_DELETED);
        event.setAuthorId(id);

        logger.info("deleted author {}", id);

        publisher.publish(event);
    }
}