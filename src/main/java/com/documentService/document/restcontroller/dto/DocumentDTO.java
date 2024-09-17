package com.documentService.document.restcontroller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {

    private Long id;

    @NotNull(message = "Title cannot be null")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @NotNull(message = "Body cannot be null")
    private String body;

    @NotNull(message = "Author IDs cannot be null")
    private Set<Long> authorIds;

    private Set<Long> referenceIds;
}

