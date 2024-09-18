package com.documentService.document.restcontroller;

import com.documentService.document.TestSecurityConfig;
import com.documentService.document.model.Author;
import com.documentService.document.model.Document;
import com.documentService.document.model.Role;
import com.documentService.document.repository.AuthorRepository;
import com.documentService.document.repository.DocumentRepository;
import com.documentService.document.restcontroller.dto.DocumentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TestSecurityConfig.class})
public class DocumentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        documentRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    public void shouldCreateDocument() throws Exception {
        Author author = new Author(null, "firstname", "lastname",
                "username", "password", Role.ROLE_USER, null);
        Author savedAuthor = authorRepository.save(author);

        DocumentDTO documentDTO = new DocumentDTO(null, "Test Title", "Test Body",
                Set.of(savedAuthor.getId()), Collections.emptySet());

        mockMvc.perform(post("/api/v1/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.body").value("Test Body"))
                .andExpect(jsonPath("$.authorIds[0]").value(savedAuthor.getId()));
    }

    @Test
    public void shouldGetDocumentById() throws Exception {
        Author author = new Author(null, "firstname", "lastname", "username", "password", Role.ROLE_USER, null);
        Author savedAuthor = authorRepository.save(author);

        Document document = new Document(null, "Test Title", "Test Body",
                Set.of(savedAuthor), Collections.emptySet());
        Document savedDocument = documentRepository.save(document);

        mockMvc.perform(get("/api/v1/documents/" + savedDocument.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.body").value("Test Body"))
                .andExpect(jsonPath("$.authorIds[0]").value(savedAuthor.getId()));
    }

    @Test
    public void shouldGetAllDocuments() throws Exception {
        Author author1 = new Author(null, "firstname", "lastname", "username",
                "password", Role.ROLE_USER, null);
        Author author2 = new Author(null, "firstname2", "lastname2", "username2",
                "password", Role.ROLE_USER, null);
        authorRepository.saveAll(List.of(author1, author2));

        Document doc1 = new Document(null, "Title 1", "Body 1", Set.of(author1), Collections.emptySet());
        Document doc2 = new Document(null, "Title 2", "Body 2", Set.of(author2), Collections.emptySet());
        documentRepository.saveAll(List.of(doc1, doc2));

        mockMvc.perform(get("/api/v1/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[1].title").value("Title 2"));
    }

    @Test
    public void shouldUpdateDocument() throws Exception {
        Author author = new Author(null, "firstname", "lastname",
                "username", "password", Role.ROLE_USER, null);
        Author savedAuthor = authorRepository.save(author);

        Document document = new Document(null, "Old Title", "Old Body",
                Set.of(savedAuthor), Collections.emptySet());
        Document savedDocument = documentRepository.save(document);

        DocumentDTO updatedDocumentDTO = new DocumentDTO(savedDocument.getId(), "Updated Title", "Updated Body",
                Set.of(savedAuthor.getId()), Collections.emptySet());

        mockMvc.perform(put("/api/v1/documents/" + savedDocument.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDocumentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.body").value("Updated Body"));
    }

    @Test
    public void shouldDeleteDocument() throws Exception {
        Author author = new Author(null, "firstname", "lastname", "username", "password", Role.ROLE_USER, null);
        Author savedAuthor = authorRepository.save(author);

        Document document = new Document(null, "Title", "Body",
                Set.of(savedAuthor), Collections.emptySet());
        Document savedDocument = documentRepository.save(document);

        mockMvc.perform(delete("/api/v1/documents/" + savedDocument.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/documents/" + savedDocument.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnBadRequestForInvalidDocument() throws Exception {
        DocumentDTO invalidDocumentDTO = new DocumentDTO(null, null, null, null, Collections.emptySet());

        mockMvc.perform(post("/api/v1/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDocumentDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title cannot be null"))
                .andExpect(jsonPath("$.body").value("Body cannot be null"))
                .andExpect(jsonPath("$.authorIds").value("Author IDs cannot be null"));
    }

    @Test
    public void shouldReturnBadRequestForInvalidAuthorId() throws Exception {
        DocumentDTO documentDTO = new DocumentDTO(null, "Title", "Body",
                Set.of(999L), Collections.emptySet());

        mockMvc.perform(post("/api/v1/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnNotFoundForDocumentById() throws Exception {
        long invalidId = 999L;

        mockMvc.perform(get("/api/v1/documents/" + invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnBadRequestWhenAuthorNotFoundForCreate() throws Exception {
        DocumentDTO documentDTO = new DocumentDTO(null, "Test Title", "Test Body",
                Set.of(999L), Collections.emptySet());

        mockMvc.perform(post("/api/v1/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenAuthorNotFoundForUpdate() throws Exception {
        Author author = new Author(null, "firstname", "lastname",
                "username", "password", Role.ROLE_USER, null);
        Author savedAuthor = authorRepository.save(author);

        Document document = new Document(null, "Test Title", "Test Body",
                Set.of(savedAuthor), Collections.emptySet());
        Document savedDocument = documentRepository.save(document);

        DocumentDTO documentDTO = new DocumentDTO(savedDocument.getId(), "Updated Title", "Updated Body",
                Set.of(999L), Collections.emptySet());

        mockMvc.perform(put("/api/v1/documents/" + savedDocument.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnNotFoundForUpdateWhenDocumentNotFound() throws Exception {
        long invalidId = 999L;

        DocumentDTO documentDTO = new DocumentDTO(null, "Updated Title", "Updated Body",
                Collections.emptySet(), Collections.emptySet());

        mockMvc.perform(put("/api/v1/documents/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentDTO)))
                .andExpect(status().isNotFound());
    }
}

