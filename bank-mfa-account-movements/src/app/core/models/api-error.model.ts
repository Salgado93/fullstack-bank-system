export interface ApiError {
  timestamp: string;
  status: number;
  mensaje: string;
  errores?: Record<string, string>;
}
