package com.documentService.document.restcontroller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorReadDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;

}
