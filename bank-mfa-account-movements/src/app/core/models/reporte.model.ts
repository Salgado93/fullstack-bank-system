export interface ReporteMovimiento {
  fecha: string;
  cliente: string;
  numeroCuenta: string;
  tipoCuenta: string;
  saldoInicial: number;
  movimiento: number;
  saldoDisponible: number;
}

export interface Reporte {
  cliente: string;
  fechaInicio: string;
  fechaFin: string;
  movimientos: ReporteMovimiento[];
  totalCreditos: number;
  totalDebitos: number;
  reportePdfBase64: string;
}
