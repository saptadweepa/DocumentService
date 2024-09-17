package com.documentService.document.service;

import com.documentService.document.model.Author;
import com.documentService.document.model.Document;
import com.documentService.document.repository.AuthorRepository;
import com.documentService.document.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.internal.util.collections.CollectionHelper.setOf;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private DocumentRepository documentRepository;

    private Author author;

    @BeforeEach
    public void setUp() {
        documentRepository.deleteAll();
        authorRepository.deleteAll();

        author = new Author(null, "firstname", "lastname");
        authorRepository.save(author);
    }

    @Test
    @Transactional
    public void shouldBeAbleToFindAllDocuments() {
        //GIVEN
        Set<Document> documents = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Document document = new Document(
                    null, "testTitle" + i, "testBody" + i, setOf(author), null
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
                null, "testTitle", "testBody", setOf(author), null
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
        Document newDocument = new Document(null, "Another Title", "Another Body",  setOf(author), new HashSet<>());

        //WHEN
        Document savedDocument = documentService.saveDocument(newDocument);

        //THEN
        assertThat(savedDocument).isNotNull();
        assertThat(savedDocument.getId()).isNotNull();
        assertThat(savedDocument).isEqualTo(newDocument);
    }

    @Test
    @Transactional
    public void fetchedDocumentShouldShouldContainAllAuthors(){
        //GIVEN
        Document newDocument = new Document(null, "Another Title", "Another Body",  setOf(author), new HashSet<>());
        Document savedDocument = documentService.saveDocument(newDocument);

        Author additionalAuthor = new Author(null, "firstname", "lastname");
        authorRepository.save(additionalAuthor);

        savedDocument.addAuthor(additionalAuthor);
        documentService.saveDocument(savedDocument);

        Set<Author> expectedAuthors = setOf(author, additionalAuthor);

        //WHEN
        Optional<Document> documentFromDb = documentService.findDocumentById(savedDocument.getId());

        //THEN
        assertThat(documentFromDb).isPresent();
        assertThat(documentFromDb.get().getAuthors()).isEqualTo(expectedAuthors);
        
    }

    @Test
    public void shouldBeAbleToDelete() {
        //GIVEN
        Document newDocument = new Document(null, "Another Title", "Another Body",  setOf(author), new HashSet<>());
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
                    null, "testTitle" + i, "testBody" + i, setOf(author), null
            );

            documents.add(documentService.saveDocument(document));
        }
        Author additionalAuthor = new Author(null, "firstname", "lastname");
        authorRepository.save(additionalAuthor);
        Document additionalDocument = new Document(
                null, "testTitle2", "testBody2", setOf(additionalAuthor), null
        );
        documentService.saveDocument(additionalDocument);

        //WHEN
        List<Document> documentsForAuthor = documentService.findAllDocumentForAuthor(author);

        //THEN
        assertThat(new HashSet<>(documentsForAuthor))
                .isEqualTo(documents);
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
        Author nullIdAuthor = new Author(null, "first", "last");
        Exception exception = assertThrows(NullPointerException.class, () -> documentService.findAllDocumentForAuthor(nullIdAuthor));
        assertEquals("Author id must not be null", exception.getMessage());
    }
}
