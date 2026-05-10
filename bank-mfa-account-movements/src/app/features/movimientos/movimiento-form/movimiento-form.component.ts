import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MovimientoService } from '../../../core/services/movimiento.service';
import { CuentaService } from '../../../core/services/cuenta.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Cuenta } from '../../../core/models/cuenta.model';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-movimiento-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './movimiento-form.component.html',
  styleUrl: './movimiento-form.component.scss',
})
export class MovimientoFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly movimientoService = inject(MovimientoService);
  private readonly cuentaService = inject(CuentaService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  readonly cuentas = signal<Cuenta[]>([]);
  readonly cuentaSeleccionada = signal<Cuenta | null>(null);

  form: FormGroup = this.fb.group({
    tipoMovimiento: ['', Validators.required],
    valor: [null, [Validators.required, Validators.min(0.01)]],
    cuentaId: [null, Validators.required],
  });

  ngOnInit(): void {
    this.cuentaService.listar().subscribe({
      next: (data) => this.cuentas.set(data),
    });

    this.form.get('cuentaId')?.valueChanges.subscribe((id) => {
      const cuenta = this.cuentas().find((c) => c.cuentaId === +id) ?? null;
      this.cuentaSeleccionada.set(cuenta);
    });
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const data = {
      ...this.form.value,
      cuentaId: +this.form.value.cuentaId,
    };

    this.movimientoService.crear(data).subscribe({
      next: () => {
        this.notificationService.success('Movimiento registrado correctamente');
        this.router.navigate(['/movimientos']);
      },
    });
  }

  hasError(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && control.touched);
  }
}
