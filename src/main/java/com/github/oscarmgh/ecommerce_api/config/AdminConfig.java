package com.github.oscarmgh.ecommerce_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Component
@ConfigurationProperties(prefix = "admin")
@Validated
public class AdminConfig {

    @NotBlank(message = "Admin username is required")
    private String username = "admin";

    @NotBlank(message = "Admin email is required")
    @Email(message = "Admin email must be valid")
    private String email = "admin@ecommerce.com";

    @NotBlank(message = "Admin password is required")
    private String password;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
