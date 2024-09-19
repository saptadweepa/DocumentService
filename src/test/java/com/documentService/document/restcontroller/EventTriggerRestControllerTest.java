package com.documentService.document.restcontroller;

import com.documentService.document.model.Author;
import com.documentService.document.model.Document;
import com.documentService.document.model.Role;
import com.documentService.document.repository.AuthorRepository;
import com.documentService.document.repository.DocumentRepository;
import com.documentService.document.restcontroller.dto.AuthorWriteDTO;
import com.documentService.document.restcontroller.dto.DocumentDTO;
import com.documentService.document.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class EventTriggerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE author CASCADE");
    }

    @Test
    public void testCreateAuthor() throws Exception {
        String username = "johndoe";
        AuthorWriteDTO dto = new AuthorWriteDTO("John", "Doe", username, "password123");

        mockMvc.perform(post("/api/v1/event-trigger/create-author")
                        .with(user("testadmin").password("testadminpass").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Awaitility
                .await()
                .atMost(3, TimeUnit.SECONDS)
                .until(() -> authorRepository.findByUsername(username).isPresent());

    }

    @Test
    public void testCreateDocument() throws Exception {
        Author author = new Author();
        author.setId(null);
        author.setFirstName("firstname");
        author.setLastName("lastname");
        author.setUsername("username");
        author.setPassword("password");
        author.setRole(Role.ROLE_USER);
        Author savedAuthor = authorRepository.save(author);

        DocumentDTO dto = new DocumentDTO(null, "Title", "Body", savedAuthor.getId(), new HashSet<>());


        mockMvc.perform(post("/api/v1/event-trigger/create-document")
                        .with(user("testadmin").password("testadminpass").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Awaitility
                .await()
                .atMost(3, TimeUnit.SECONDS)
                .until(() -> !documentRepository.findAllByAuthorId(savedAuthor.getId()).isEmpty());

    }

    @Test
    public void testDeleteAuthor() throws Exception {
        Author author = new Author();
        author.setId(null);
        author.setFirstName("firstname");
        author.setLastName("lastname");
        author.setUsername("username");
        author.setPassword("password");
        author.setRole(Role.ROLE_USER);
        Author savedAuthor = authorRepository.save(author);

        mockMvc.perform(delete("/api/v1/event-trigger/delete-author/{authorId}", savedAuthor.getId())
                        .with(user("testadmin").password("testadminpass").roles("ADMIN"))
                )
                .andExpect(status().isOk());

        Awaitility
                .await()
                .atMost(3, TimeUnit.SECONDS)
                .until(() -> authorRepository.findAll().isEmpty());

    }

    @Test
    public void testDeleteDocument() throws Exception {
        Author author = new Author();
        author.setId(null);
        author.setFirstName("firstname");
        author.setLastName("lastname");
        author.setUsername("username");
        author.setPassword("password");
        author.setRole(Role.ROLE_USER);
        Author savedAuthor = authorRepository.save(author);
        Document newDocument = new Document(null, "Another Title", "Another Body",
                savedAuthor.getId(), new HashSet<>());
        newDocument = documentService.saveDocument(newDocument);

        mockMvc.perform(delete("/api/v1/event-trigger/delete-document/{documentId}", newDocument.getId())
                        .with(user("testadmin").password("testadminpass").roles("ADMIN"))
                )
                .andExpect(status().isOk());

        Awaitility
                .await()
                .atMost(3, TimeUnit.SECONDS)
                .until(() -> documentRepository.findAll().isEmpty());

    }
}
