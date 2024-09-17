package com.documentService.document.restcontroller;

import com.documentService.document.TestSecurityConfig;
import com.documentService.document.model.Author;
import com.documentService.document.repository.AuthorRepository;
import com.documentService.document.restcontroller.dto.AuthorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TestSecurityConfig.class})
public class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        authorRepository.deleteAll(); 
    }

    @Test
    public void shouldCreateAuthor() throws Exception {
        AuthorDTO authorDTO = new AuthorDTO(null, "firstname", "lastname");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("firstname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("lastname"));
    }

    @Test
    public void shouldGetAuthorById() throws Exception {
        Author author = new Author(null, "firstname", "lastname");
        Author savedAuthor = authorRepository.save(author);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/authors/" + savedAuthor.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("firstname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("lastname"));
    }

    @Test
    public void shouldGetAllAuthors() throws Exception {
        Author author1 = new Author(null, "firstname", "lastname");
        Author author2 = new Author(null, "firstname2", "lastname2");
        authorRepository.save(author1);
        authorRepository.save(author2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/authors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("firstname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("firstname2"));
    }

    @Test
    public void shouldUpdateAuthor() throws Exception {
        Author author = new Author(null, "firstname", "old lastname");
        Author savedAuthor = authorRepository.save(author);

        AuthorDTO updatedAuthorDTO = new AuthorDTO(savedAuthor.getId(), "firstname", "lastname Updated");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("lastname Updated"));
    }

    @Test
    public void shouldDeleteAuthor() throws Exception {
        Author author = new Author(null, "firstname", "lastname");
        Author savedAuthor = authorRepository.save(author);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/authors/" + savedAuthor.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/authors/" + savedAuthor.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldFailValidationWhenFirstNameIsNull() throws Exception {
        AuthorDTO invalidAuthorDTO = new AuthorDTO(null, null, "lastname");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First name cannot be null"));
    }

    @Test
    public void shouldFailValidationWhenFirstNameIsTooShort() throws Exception {
        AuthorDTO invalidAuthorDTO = new AuthorDTO(null, "", "lastname");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First name must be between 1 and 50 characters"));
    }

    @Test
    public void shouldFailValidationWhenLastNameIsNull() throws Exception {
        AuthorDTO invalidAuthorDTO = new AuthorDTO(null, "firstname", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Last name cannot be null"));
    }

    @Test
    public void shouldFailValidationWhenLastNameIsTooShort() throws Exception {
        AuthorDTO invalidAuthorDTO = new AuthorDTO(null, "firstname", "");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Last name must be between 1 and 50 characters"));
    }

    @Test
    public void shouldFailValidationWhenBothNamesAreInvalid() throws Exception {
        AuthorDTO invalidAuthorDTO = new AuthorDTO(null, "", "");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First name must be between 1 and 50 characters"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Last name must be between 1 and 50 characters"));
    }

    @Test
    public void shouldFailValidationWhenFirstNameIsTooLong() throws Exception {
        String longFirstName = "a".repeat(51);

        AuthorDTO invalidAuthorDTO = new AuthorDTO(null, longFirstName, "lastname");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First name must be between 1 and 50 characters"));
    }

}
