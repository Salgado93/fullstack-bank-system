package com.banco.api.controller;

import com.banco.api.dto.ReporteResponseDTO;
import com.banco.api.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Generación de reportes de movimientos")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    @Operation(summary = "Generar reporte de movimientos por cliente y rango de fechas")
    public ResponseEntity<ReporteResponseDTO> generarReporte(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(reporteService.generarReporte(clienteId, inicio, fin));
    }
}
