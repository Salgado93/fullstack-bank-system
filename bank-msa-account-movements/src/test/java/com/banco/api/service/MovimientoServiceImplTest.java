package com.banco.api.service;

import com.banco.api.dto.MovimientoRequestDTO;
import com.banco.api.dto.MovimientoResponseDTO;
import com.banco.api.exception.LimiteDiarioExcedidoException;
import com.banco.api.exception.RecursoNoEncontradoException;
import com.banco.api.exception.SaldoInsuficienteException;
import com.banco.api.model.*;
import com.banco.api.repository.CuentaRepository;
import com.banco.api.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceImplTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private MovimientoServiceImpl movimientoService;

    private Cuenta cuentaBase;
    private Cliente clienteBase;

    @BeforeEach
    void setUp() {
        clienteBase = new Cliente();
        clienteBase.setId(1L);
        clienteBase.setNombre("Jose Lema");
        clienteBase.setIdentificacion("1234567890");
        clienteBase.setClienteId("CLI-0001");
        clienteBase.setEstado(true);

        cuentaBase = new Cuenta();
        cuentaBase.setCuentaId(1L);
        cuentaBase.setNumeroCuenta("478758");
        cuentaBase.setTipoCuenta(TipoCuenta.AHORROS);
        cuentaBase.setSaldoInicial(new BigDecimal("2000.00"));
        cuentaBase.setSaldo(new BigDecimal("2000.00"));
        cuentaBase.setEstado(true);
        cuentaBase.setCliente(clienteBase);
    }

    @Test
    @DisplayName("Crear movimiento CREDITO debe sumar al saldo")
    void crearCredito_debeSumarAlSaldo() {
        MovimientoRequestDTO request = new MovimientoRequestDTO();
        request.setTipoMovimiento(TipoMovimiento.CREDITO);
        request.setValor(new BigDecimal("500.00"));
        request.setCuentaId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaBase));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaBase);

        Movimiento movimientoGuardado = new Movimiento();
        movimientoGuardado.setMovimientoId(1L);
        movimientoGuardado.setFecha(LocalDateTime.now());
        movimientoGuardado.setTipoMovimiento(TipoMovimiento.CREDITO);
        movimientoGuardado.setValor(new BigDecimal("500.00"));
        movimientoGuardado.setSaldoDisponible(new BigDecimal("2500.00"));
        movimientoGuardado.setEstado(true);
        movimientoGuardado.setCuenta(cuentaBase);

        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimientoGuardado);

        MovimientoResponseDTO response = movimientoService.crear(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("2500.00"), response.getSaldoDisponible());
        assertEquals(TipoMovimiento.CREDITO, response.getTipoMovimiento());
        verify(cuentaRepository).save(any(Cuenta.class));
    }

    @Test
    @DisplayName("Crear movimiento DEBITO debe restar del saldo")
    void crearDebito_debeRestarDelSaldo() {
        MovimientoRequestDTO request = new MovimientoRequestDTO();
        request.setTipoMovimiento(TipoMovimiento.DEBITO);
        request.setValor(new BigDecimal("575.00"));
        request.setCuentaId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaBase));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaBase);
        when(movimientoRepository.sumDebitosDiarios(eq(1L), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        Movimiento movimientoGuardado = new Movimiento();
        movimientoGuardado.setMovimientoId(2L);
        movimientoGuardado.setFecha(LocalDateTime.now());
        movimientoGuardado.setTipoMovimiento(TipoMovimiento.DEBITO);
        movimientoGuardado.setValor(new BigDecimal("575.00"));
        movimientoGuardado.setSaldoDisponible(new BigDecimal("1425.00"));
        movimientoGuardado.setEstado(true);
        movimientoGuardado.setCuenta(cuentaBase);

        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimientoGuardado);

        MovimientoResponseDTO response = movimientoService.crear(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("1425.00"), response.getSaldoDisponible());
        assertEquals(TipoMovimiento.DEBITO, response.getTipoMovimiento());
    }

    @Test
    @DisplayName("Debito con saldo cero debe lanzar SaldoInsuficienteException")
    void crearDebito_saldoCero_debeLanzarExcepcion() {
        cuentaBase.setSaldo(BigDecimal.ZERO);

        MovimientoRequestDTO request = new MovimientoRequestDTO();
        request.setTipoMovimiento(TipoMovimiento.DEBITO);
        request.setValor(new BigDecimal("100.00"));
        request.setCuentaId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaBase));

        SaldoInsuficienteException ex = assertThrows(
                SaldoInsuficienteException.class,
                () -> movimientoService.crear(request)
        );

        assertEquals("Saldo no disponible", ex.getMessage());
        verify(cuentaRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debito mayor al saldo debe lanzar SaldoInsuficienteException")
    void crearDebito_saldoInsuficiente_debeLanzarExcepcion() {
        cuentaBase.setSaldo(new BigDecimal("50.00"));

        MovimientoRequestDTO request = new MovimientoRequestDTO();
        request.setTipoMovimiento(TipoMovimiento.DEBITO);
        request.setValor(new BigDecimal("100.00"));
        request.setCuentaId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaBase));

        SaldoInsuficienteException ex = assertThrows(
                SaldoInsuficienteException.class,
                () -> movimientoService.crear(request)
        );

        assertTrue(ex.getMessage().contains("Saldo insuficiente"));
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debito que excede límite diario debe lanzar LimiteDiarioExcedidoException")
    void crearDebito_limiteDiarioExcedido_debeLanzarExcepcion() {
        MovimientoRequestDTO request = new MovimientoRequestDTO();
        request.setTipoMovimiento(TipoMovimiento.DEBITO);
        request.setValor(new BigDecimal("400.00"));
        request.setCuentaId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaBase));
        when(movimientoRepository.sumDebitosDiarios(eq(1L), any(), any()))
                .thenReturn(new BigDecimal("700.00"));

        LimiteDiarioExcedidoException ex = assertThrows(
                LimiteDiarioExcedidoException.class,
                () -> movimientoService.crear(request)
        );

        assertTrue(ex.getMessage().contains("Cupo diario excedido"));
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cuenta no encontrada debe lanzar RecursoNoEncontradoException")
    void crearMovimiento_cuentaNoExiste_debeLanzarExcepcion() {
        MovimientoRequestDTO request = new MovimientoRequestDTO();
        request.setTipoMovimiento(TipoMovimiento.CREDITO);
        request.setValor(new BigDecimal("100.00"));
        request.setCuentaId(999L);

        when(cuentaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> movimientoService.crear(request)
        );
    }

    @Test
    @DisplayName("Debito justo en el límite diario debe procesarse correctamente")
    void crearDebito_justoEnLimite_debePermitir() {
        MovimientoRequestDTO request = new MovimientoRequestDTO();
        request.setTipoMovimiento(TipoMovimiento.DEBITO);
        request.setValor(new BigDecimal("300.00"));
        request.setCuentaId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaBase));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaBase);
        when(movimientoRepository.sumDebitosDiarios(eq(1L), any(), any()))
                .thenReturn(new BigDecimal("700.00"));

        Movimiento movGuardado = new Movimiento();
        movGuardado.setMovimientoId(3L);
        movGuardado.setFecha(LocalDateTime.now());
        movGuardado.setTipoMovimiento(TipoMovimiento.DEBITO);
        movGuardado.setValor(new BigDecimal("300.00"));
        movGuardado.setSaldoDisponible(new BigDecimal("1700.00"));
        movGuardado.setEstado(true);
        movGuardado.setCuenta(cuentaBase);

        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movGuardado);

        MovimientoResponseDTO response = movimientoService.crear(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("1700.00"), response.getSaldoDisponible());
    }
}
