package com.futura.FuturaFlow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {
    @NotBlank(message = "Email не може бути порожнім")
    @Email(message = "Некоректний формат email адреси")
    private String email;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 6, message = "Пароль має містити мінімум 6 символів")
    private String password;

    public String getEmail () {
        return email;
    }
    public void setEmail (String email){
        this.email=email;
    }

    public String getPassword () {
        return password;
    }
    public void setPassword (String password) {
        this.password=password;
    }



}
