package com.documentService.document.service;

import com.documentService.document.model.Author;
import com.documentService.document.model.Document;
import com.documentService.document.model.Role;
import com.documentService.document.repository.AuthorRepository;
import com.documentService.document.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuthorRepository authorRepository;

    private Author author;

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceTest.class);

    @BeforeEach

    public void setUp() {

        jdbcTemplate.execute("TRUNCATE TABLE author CASCADE");


        Author newAuthor = new Author();
        newAuthor.setFirstName("firstname");
        newAuthor.setLastName("lastname");
        newAuthor.setUsername("username");
        newAuthor.setPassword("password");
        newAuthor.setRole(Role.ROLE_USER);

        author = authorRepository.save(newAuthor);


    }

    @Test
    @Transactional
    public void shouldBeAbleToFindAllDocuments() {

        //GIVEN
        Set<Document> documents = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Document document = new Document(
                    null, "testTitle" + i, "testBody" + i, author.getId(), null
            );

            documentService.saveDocument(document);
            documents.add(document);
        }

        //WHEN
        List<Document> documentsInDb = documentService.findAllDocuments();

        //THEN
        assertNotNull(documents);
        assertThat(new HashSet<>(documentsInDb))
                .isEqualTo(documents);
    }

    @Test
    @Transactional
    public void shouldBeAbleToFindById() {
        //GIVEN
        Document document = new Document(
                null, "testTitle", "testBody", author.getId(), null
        );
        Long id = documentService.saveDocument(document).getId();

        //WHEN
        Optional<Document> foundDocument = documentService.findDocumentById(id);

        //THEN
        assertThat(foundDocument).isPresent();
        assertThat(foundDocument.get()).isEqualTo(document);
    }

    @Test
    public void shouldBeAbleToSave() {
        //GIVEN
        Document newDocument = new Document(null, "Another Title", "Another Body",  author.getId(), new HashSet<>());

        //WHEN
        Document savedDocument = documentService.saveDocument(newDocument);

        //THEN
        assertThat(savedDocument).isNotNull();
        assertThat(savedDocument.getId()).isNotNull();
        assertThat(savedDocument).isEqualTo(newDocument);
    }

    @Test
    public void shouldBeAbleToDelete() {
        //GIVEN
        Document newDocument = new Document(null, "Another Title", "Another Body",  author.getId(), new HashSet<>());
        Document savedDocument = documentService.saveDocument(newDocument);

        //WHEN
        documentService.deleteDocument(savedDocument.getId());

        //THEN
        Optional<Document> deletedDocument = documentService.findDocumentById(savedDocument.getId());
        assertThat(deletedDocument).isNotPresent();
    }

    @Test
    @Transactional
    public void shouldBeAbleToFindAllDocumentForGivenAuthor() {
        //GIVEN
        Set<Document> documents = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Document document = new Document(
                    null, "testTitle" + i, "testBody" + i, author.getId(), null
            );

            documents.add(documentService.saveDocument(document));
        }
        Author additionalAuthor = new Author();
        additionalAuthor.setFirstName("firstname2");
        additionalAuthor.setLastName("lastname2");
        additionalAuthor.setUsername("username2");
        additionalAuthor.setPassword("password2");
        additionalAuthor.setRole(Role.ROLE_USER);

        authorRepository.save(additionalAuthor);
        Document additionalDocument = new Document(
                null, "testTitle2", "testBody2", additionalAuthor.getId(), null
        );
        documentService.saveDocument(additionalDocument);

        //WHEN
        List<Document> documentsForAuthor = documentService.findAllDocumentForAuthor(author);

        //THEN
        assertThat(new HashSet<>(documentsForAuthor))
                .isEqualTo(documents);
    }

    @Test
    public void shouldUpdateDocumentIdListInAuthorOnNewDocumentSave(){
        //GIVEN
        Document newDocument = new Document(null, "Another Title", "Another Body",  author.getId(), new HashSet<>());

        //WHEN
        Document savedDocument = documentService.saveDocument(newDocument);

        //THEN
        Author authorInDb = authorRepository.findById(author.getId()).orElseThrow();
        assertThat(authorInDb.getDocumentIds())
                .contains(savedDocument.getId());
    }

    @Test
    public void shouldUpdateDocumentIdListInAuthorOnDocumentDelete(){
        //GIVEN
        Document newDocument = new Document(null, "Another Title", "Another Body",  author.getId(), new HashSet<>());
        Document savedDocument = documentService.saveDocument(newDocument);

        //WHEN
        documentService.deleteDocument(savedDocument.getId());

        //THEN
        Author authorInDb = authorRepository.findById(author.getId()).orElseThrow();
        assertThat(authorInDb.getDocumentIds()).isEmpty();
    }

    @Test
    public void saveDocumentWithNullShouldThrowException() {
        Exception exception = assertThrows(NullPointerException.class, () -> documentService.saveDocument(null));
        assertEquals("saved document must not be null", exception.getMessage());
    }

    @Test
    public void findDocumentByIdWithNullShouldThrowException() {
        Exception exception = assertThrows(NullPointerException.class, () -> documentService.findDocumentById(null));
        assertEquals("Document ID must not be null", exception.getMessage());
    }

    @Test
    public void deleteDocumentWithNullShouldThrowException() {
        Exception exception = assertThrows(NullPointerException.class, () -> documentService.deleteDocument(null));
        assertEquals("Document ID must not be null", exception.getMessage());
    }

    @Test
    public void findAllDocumentsForAuthorWithNullAuthorShouldThrowException() {
        Exception exception = assertThrows(NullPointerException.class, () -> documentService.findAllDocumentForAuthor(null));
        assertEquals("Author must not be null", exception.getMessage());
    }

    @Test
    public void findAllDocumentsForAuthorWithNullAuthorIdShouldThrowException() {
        Author nullIdAuthor = new Author();
        nullIdAuthor.setId(null);
        nullIdAuthor.setFirstName("first");
        nullIdAuthor.setLastName("last");
        nullIdAuthor.setUsername("username");
        nullIdAuthor.setPassword("password");
        nullIdAuthor.setRole(Role.ROLE_USER);
        Exception exception = assertThrows(NullPointerException.class, () -> documentService.findAllDocumentForAuthor(nullIdAuthor));
        assertEquals("Author id must not be null", exception.getMessage());
    }
}
