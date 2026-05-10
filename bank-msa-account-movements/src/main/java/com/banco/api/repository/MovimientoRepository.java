package com.banco.api.repository;

import com.banco.api.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaCuentaId(Long cuentaId);

    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.cliente.id = :clienteId " +
           "AND m.fecha BETWEEN :inicio AND :fin ORDER BY m.fecha ASC")
    List<Movimiento> findByClienteAndFechaRange(
            @Param("clienteId") Long clienteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM Movimiento m " +
           "WHERE m.cuenta.cuentaId = :cuentaId " +
           "AND m.tipoMovimiento = com.banco.api.model.TipoMovimiento.DEBITO " +
           "AND m.fecha BETWEEN :inicioDia AND :finDia")
    BigDecimal sumDebitosDiarios(
            @Param("cuentaId") Long cuentaId,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia);
}
