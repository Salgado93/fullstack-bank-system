package com.banco.api.service;

import com.banco.api.dto.MovimientoRequestDTO;
import com.banco.api.dto.MovimientoResponseDTO;

import java.util.List;

public interface MovimientoService {
    List<MovimientoResponseDTO> listarTodos();
    MovimientoResponseDTO obtenerPorId(Long id);
    MovimientoResponseDTO crear(MovimientoRequestDTO dto);
    MovimientoResponseDTO actualizar(Long id, MovimientoRequestDTO dto);
    void eliminar(Long id);
}
