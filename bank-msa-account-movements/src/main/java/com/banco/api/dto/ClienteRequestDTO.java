package com.banco.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String genero;

    @Min(value = 0, message = "La edad debe ser positiva")
    private Integer edad;

    @NotBlank(message = "La identificación es obligatoria")
    private String identificacion;

    private String direccion;
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private Boolean estado = true;
}
