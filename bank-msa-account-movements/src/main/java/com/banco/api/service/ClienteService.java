package com.banco.api.service;

import com.banco.api.dto.ClienteRequestDTO;
import com.banco.api.dto.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {
    List<ClienteResponseDTO> listarTodos();
    ClienteResponseDTO obtenerPorId(Long id);
    ClienteResponseDTO crear(ClienteRequestDTO dto);
    ClienteResponseDTO actualizar(Long id, ClienteRequestDTO dto);
    void eliminar(Long id);
}
