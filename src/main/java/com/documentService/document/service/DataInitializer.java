package com.documentService.document.service;


import com.documentService.document.model.Author;
import com.documentService.document.model.Role;
import com.documentService.document.repository.AuthorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * Since the service is secured, this class creates two demo users which can be used for
 * accessing rest apis.
 * Two kind of users are create
 * 1. Admin -> has all access
 * 2. User -> has limited access
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(AuthorRepository authorRepository, PasswordEncoder passwordEncoder) {
        return args -> {

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

        };
    }
}
