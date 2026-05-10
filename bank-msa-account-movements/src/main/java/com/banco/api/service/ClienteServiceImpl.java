package com.banco.api.service;

import com.banco.api.dto.ClienteRequestDTO;
import com.banco.api.dto.ClienteResponseDTO;
import com.banco.api.exception.RecursoNoEncontradoException;
import com.banco.api.model.Cliente;
import com.banco.api.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id));
        return toResponseDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDTO crear(ClienteRequestDTO dto) {
        if (clienteRepository.existsByIdentificacion(dto.getIdentificacion())) {
            throw new IllegalArgumentException("Ya existe un cliente con la identificación: " + dto.getIdentificacion());
        }

        Cliente cliente = new Cliente();
        cliente.setClienteId(UUID.randomUUID().toString().substring(0, 8));
        mapToEntity(dto, cliente);

        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id));
        mapToEntity(dto, cliente);
        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id));
        clienteRepository.delete(cliente);
    }

    private void mapToEntity(ClienteRequestDTO dto, Cliente cliente) {
        cliente.setNombre(dto.getNombre());
        cliente.setGenero(dto.getGenero());
        cliente.setEdad(dto.getEdad());
        cliente.setIdentificacion(dto.getIdentificacion());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setPassword(dto.getPassword());
        cliente.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .estado(cliente.getEstado())
                .build();
    }
}
