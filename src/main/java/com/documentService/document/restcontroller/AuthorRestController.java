package com.documentService.document.restcontroller;

import com.documentService.document.model.Author;
import com.documentService.document.restcontroller.dto.AuthorDTO;
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
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<AuthorDTO> authors = authorService.findAllAuthors()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Retrieve an author by their ID")
    @ApiResponse(responseCode = "200", description = "Author found")
    @ApiResponse(responseCode = "404", description = "Author not found")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        Optional<Author> author = authorService.findAuthorById(id);

        return author.map(value -> ResponseEntity.ok(toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new author", description = "Add a new author to the system")
    @ApiResponse(responseCode = "201", description = "Author created")
    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody @Valid AuthorDTO authorDTO) {

        Author newAuthor = new Author(null, authorDTO.getFirstName(), authorDTO.getLastName());

        Author savedAuthor = authorService.saveAuthor(newAuthor);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedAuthor));
    }

    @Operation(summary = "Update an author", description = "Update an existing author's details")
    @ApiResponse(responseCode = "200", description = "Author updated")
    @ApiResponse(responseCode = "404", description = "Author not found")
    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @RequestBody @Valid AuthorDTO authorDTO) {
        Optional<Author> existingAuthor = authorService.findAuthorById(id);

        if (existingAuthor.isPresent()) {
            Author author = existingAuthor.get();
            author.setFirstName(authorDTO.getFirstName());
            author.setLastName(authorDTO.getLastName());
            Author updatedAuthor = authorService.saveAuthor(author);
            return ResponseEntity.ok(toDTO(updatedAuthor));
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

    private AuthorDTO toDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getFirstName(), author.getLastName());
    }

}
