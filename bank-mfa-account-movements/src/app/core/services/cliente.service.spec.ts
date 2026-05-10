import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ClienteService } from './cliente.service';
import { Cliente, ClienteRequest } from '../models/cliente.model';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ClienteService,
      ],
    });

    service = TestBed.inject(ClienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('listar() should return an array of clients', () => {
    const mockClientes: Cliente[] = [
      {
        id: 1,
        clienteId: 'CLI-0001',
        nombre: 'Jose Lema',
        genero: 'Masculino',
        edad: 30,
        identificacion: '1234567890',
        direccion: 'Otavalo sn',
        telefono: '098254785',
        estado: true,
      },
    ];

    service.listar().subscribe((clientes) => {
      expect(clientes).toEqual(mockClientes);
      expect(clientes.length).toBe(1);
    });

    const req = httpMock.expectOne('/api/clientes');
    expect(req.request.method).toBe('GET');
    req.flush(mockClientes);
  });

  it('crear() should POST a new client', () => {
    const newCliente: ClienteRequest = {
      nombre: 'Pedro Perez',
      genero: 'Masculino',
      edad: 40,
      identificacion: '999888777',
      direccion: 'Av. Siempre Viva',
      telefono: '099999999',
      password: 'secreto',
    };

    const mockResponse: Cliente = {
      id: 2,
      clienteId: 'CLI-0002',
      ...newCliente,
      estado: true,
    };

    service.crear(newCliente).subscribe((cliente) => {
      expect(cliente.id).toBe(2);
      expect(cliente.nombre).toBe('Pedro Perez');
    });

    const req = httpMock.expectOne('/api/clientes');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newCliente);
    req.flush(mockResponse);
  });

  it('eliminar() should DELETE a client', () => {
    service.eliminar(1).subscribe();

    const req = httpMock.expectOne('/api/clientes/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
