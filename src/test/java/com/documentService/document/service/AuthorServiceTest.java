package com.documentService.document.service;

import com.documentService.document.model.Author;
import com.documentService.document.model.Document;
import com.documentService.document.model.Role;
import com.documentService.document.repository.AuthorRepository;
import com.documentService.document.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AuthorServiceTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanup() {
        jdbcTemplate.execute("TRUNCATE TABLE author CASCADE");
    }

    @Test
    @Transactional
    void shouldSaveAndFetchAuthor() {

        //GIVEN
        Author author = new Author();
        author.setId(null);
        author.setFirstName("TestFirstName");
        author.setLastName("TestLastName");
        author.setUsername("TestUsername");
        author.setPassword("TestPassword");
        author.setRole(Role.ROLE_USER);

        //WHEN
        Author authorSaved = authorService.saveAuthor(author);
        Author authorInDb = authorService.findAuthorById(authorSaved.getId()).get();

        //THEN
        assertThat(authorSaved.getId())
                .isNotNull();
        assertThat(authorSaved)
                .isEqualTo(authorInDb);
    }

    @Test
    @Transactional
    void shouldBeAbleToFetchAllAuthorsInDb() {

        //GIVEN
        Set<Author> authors = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Author author = new Author();
            author.setUsername("username" + i);
            author.setPassword("password" + i);
            author.setFirstName("First" + i);
            author.setLastName("Last" + i);
            author.setRole(Role.ROLE_USER);
            authors.add(author);
        }
        authorRepository.saveAll(authors);

        //WHEN
        List<Author> authorsInDb = authorService.findAllAuthors();

        //THEN
        assertThat(new HashSet<>(authorsInDb))
                .isEqualTo(authors);

    }

    @Test
    void shouldBeAbleToDeleteAuthor() {
        //GIVEN
        Author author = new Author();
        author.setId(null);
        author.setFirstName("TestFirstName");
        author.setLastName("TestLastName");
        author.setUsername("TestUsername");
        author.setPassword("TestPassword");
        author.setRole(Role.ROLE_USER);
        Author authorSaved = authorService.saveAuthor(author);

        //WHEN
        authorService.deleteAuthor(authorSaved.getId());

        //THEN
        assertThat(authorRepository.findById(authorSaved.getId()))
                .isEmpty();
    }

    @Test
    void shouldDeleteAllRelatedDocumentOnDeleteAuthor() {
        //GIVEN
        Author author = new Author();
        author.setId(null);
        author.setFirstName("TestFirstName");
        author.setLastName("TestLastName");
        author.setUsername("TestUsername");
        author.setPassword("TestPassword");
        author.setRole(Role.ROLE_USER);
        Author authorSaved = authorService.saveAuthor(author);

        Set<Document> documents = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Document document = new Document(
                    null, "testTitle" + i, "testBody" + i, author.getId(), null
            );

            documents.add(documentService.saveDocument(document));
        }

        //WHEN
        authorService.deleteAuthor(authorSaved.getId());

        //THEN
        assertThat(documentRepository.findAllByAuthorId(authorSaved.getId()))
                .isEmpty();

    }

}
