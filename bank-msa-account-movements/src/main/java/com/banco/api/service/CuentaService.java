package com.banco.api.service;

import com.banco.api.dto.CuentaRequestDTO;
import com.banco.api.dto.CuentaResponseDTO;

import java.util.List;

public interface CuentaService {
    List<CuentaResponseDTO> listarTodas();
    CuentaResponseDTO obtenerPorId(Long id);
    CuentaResponseDTO crear(CuentaRequestDTO dto);
    CuentaResponseDTO actualizar(Long id, CuentaRequestDTO dto);
    void eliminar(Long id);
}
