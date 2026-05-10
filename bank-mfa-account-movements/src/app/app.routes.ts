import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'clientes', pathMatch: 'full' },
  {
    path: 'clientes',
    loadComponent: () =>
      import('./features/clientes/cliente-list/cliente-list.component').then(
        (m) => m.ClienteListComponent
      ),
  },
  {
    path: 'clientes/nuevo',
    loadComponent: () =>
      import('./features/clientes/cliente-form/cliente-form.component').then(
        (m) => m.ClienteFormComponent
      ),
  },
  {
    path: 'clientes/editar/:id',
    loadComponent: () =>
      import('./features/clientes/cliente-form/cliente-form.component').then(
        (m) => m.ClienteFormComponent
      ),
  },
  {
    path: 'cuentas',
    loadComponent: () =>
      import('./features/cuentas/cuenta-list/cuenta-list.component').then(
        (m) => m.CuentaListComponent
      ),
  },
  {
    path: 'cuentas/nueva',
    loadComponent: () =>
      import('./features/cuentas/cuenta-form/cuenta-form.component').then(
        (m) => m.CuentaFormComponent
      ),
  },
  {
    path: 'cuentas/editar/:id',
    loadComponent: () =>
      import('./features/cuentas/cuenta-form/cuenta-form.component').then(
        (m) => m.CuentaFormComponent
      ),
  },
  {
    path: 'movimientos',
    loadComponent: () =>
      import('./features/movimientos/movimiento-list/movimiento-list.component').then(
        (m) => m.MovimientoListComponent
      ),
  },
  {
    path: 'movimientos/nuevo',
    loadComponent: () =>
      import('./features/movimientos/movimiento-form/movimiento-form.component').then(
        (m) => m.MovimientoFormComponent
      ),
  },
  {
    path: 'reportes',
    loadComponent: () =>
      import('./features/reportes/reporte-view/reporte-view.component').then(
        (m) => m.ReporteViewComponent
      ),
  },
  { path: '**', redirectTo: 'clientes' },
];
