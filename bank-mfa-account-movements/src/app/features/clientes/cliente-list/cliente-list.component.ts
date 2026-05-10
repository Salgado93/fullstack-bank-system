import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../../core/services/cliente.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Cliente } from '../../../core/models/cliente.model';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './cliente-list.component.html',
  styleUrl: './cliente-list.component.scss',
})
export class ClienteListComponent implements OnInit {
  private readonly clienteService = inject(ClienteService);
  private readonly notificationService = inject(NotificationService);

  readonly clientes = signal<Cliente[]>([]);
  readonly searchTerm = signal('');
  readonly loading = signal(false);

  readonly clientesFiltrados = computed(() => {
    const term = this.searchTerm().toLowerCase();
    if (!term) return this.clientes();
    return this.clientes().filter(
      (c) =>
        c.nombre.toLowerCase().includes(term) ||
        c.identificacion.includes(term)
    );
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.clienteService.listar().subscribe({
      next: (data) => {
        this.clientes.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  eliminar(id: number): void {
    if (!confirm('¿Está seguro de eliminar este cliente?')) return;

    this.clienteService.eliminar(id).subscribe({
      next: () => {
        this.notificationService.success('Cliente eliminado correctamente');
        this.cargar();
      },
    });
  }

  onSearch(value: string): void {
    this.searchTerm.set(value);
  }
}
