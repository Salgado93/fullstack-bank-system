package com.banco.api.service;

import com.banco.api.dto.CuentaRequestDTO;
import com.banco.api.dto.CuentaResponseDTO;
import com.banco.api.exception.RecursoNoEncontradoException;
import com.banco.api.model.Cliente;
import com.banco.api.model.Cuenta;
import com.banco.api.repository.ClienteRepository;
import com.banco.api.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> listarTodas() {
        return cuentaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponseDTO obtenerPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada con ID: " + id));
        return toResponseDTO(cuenta);
    }

    @Override
    @Transactional
    public CuentaResponseDTO crear(CuentaRequestDTO dto) {
        if (cuentaRepository.existsByNumeroCuenta(dto.getNumeroCuenta())) {
            throw new IllegalArgumentException("Ya existe una cuenta con el número: " + dto.getNumeroCuenta());
        }

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + dto.getClienteId()));

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setSaldo(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        cuenta.setCliente(cliente);

        return toResponseDTO(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional
    public CuentaResponseDTO actualizar(Long id, CuentaRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada con ID: " + id));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + dto.getClienteId()));

        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        cuenta.setCliente(cliente);

        return toResponseDTO(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada con ID: " + id));
        cuentaRepository.delete(cuenta);
    }

    private CuentaResponseDTO toResponseDTO(Cuenta cuenta) {
        return CuentaResponseDTO.builder()
                .cuentaId(cuenta.getCuentaId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .saldo(cuenta.getSaldo())
                .estado(cuenta.getEstado())
                .clienteNombre(cuenta.getCliente().getNombre())
                .clienteId(cuenta.getCliente().getId())
                .build();
    }
}
