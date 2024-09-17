package com.documentService.document.repository;

import com.documentService.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("SELECT d FROM Document d JOIN d.authors a WHERE a.id = :authorId")
    List<Document> findAllByAuthorId(@Param("authorId") Long authorId);

}