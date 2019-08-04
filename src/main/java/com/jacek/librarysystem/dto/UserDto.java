package com.jacek.librarysystem.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    @NotBlank
    private String username;

    @Length(min=8, max=30)
    private String password;

    @Email
    private String email;

}
