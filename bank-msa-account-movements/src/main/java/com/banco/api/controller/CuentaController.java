package com.banco.api.controller;

import com.banco.api.dto.CuentaRequestDTO;
import com.banco.api.dto.CuentaResponseDTO;
import com.banco.api.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "CRUD de cuentas bancarias")
public class CuentaController {

    private final CuentaService cuentaService;

    @GetMapping
    @Operation(summary = "Listar todas las cuentas")
    public ResponseEntity<List<CuentaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(cuentaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID")
    public ResponseEntity<CuentaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva cuenta")
    public ResponseEntity<CuentaResponseDTO> crear(@Valid @RequestBody CuentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una cuenta existente")
    public ResponseEntity<CuentaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CuentaRequestDTO dto) {
        return ResponseEntity.ok(cuentaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cuenta")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cuentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
