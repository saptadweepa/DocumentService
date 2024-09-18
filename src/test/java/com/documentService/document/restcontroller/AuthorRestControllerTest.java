package com.documentService.document.restcontroller;

import com.documentService.document.TestSecurityConfig;
import com.documentService.document.model.Author;
import com.documentService.document.model.Role;
import com.documentService.document.repository.AuthorRepository;
import com.documentService.document.restcontroller.dto.AuthorWriteDTO;
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
        AuthorWriteDTO authorDTO = new AuthorWriteDTO("firstname", "lastname", "username", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("firstname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("lastname"));
    }

    @Test
    public void shouldGetAuthorById() throws Exception {
        Author author = new Author();
        author.setId(null);
        author.setFirstName("firstname");
        author.setLastName("lastname");
        author.setUsername("username");
        author.setPassword("password");
        author.setRole(Role.ROLE_USER);
        Author savedAuthor = authorRepository.save(author);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/authors/" + savedAuthor.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("firstname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("lastname"));
    }

    @Test
    public void shouldGetAllAuthors() throws Exception {
        Author author1 = new Author();
        author1.setId(null);
        author1.setFirstName("firstname");
        author1.setLastName("lastname");
        author1.setUsername("username");
        author1.setPassword("password");
        author1.setRole(Role.ROLE_USER);

        Author author2 = new Author();
        author2.setId(null);
        author2.setFirstName("firstname2");
        author2.setLastName("lastname2");
        author2.setUsername("username2");
        author2.setPassword("password2");
        author2.setRole(Role.ROLE_USER);

        authorRepository.save(author1);
        authorRepository.save(author2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/authors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("firstname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("firstname2"));
    }

    @Test
    public void shouldUpdateAuthor() throws Exception {
        Author author = new Author();
        author.setId(null);
        author.setFirstName("firstname");
        author.setLastName("old lastname");
        author.setUsername("username");
        author.setPassword("password");
        author.setRole(Role.ROLE_USER);
        Author savedAuthor = authorRepository.save(author);

        AuthorWriteDTO updatedAuthorDTO = new AuthorWriteDTO( "firstname", "lastname Updated", "username", "password");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("lastname Updated"));
    }

    @Test
    public void shouldDeleteAuthor() throws Exception {
        Author author = new Author();
        author.setId(null);
        author.setFirstName("firstname");
        author.setLastName("lastname");
        author.setUsername("username");
        author.setPassword("password");
        author.setRole(Role.ROLE_USER);
        Author savedAuthor = authorRepository.save(author);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/authors/" + savedAuthor.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/authors/" + savedAuthor.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldFailValidationWhenFirstNameIsNull() throws Exception {
        AuthorWriteDTO invalidAuthorDTO = new AuthorWriteDTO(null, "lastname", "username", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First name cannot be null"));
    }

    @Test
    public void shouldFailValidationWhenFirstNameIsTooShort() throws Exception {
        AuthorWriteDTO invalidAuthorDTO = new AuthorWriteDTO("", "lastname", "username", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First name must be between 1 and 50 characters"));
    }

    @Test
    public void shouldFailValidationWhenLastNameIsNull() throws Exception {
        AuthorWriteDTO invalidAuthorDTO = new AuthorWriteDTO( "firstname", null, "username", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Last name cannot be null"));
    }

    @Test
    public void shouldFailValidationWhenLastNameIsTooShort() throws Exception {
        AuthorWriteDTO invalidAuthorDTO = new AuthorWriteDTO("firstname", "", "username", "password");


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Last name must be between 1 and 50 characters"));
    }

    @Test
    public void shouldFailValidationWhenBothNamesAreInvalid() throws Exception {
        AuthorWriteDTO invalidAuthorDTO = new AuthorWriteDTO("", "", "username", "password");

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

        AuthorWriteDTO invalidAuthorDTO = new AuthorWriteDTO(longFirstName, "lastname", "username", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First name must be between 1 and 50 characters"));
    }

}
