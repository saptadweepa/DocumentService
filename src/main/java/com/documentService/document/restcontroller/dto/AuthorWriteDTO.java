package com.documentService.document.restcontroller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorWriteDTO {

    @NotNull(message = "First name cannot be null")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @NotNull(message = "Username cannot be null")
    @Size(min = 1, max = 50, message = "username must be between 1 and 50 characters")
    private String username;

    @NotNull(message = "password cannot be null")
    @Size(min = 1, max = 50, message = "password must be between 1 and 50 characters")
    private String password;
}
