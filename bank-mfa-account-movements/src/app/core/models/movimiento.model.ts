export type TipoMovimiento = 'CREDITO' | 'DEBITO';

export interface Movimiento {
  movimientoId: number;
  fecha: string;
  tipoMovimiento: TipoMovimiento;
  valor: number;
  saldoDisponible: number;
  estado: boolean;
  cuentaId: number;
  numeroCuenta: string;
}

export interface MovimientoRequest {
  tipoMovimiento: TipoMovimiento;
  valor: number;
  cuentaId: number;
}
