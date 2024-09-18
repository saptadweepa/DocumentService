package com.documentService.document.service;

import com.documentService.document.model.Author;
import com.documentService.document.model.Document;
import com.documentService.document.repository.AuthorRepository;
import com.documentService.document.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final DocumentRepository documentRepository;

    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    public Optional<Author> findAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    @Transactional
    public Author saveAuthor(Author author) {

        if (author.getId() != null){
            Author existingAuthor = authorRepository.findById(author.getId()).orElseThrow();
            author.setDocuments(existingAuthor.getDocuments());
        }

        return authorRepository.save(author);
    }

    @Transactional
    public void deleteAuthor(Long id) {

        Author author = authorRepository.findById(id).orElseThrow();

        Set<Document> documents = author.getDocuments();

        documents.forEach(
                it -> it.removeAuthor(author)
        );

        author.setDocuments(null);
        authorRepository.save(author);
        Set<Document> docsWithoutAnyAuthor = documents.stream().filter(
                it -> it.getAuthors().isEmpty()
        ).collect(Collectors.toSet());

        Set<Document> docsToBeUpdated = documents.stream().filter(
                it -> !docsWithoutAnyAuthor.contains(it)
        ).collect(Collectors.toSet());

        docsWithoutAnyAuthor.forEach(documentRepository::delete);
        documentRepository.saveAll(docsToBeUpdated);

        authorRepository.deleteById(id);
    }
}