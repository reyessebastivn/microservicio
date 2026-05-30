package com.example.ProductosJSS.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {

    @Email
    private String email; // opcional en tu flujo, pero válido si lo envías

    @NotEmpty
    private List<CheckoutItemDTO> items;
}
