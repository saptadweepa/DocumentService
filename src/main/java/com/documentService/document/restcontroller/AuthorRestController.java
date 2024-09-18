package com.documentService.document.restcontroller;

import com.documentService.document.model.Author;
import com.documentService.document.model.Role;
import com.documentService.document.restcontroller.dto.AuthorReadDTO;
import com.documentService.document.restcontroller.dto.AuthorWriteDTO;
import com.documentService.document.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/authors")
@AllArgsConstructor
@Tag(name = "Authors", description = "Operations pertaining to authors")
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping
    @Operation(summary = "Get all authors", description = "Retrieve a list of all authors")
    public ResponseEntity<List<AuthorReadDTO>> getAllAuthors() {
        List<AuthorReadDTO> authors = authorService.findAllAuthors()
                .stream()
                .map(this::toReadDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Retrieve an author by their ID")
    @ApiResponse(responseCode = "200", description = "Author found")
    @ApiResponse(responseCode = "404", description = "Author not found")
    public ResponseEntity<AuthorReadDTO> getAuthorById(@PathVariable Long id) {
        Optional<Author> author = authorService.findAuthorById(id);

        return author.map(value -> ResponseEntity.ok(toReadDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new author", description = "Add a new author to the system")
    @ApiResponse(responseCode = "201", description = "Author created")
    @PostMapping
    public ResponseEntity<AuthorReadDTO> createAuthor(@RequestBody @Valid AuthorWriteDTO authorDTO) {

        Author newAuthor = new Author();
        newAuthor.setId(null);
        newAuthor.setFirstName(authorDTO.getFirstName());
        newAuthor.setLastName(authorDTO.getLastName());
        newAuthor.setUsername(authorDTO.getUsername());
        newAuthor.setPassword(authorDTO.getPassword());
        newAuthor.setRole(Role.ROLE_USER);

        Author savedAuthor = authorService.saveAuthor(newAuthor);

        return ResponseEntity.status(HttpStatus.CREATED).body(toReadDTO(savedAuthor));
    }

    @Operation(summary = "Update an author", description = "Update an existing author's details")
    @ApiResponse(responseCode = "200", description = "Author updated")
    @ApiResponse(responseCode = "404", description = "Author not found")
    @PutMapping("/{id}")
    public ResponseEntity<AuthorReadDTO> updateAuthor(@PathVariable Long id, @RequestBody @Valid AuthorWriteDTO authorDTO) {
        Optional<Author> existingAuthor = authorService.findAuthorById(id);

        if (existingAuthor.isPresent()) {
            Author author = existingAuthor.get();
            author.setFirstName(authorDTO.getFirstName());
            author.setLastName(authorDTO.getLastName());
            author.setUsername(authorDTO.getUsername());
            author.setPassword(authorDTO.getPassword());
            Author updatedAuthor = authorService.saveAuthor(author);
            return ResponseEntity.ok(toReadDTO(updatedAuthor));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete an author", description = "Remove an author from the system")
    @ApiResponse(responseCode = "200", description = "Author deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok().build();
    }

    private AuthorReadDTO toReadDTO(Author author) {
        return new AuthorReadDTO(author.getId(), author.getFirstName(), author.getLastName(),
                author.getUsername());
    }

}
