import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CuentaService } from '../../../core/services/cuenta.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Cliente } from '../../../core/models/cliente.model';

@Component({
  selector: 'app-cuenta-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cuenta-form.component.html',
  styleUrl: './cuenta-form.component.scss',
})
export class CuentaFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly cuentaService = inject(CuentaService);
  private readonly clienteService = inject(ClienteService);
  private readonly notificationService = inject(NotificationService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly isEdit = signal(false);
  readonly cuentaId = signal<number | null>(null);
  readonly clientes = signal<Cliente[]>([]);

  form: FormGroup = this.fb.group({
    numeroCuenta: ['', [Validators.required, Validators.maxLength(20)]],
    tipoCuenta: ['', Validators.required],
    saldoInicial: [0, [Validators.required, Validators.min(0)]],
    clienteId: [null, Validators.required],
  });

  ngOnInit(): void {
    this.clienteService.listar().subscribe({
      next: (data) => this.clientes.set(data),
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit.set(true);
      this.cuentaId.set(+id);
      this.cargarCuenta(+id);
    }
  }

  private cargarCuenta(id: number): void {
    this.cuentaService.obtener(id).subscribe({
      next: (cuenta) => {
        this.form.patchValue({
          numeroCuenta: cuenta.numeroCuenta,
          tipoCuenta: cuenta.tipoCuenta,
          saldoInicial: cuenta.saldoInicial,
          clienteId: cuenta.clienteId,
        });
      },
    });
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const data = this.form.value;

    if (this.isEdit()) {
      this.cuentaService.actualizar(this.cuentaId()!, data).subscribe({
        next: () => {
          this.notificationService.success('Cuenta actualizada correctamente');
          this.router.navigate(['/cuentas']);
        },
      });
    } else {
      this.cuentaService.crear(data).subscribe({
        next: () => {
          this.notificationService.success('Cuenta creada correctamente');
          this.router.navigate(['/cuentas']);
        },
      });
    }
  }

  hasError(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && control.touched);
  }
}
