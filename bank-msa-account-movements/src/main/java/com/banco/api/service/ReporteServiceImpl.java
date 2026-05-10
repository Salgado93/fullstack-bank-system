package com.banco.api.service;

import com.banco.api.dto.ReporteMovimientoDTO;
import com.banco.api.dto.ReporteResponseDTO;
import com.banco.api.exception.RecursoNoEncontradoException;
import com.banco.api.model.Cliente;
import com.banco.api.model.Movimiento;
import com.banco.api.model.TipoMovimiento;
import com.banco.api.repository.ClienteRepository;
import com.banco.api.repository.MovimientoRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final MovimientoRepository movimientoRepository;
    private final ClienteRepository clienteRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    @Transactional(readOnly = true)
    public ReporteResponseDTO generarReporte(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + clienteId));

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Movimiento> movimientos = movimientoRepository.findByClienteAndFechaRange(clienteId, inicio, fin);

        List<ReporteMovimientoDTO> detalles = movimientos.stream()
                .map(m -> ReporteMovimientoDTO.builder()
                        .fecha(m.getFecha().format(DATETIME_FMT))
                        .cliente(cliente.getNombre())
                        .numeroCuenta(m.getCuenta().getNumeroCuenta())
                        .tipoCuenta(m.getCuenta().getTipoCuenta().name())
                        .saldoInicial(m.getCuenta().getSaldoInicial())
                        .movimiento(m.getTipoMovimiento() == TipoMovimiento.DEBITO
                                ? m.getValor().negate() : m.getValor())
                        .saldoDisponible(m.getSaldoDisponible())
                        .build())
                .toList();

        BigDecimal totalCreditos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == TipoMovimiento.CREDITO)
                .map(Movimiento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebitos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == TipoMovimiento.DEBITO)
                .map(Movimiento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String pdfBase64 = generarPdfBase64(cliente.getNombre(), fechaInicio, fechaFin, detalles, totalCreditos, totalDebitos);

        return ReporteResponseDTO.builder()
                .cliente(cliente.getNombre())
                .fechaInicio(fechaInicio.format(DATE_FMT))
                .fechaFin(fechaFin.format(DATE_FMT))
                .movimientos(detalles)
                .totalCreditos(totalCreditos)
                .totalDebitos(totalDebitos)
                .reportePdfBase64(pdfBase64)
                .build();
    }

    private String generarPdfBase64(String clienteNombre, LocalDate inicio, LocalDate fin,
                                     List<ReporteMovimientoDTO> detalles,
                                     BigDecimal totalCreditos, BigDecimal totalDebitos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Título
            document.add(new Paragraph("Reporte de Movimientos Bancarios")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Cliente: " + clienteNombre).setFontSize(12));
            document.add(new Paragraph("Período: " + inicio.format(DATE_FMT) + " - " + fin.format(DATE_FMT))
                    .setFontSize(12));
            document.add(new Paragraph(" "));

            // Tabla
            float[] columnWidths = {100f, 80f, 80f, 80f, 80f, 80f};
            Table table = new Table(UnitValue.createPointArray(columnWidths));

            // Encabezados
            String[] headers = {"Fecha", "Nro. Cuenta", "Tipo Cuenta", "Saldo Inicial", "Movimiento", "Saldo Disponible"};
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setBold().setFontSize(9)));
            }

            // Filas
            for (ReporteMovimientoDTO d : detalles) {
                table.addCell(new Cell().add(new Paragraph(d.getFecha()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(d.getNumeroCuenta()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(d.getTipoCuenta()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(d.getSaldoInicial().toString()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(d.getMovimiento().toString()).setFontSize(8)));
                table.addCell(new Cell().add(new Paragraph(d.getSaldoDisponible().toString()).setFontSize(8)));
            }

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Créditos: " + totalCreditos).setFontSize(11));
            document.add(new Paragraph("Total Débitos: " + totalDebitos).setFontSize(11));

            document.close();

            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF del reporte", e);
        }
    }
}
