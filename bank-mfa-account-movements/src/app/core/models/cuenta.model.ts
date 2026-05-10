export type TipoCuenta = 'AHORROS' | 'CORRIENTE';

export interface Cuenta {
  cuentaId: number;
  numeroCuenta: string;
  tipoCuenta: TipoCuenta;
  saldoInicial: number;
  saldo: number;
  estado: boolean;
  clienteNombre: string;
  clienteId: number;
}

export interface CuentaRequest {
  numeroCuenta: string;
  tipoCuenta: TipoCuenta;
  saldoInicial: number;
  clienteId: number;
  estado?: boolean;
}
