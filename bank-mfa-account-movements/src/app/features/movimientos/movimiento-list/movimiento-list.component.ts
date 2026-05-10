import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MovimientoService } from '../../../core/services/movimiento.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Movimiento } from '../../../core/models/movimiento.model';
import { CurrencyPipe, DatePipe } from '@angular/common';

@Component({
  selector: 'app-movimiento-list',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './movimiento-list.component.html',
  styleUrl: './movimiento-list.component.scss',
})
export class MovimientoListComponent implements OnInit {
  private readonly movimientoService = inject(MovimientoService);
  private readonly notificationService = inject(NotificationService);

  readonly movimientos = signal<Movimiento[]>([]);
  readonly loading = signal(false);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.movimientoService.listar().subscribe({
      next: (data) => {
        this.movimientos.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  eliminar(id: number): void {
    if (!confirm('¿Está seguro de eliminar este movimiento?')) return;

    this.movimientoService.eliminar(id).subscribe({
      next: () => {
        this.notificationService.success('Movimiento eliminado correctamente');
        this.cargar();
      },
    });
  }
}
