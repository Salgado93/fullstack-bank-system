import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClienteService } from '../../../core/services/cliente.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cliente-form.component.html',
  styleUrl: './cliente-form.component.scss',
})
export class ClienteFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly clienteService = inject(ClienteService);
  private readonly notificationService = inject(NotificationService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly isEdit = signal(false);
  readonly clienteId = signal<number | null>(null);

  form: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    genero: ['', Validators.required],
    edad: [null, [Validators.required, Validators.min(0), Validators.max(150)]],
    identificacion: ['', [Validators.required, Validators.maxLength(20)]],
    direccion: ['', Validators.maxLength(200)],
    telefono: ['', Validators.maxLength(20)],
    password: ['', Validators.required],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit.set(true);
      this.clienteId.set(+id);
      this.form.get('password')?.clearValidators();
      this.form.get('password')?.updateValueAndValidity();
      this.cargarCliente(+id);
    }
  }

  private cargarCliente(id: number): void {
    this.clienteService.obtener(id).subscribe({
      next: (cliente) => {
        this.form.patchValue({
          nombre: cliente.nombre,
          genero: cliente.genero,
          edad: cliente.edad,
          identificacion: cliente.identificacion,
          direccion: cliente.direccion,
          telefono: cliente.telefono,
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
      if (!data.password) data.password = '****';
      this.clienteService.actualizar(this.clienteId()!, data).subscribe({
        next: () => {
          this.notificationService.success('Cliente actualizado correctamente');
          this.router.navigate(['/clientes']);
        },
      });
    } else {
      this.clienteService.crear(data).subscribe({
        next: () => {
          this.notificationService.success('Cliente creado correctamente');
          this.router.navigate(['/clientes']);
        },
      });
    }
  }

  hasError(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && control.touched);
  }
}
