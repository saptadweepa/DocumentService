package com.documentService.document.restcontroller;

import com.documentService.document.model.Document;
import com.documentService.document.model.Author;
import com.documentService.document.restcontroller.dto.DocumentDTO;
import com.documentService.document.service.DocumentService;
import com.documentService.document.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Documents", description = "Operations pertaining to documents")
public class DocumentRestController {

    private final DocumentService documentService;
    private final AuthorService authorService;

    @GetMapping
    @Operation(summary = "Get all documents", description = "Retrieve a list of all documents")
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        List<DocumentDTO> documents = documentService.findAllDocuments()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Retrieve a document by its ID")
    @ApiResponse(responseCode = "200", description = "Document found")
    @ApiResponse(responseCode = "404", description = "Document not found")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        Optional<Document> document = documentService.findDocumentById(id);
        return document.map(value -> ResponseEntity.ok(toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new document", description = "Add a new document to the system")
    @ApiResponse(responseCode = "201", description = "Document created")
    @ApiResponse(responseCode = "400", description = "Bad request if authors are not found")
    public ResponseEntity<DocumentDTO> createDocument(@Valid @RequestBody DocumentDTO documentDTO) {
        Optional<Author> authorOpt = authorService.findAuthorById(documentDTO.getAuthorId());

        if (authorOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Author author = authorOpt.get();

        Document document = new Document();
        document.setId(null);
        document.setTitle(documentDTO.getTitle());
        document.setBody(documentDTO.getBody());
        document.setAuthorId(author.getId());
        document.setReferenceIds(documentDTO.getReferenceIds());

        Document savedDocument = documentService.saveDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedDocument));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing document", description = "Update details of an existing document")
    @ApiResponse(responseCode = "200", description = "Document updated")
    @ApiResponse(responseCode = "400", description = "Bad request if authors are not found")
    @ApiResponse(responseCode = "404", description = "Document not found")
    public ResponseEntity<DocumentDTO> updateDocument(@PathVariable("id") Long id, @Valid @RequestBody DocumentDTO documentDTO) {
        Optional<Document> existingDocument = documentService.findDocumentById(id);

        if (existingDocument.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Document document = existingDocument.get();
        Optional<Author> author = authorService.findAuthorById(documentDTO.getAuthorId());
        if (author.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        document.setTitle(documentDTO.getTitle());
        document.setBody(documentDTO.getBody());
        document.setAuthorId(author.get().getId());
        document.setReferenceIds(documentDTO.getReferenceIds());

        Document updatedDocument = documentService.saveDocument(document);
        return ResponseEntity.ok(toDTO(updatedDocument));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a document", description = "Remove a document from the system")
    @ApiResponse(responseCode = "200", description = "Document deleted")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }


    private DocumentDTO toDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setBody(document.getBody());
        dto.setAuthorId(document.getAuthorId());
        dto.setReferenceIds(document.getReferenceIds());
        return dto;
    }

}

