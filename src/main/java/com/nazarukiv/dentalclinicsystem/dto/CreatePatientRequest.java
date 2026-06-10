package com.nazarukiv.dentalclinicsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreatePatientRequest {

    @NotBlank(message = "must not be blank")
    @Size(max = 50, message = "must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "must not be blank")
    @Size(max = 50, message = "must be at most 50 characters")
    private String lastName;

    @NotBlank(message = "must not be blank")
    @Size(max = 20, message = "must be at most 20 characters")
    @Pattern(regexp = "^\\+?[0-9 .()-]{7,20}$", message = "must be a valid phone number")
    private String phoneNumber;

    @NotBlank(message = "must not be blank")
    @Email(message = "must be a valid email address")
    @Size(max = 100, message = "must be at most 100 characters")
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
