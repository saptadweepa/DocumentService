package com.documentService.document.restcontroller;
import com.documentService.document.model.Author;
import com.documentService.document.restcontroller.dto.AuthorDTO;
import com.documentService.document.service.AuthorService;
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
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<AuthorDTO> authors = authorService.findAllAuthors()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        Optional<Author> author = authorService.findAuthorById(id);

        return author.map(value -> ResponseEntity.ok(toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody @Valid AuthorDTO authorDTO) {

        Author newAuthor = new Author(null, authorDTO.getFirstName(), authorDTO.getLastName());

        Author savedAuthor = authorService.saveAuthor(newAuthor);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedAuthor));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok().build();
    }

    private AuthorDTO toDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getFirstName(), author.getLastName());
    }

}
