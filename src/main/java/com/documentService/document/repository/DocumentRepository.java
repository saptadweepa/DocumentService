package com.documentService.document.repository;

import com.documentService.document.model.Author;
import com.documentService.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByAuthor(Author author);

}