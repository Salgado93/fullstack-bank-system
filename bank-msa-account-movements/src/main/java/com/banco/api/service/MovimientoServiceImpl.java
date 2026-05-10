package com.banco.api.service;

import com.banco.api.dto.MovimientoRequestDTO;
import com.banco.api.dto.MovimientoResponseDTO;
import com.banco.api.exception.LimiteDiarioExcedidoException;
import com.banco.api.exception.RecursoNoEncontradoException;
import com.banco.api.exception.SaldoInsuficienteException;
import com.banco.api.model.Cuenta;
import com.banco.api.model.Movimiento;
import com.banco.api.model.TipoMovimiento;
import com.banco.api.repository.CuentaRepository;
import com.banco.api.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private static final BigDecimal LIMITE_DIARIO = new BigDecimal("1000.00");

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> listarTodos() {
        return movimientoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponseDTO obtenerPorId(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento no encontrado con ID: " + id));
        return toResponseDTO(movimiento);
    }

    @Override
    @Transactional
    public MovimientoResponseDTO crear(MovimientoRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(dto.getCuentaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada con ID: " + dto.getCuentaId()));

        BigDecimal saldoActual = cuenta.getSaldo();
        BigDecimal valor = dto.getValor();
        BigDecimal nuevoSaldo;

        if (dto.getTipoMovimiento() == TipoMovimiento.DEBITO) {
            // Validar saldo suficiente
            if (saldoActual.compareTo(BigDecimal.ZERO) == 0) {
                throw new SaldoInsuficienteException("Saldo no disponible");
            }
            if (saldoActual.compareTo(valor) < 0) {
                throw new SaldoInsuficienteException(
                        "Saldo insuficiente. Saldo actual: " + saldoActual + ", débito solicitado: " + valor);
            }

            // Validar límite diario de retiros
            validarLimiteDiario(cuenta.getCuentaId(), valor);

            nuevoSaldo = saldoActual.subtract(valor);
        } else {
            nuevoSaldo = saldoActual.add(valor);
        }

        // Actualizar saldo de la cuenta
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);

        // Crear el movimiento
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setValor(valor);
        movimiento.setSaldoDisponible(nuevoSaldo);
        movimiento.setEstado(true);
        movimiento.setCuenta(cuenta);

        return toResponseDTO(movimientoRepository.save(movimiento));
    }

    @Override
    @Transactional
    public MovimientoResponseDTO actualizar(Long id, MovimientoRequestDTO dto) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento no encontrado con ID: " + id));

        Cuenta cuenta = cuentaRepository.findById(dto.getCuentaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada con ID: " + dto.getCuentaId()));

        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setValor(dto.getValor());
        movimiento.setCuenta(cuenta);

        return toResponseDTO(movimientoRepository.save(movimiento));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento no encontrado con ID: " + id));
        movimientoRepository.delete(movimiento);
    }

    private void validarLimiteDiario(Long cuentaId, BigDecimal nuevoDebito) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = hoy.atTime(LocalTime.MAX);

        BigDecimal totalDebitosHoy = movimientoRepository.sumDebitosDiarios(cuentaId, inicioDia, finDia);
        BigDecimal totalConNuevo = totalDebitosHoy.add(nuevoDebito);

        if (totalConNuevo.compareTo(LIMITE_DIARIO) > 0) {
            throw new LimiteDiarioExcedidoException(
                    "Cupo diario excedido. Límite: " + LIMITE_DIARIO +
                    ", ya retirado hoy: " + totalDebitosHoy +
                    ", intento de retiro: " + nuevoDebito);
        }
    }

    private MovimientoResponseDTO toResponseDTO(Movimiento movimiento) {
        return MovimientoResponseDTO.builder()
                .movimientoId(movimiento.getMovimientoId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldoDisponible(movimiento.getSaldoDisponible())
                .estado(movimiento.getEstado())
                .cuentaId(movimiento.getCuenta().getCuentaId())
                .numeroCuenta(movimiento.getCuenta().getNumeroCuenta())
                .build();
    }
}
