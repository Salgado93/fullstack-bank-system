import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CuentaService } from '../../../core/services/cuenta.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Cuenta } from '../../../core/models/cuenta.model';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-cuenta-list',
  standalone: true,
  imports: [RouterLink, CurrencyPipe],
  templateUrl: './cuenta-list.component.html',
  styleUrl: './cuenta-list.component.scss',
})
export class CuentaListComponent implements OnInit {
  private readonly cuentaService = inject(CuentaService);
  private readonly notificationService = inject(NotificationService);

  readonly cuentas = signal<Cuenta[]>([]);
  readonly loading = signal(false);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.cuentaService.listar().subscribe({
      next: (data) => {
        this.cuentas.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  eliminar(id: number): void {
    if (!confirm('¿Está seguro de eliminar esta cuenta?')) return;

    this.cuentaService.eliminar(id).subscribe({
      next: () => {
        this.notificationService.success('Cuenta eliminada correctamente');
        this.cargar();
      },
    });
  }
}
