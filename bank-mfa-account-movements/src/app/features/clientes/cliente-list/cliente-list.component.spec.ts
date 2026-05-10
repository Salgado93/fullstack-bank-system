import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { ClienteListComponent } from './cliente-list.component';
import { Cliente } from '../../../core/models/cliente.model';

describe('ClienteListComponent', () => {
  let component: ClienteListComponent;
  let fixture: ComponentFixture<ClienteListComponent>;
  let httpMock: HttpTestingController;

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
    {
      id: 2,
      clienteId: 'CLI-0002',
      nombre: 'Marianela Montalvo',
      genero: 'Femenino',
      edad: 28,
      identificacion: '0987654321',
      direccion: 'Amazonas y NNUU',
      telefono: '097548965',
      estado: true,
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClienteListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ClienteListComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load clients on init', () => {
    fixture.detectChanges(); // triggers ngOnInit

    const req = httpMock.expectOne('/api/clientes');
    req.flush(mockClientes);

    expect(component.clientes().length).toBe(2);
    expect(component.loading()).toBe(false);
  });

  it('should filter clients by search term', () => {
    fixture.detectChanges();

    const req = httpMock.expectOne('/api/clientes');
    req.flush(mockClientes);

    component.onSearch('Jose');
    expect(component.clientesFiltrados().length).toBe(1);
    expect(component.clientesFiltrados()[0].nombre).toBe('Jose Lema');
  });

  it('should filter clients by identification', () => {
    fixture.detectChanges();

    const req = httpMock.expectOne('/api/clientes');
    req.flush(mockClientes);

    component.onSearch('0987654321');
    expect(component.clientesFiltrados().length).toBe(1);
    expect(component.clientesFiltrados()[0].nombre).toBe('Marianela Montalvo');
  });

  it('should show all clients when search is empty', () => {
    fixture.detectChanges();

    const req = httpMock.expectOne('/api/clientes');
    req.flush(mockClientes);

    component.onSearch('');
    expect(component.clientesFiltrados().length).toBe(2);
  });

  it('should render client rows in the table', () => {
    fixture.detectChanges();

    const req = httpMock.expectOne('/api/clientes');
    req.flush(mockClientes);

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const rows = compiled.querySelectorAll('.table__tr');
    expect(rows.length).toBe(2);
  });
});
