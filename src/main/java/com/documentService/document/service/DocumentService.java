package com.documentService.document.service;

import com.documentService.document.model.Author;
import com.documentService.document.model.Document;
import com.documentService.document.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    public List<Document> findAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<Document> findDocumentById(Long id) {
        Objects.requireNonNull(id, "Document ID must not be null");
        return documentRepository.findById(id);
    }

    public Document saveDocument(Document document) {
        Objects.requireNonNull(document, "saved document must not be null");
        return documentRepository.save(document);
    }

    public void deleteDocument(Long id) {
        Objects.requireNonNull(id, "Document ID must not be null");
        documentRepository.deleteById(id);
    }

    public List<Document> findAllDocumentForAuthor(Author author) {

        Objects.requireNonNull(author, "Author must not be null");
        Objects.requireNonNull(author.getId(), "Author id must not be null");

        return documentRepository.findAllByAuthorId(author.getId());

    }
}
