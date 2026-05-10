package com.banco.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Persona {

    @Column(name = "cliente_id", unique = true, nullable = false)
    private String clienteId;

    @Column(length = 200)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private String password;
}
