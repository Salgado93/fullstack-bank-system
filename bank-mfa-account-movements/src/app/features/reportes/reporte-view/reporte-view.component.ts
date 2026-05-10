import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReporteService } from '../../../core/services/reporte.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Cliente } from '../../../core/models/cliente.model';
import { Reporte } from '../../../core/models/reporte.model';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-reporte-view',
  standalone: true,
  imports: [ReactiveFormsModule, CurrencyPipe],
  templateUrl: './reporte-view.component.html',
  styleUrl: './reporte-view.component.scss',
})
export class ReporteViewComponent {
  private readonly fb = inject(FormBuilder);
  private readonly reporteService = inject(ReporteService);
  private readonly clienteService = inject(ClienteService);
  private readonly notificationService = inject(NotificationService);

  readonly clientes = signal<Cliente[]>([]);
  readonly reporte = signal<Reporte | null>(null);
  readonly loading = signal(false);

  form: FormGroup = this.fb.group({
    clienteId: [null, Validators.required],
    inicio: ['', Validators.required],
    fin: ['', Validators.required],
  });

  constructor() {
    this.clienteService.listar().subscribe({
      next: (data) => this.clientes.set(data),
    });
  }

  buscar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { clienteId, inicio, fin } = this.form.value;
    this.loading.set(true);

    this.reporteService.generar(+clienteId, inicio, fin).subscribe({
      next: (data) => {
        this.reporte.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  descargarPdf(): void {
    const r = this.reporte();
    if (r?.reportePdfBase64) {
      this.reporteService.descargarPdf(r.reportePdfBase64, r.cliente);
      this.notificationService.success('PDF descargado');
    }
  }

  hasError(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && control.touched);
  }
}
