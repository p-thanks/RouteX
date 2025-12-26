package com.delivery.RouteX.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+~-]).*$")
    private String password;

    @NotBlank @Pattern(regexp = "^[0-9]{10,15}$")
    private String phone;

    @NotBlank
    private String role;

    private String vehicleType;
    private String vehiclePlate;
    private String licenseNumber;
}