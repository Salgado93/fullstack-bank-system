package com.banco.api.repository;

import com.banco.api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByIdentificacion(String identificacion);
    Optional<Cliente> findByClienteId(String clienteId);
    boolean existsByIdentificacion(String identificacion);
}
