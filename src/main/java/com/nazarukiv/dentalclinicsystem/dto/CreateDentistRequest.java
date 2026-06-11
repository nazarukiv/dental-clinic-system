package com.nazarukiv.dentalclinicsystem.dto;

import com.nazarukiv.dentalclinicsystem.entity.Specialisation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateDentistRequest {

    @NotBlank(message = "must not be blank")
    @Size(max = 50, message = "must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "must not be blank")
    @Size(max = 50, message = "must be at most 50 characters")
    private String lastName;

    @NotNull(message = "must not be null")
    private Specialisation specialisation;

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

    public Specialisation getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(Specialisation specialisation) {
        this.specialisation = specialisation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
