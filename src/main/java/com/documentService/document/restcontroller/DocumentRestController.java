package com.documentService.document.restcontroller;

import com.documentService.document.model.Document;
import com.documentService.document.model.Author;
import com.documentService.document.restcontroller.dto.DocumentDTO;
import com.documentService.document.service.DocumentService;
import com.documentService.document.service.AuthorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/documents")
@AllArgsConstructor
public class DocumentRestController {

    private final DocumentService documentService;
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        List<DocumentDTO> documents = documentService.findAllDocuments()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        Optional<Document> document = documentService.findDocumentById(id);
        return document.map(value -> ResponseEntity.ok(toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DocumentDTO> createDocument(@Valid @RequestBody DocumentDTO documentDTO) {
        Set<Author> authors = getAuthorsByIds(documentDTO.getAuthorIds());
        if (authors.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Set<Document> references = getDocumentsByIds(documentDTO.getReferenceIds());

        Document document = new Document(null, documentDTO.getTitle(), documentDTO.getBody(), authors, references);
        Document savedDocument = documentService.saveDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedDocument));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(@PathVariable Long id, @Valid @RequestBody DocumentDTO documentDTO) {
        Optional<Document> existingDocument = documentService.findDocumentById(id);

        if (existingDocument.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Document document = existingDocument.get();
        Set<Author> authors = getAuthorsByIds(documentDTO.getAuthorIds());
        if (authors.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Set<Document> references = getDocumentsByIds(documentDTO.getReferenceIds());

        document.setTitle(documentDTO.getTitle());
        document.setBody(documentDTO.getBody());
        document.setAuthors(authors);
        document.setReferences(references);

        Document updatedDocument = documentService.saveDocument(document);
        return ResponseEntity.ok(toDTO(updatedDocument));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }


    private DocumentDTO toDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setBody(document.getBody());
        dto.setAuthorIds(document.getAuthors().stream().map(Author::getId).collect(Collectors.toSet()));
        dto.setReferenceIds(document.getReferences().stream().map(Document::getId).collect(Collectors.toSet()));
        return dto;
    }

    private Set<Author> getAuthorsByIds(Set<Long> authorIds) {
        return authorIds.stream()
                .map(authorId -> authorService.findAuthorById(authorId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<Document> getDocumentsByIds(Set<Long> documentIds) {
        return documentIds.stream()
                .map(documentId -> documentService.findDocumentById(documentId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}

