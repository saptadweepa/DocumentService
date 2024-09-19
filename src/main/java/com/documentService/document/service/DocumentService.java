package com.documentService.document.service;

import com.documentService.document.model.Author;
import com.documentService.document.model.Document;
import com.documentService.document.repository.AuthorRepository;
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
    private final AuthorRepository authorRepository;

    public List<Document> findAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<Document> findDocumentById(Long id) {
        Objects.requireNonNull(id, "Document ID must not be null");
        return documentRepository.findById(id);
    }

    public Document saveDocument(Document document) {
        Objects.requireNonNull(document, "saved document must not be null");
        Optional<Author> authorOpt = authorRepository.findById(document.getAuthorId());

        if (authorOpt.isEmpty()){
            throw new IllegalStateException("Author must exist before creating/updating a new document");
        }

        Document savedDoc = documentRepository.save(document);

        Author author = authorOpt.get();
        author.addDocument(savedDoc);
        authorRepository.save(author);

        return savedDoc;
    }

    public void deleteDocument(Long id) {
        Objects.requireNonNull(id, "Document ID must not be null");

        Document doc = documentRepository.findById(id).orElseThrow();
        Author author = authorRepository.findById(doc.getAuthorId()).orElseThrow();

        author.removeDocument(doc);
        authorRepository.save(author);

        documentRepository.deleteById(id);
    }

    public List<Document> findAllDocumentForAuthor(Author author) {

        Objects.requireNonNull(author, "Author must not be null");
        Objects.requireNonNull(author.getId(), "Author id must not be null");

        return documentRepository.findAllByAuthorId(author.getId());

    }
}
