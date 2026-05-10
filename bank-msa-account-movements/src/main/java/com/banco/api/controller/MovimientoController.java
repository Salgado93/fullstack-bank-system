package com.banco.api.controller;

import com.banco.api.dto.MovimientoRequestDTO;
import com.banco.api.dto.MovimientoResponseDTO;
import com.banco.api.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Gestión de movimientos bancarios")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    @Operation(summary = "Listar todos los movimientos")
    public ResponseEntity<List<MovimientoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por ID")
    public ResponseEntity<MovimientoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo movimiento (aplica reglas de negocio)")
    public ResponseEntity<MovimientoResponseDTO> crear(@Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un movimiento existente")
    public ResponseEntity<MovimientoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.ok(movimientoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un movimiento")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
