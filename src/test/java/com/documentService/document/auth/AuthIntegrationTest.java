package com.documentService.document.auth;

import com.documentService.document.model.Author;
import com.documentService.document.model.Role;
import com.documentService.document.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        restTemplate = new RestTemplate();

        Optional<Author> adminOpt = authorRepository.findByUsername("admin");
        Optional<Author> userOpt = authorRepository.findByUsername("user");

        if (adminOpt.isEmpty()){
            // create Admin if not present
            Author admin = new Author();
            admin.setFirstName("AdminFirstName");
            admin.setLastName("AdminLastName");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminpassword"));
            admin.setRole(Role.ROLE_ADMIN);
            authorRepository.save(admin);
        }

        if (userOpt.isEmpty()){
            //create User if not present
            Author user = new Author();
            user.setFirstName("UserFirstName");
            user.setLastName("UserLastName");
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("userpassword"));
            user.setRole(Role.ROLE_USER);
            authorRepository.save(user);
        }

    }

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    private HttpHeaders createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }

    @Test
    public void testAdminAccess() {
        HttpHeaders headers = createHeaders("admin", "adminpassword");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/v1/authors",
                HttpMethod.GET,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUserAccess() {
        HttpHeaders headers = createHeaders("user", "userpassword");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/v1/documents",
                HttpMethod.GET,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUserAccessToAuthorsIsForbidden() {
        HttpHeaders headers = createHeaders("user", "userpassword");
        HttpEntity<String> entity = new HttpEntity<>(headers);


        assertThrows(HttpClientErrorException.Forbidden.class, () -> {
                    ResponseEntity<String> response = restTemplate.exchange(
                            getBaseUrl() + "/api/v1/authors",
                            HttpMethod.GET,
                            entity,
                            String.class
                    );
                }
        );

    }

    @Test
    public void testWrongPasswordIsUnauthorized() {
        HttpHeaders headers = createHeaders("user", "12352315");
        HttpEntity<String> entity = new HttpEntity<>(headers);


        assertThrows(HttpClientErrorException.Unauthorized.class, () -> {
                    ResponseEntity<String> response = restTemplate.exchange(
                            getBaseUrl() + "/api/v1/authors",
                            HttpMethod.GET,
                            entity,
                            String.class
                    );
                }
        );


    }
}
