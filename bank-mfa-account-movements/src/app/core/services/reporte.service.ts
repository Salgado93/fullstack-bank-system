import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reporte } from '../models/reporte.model';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/reportes';

  generar(clienteId: number, inicio: string, fin: string): Observable<Reporte> {
    const params = new HttpParams()
      .set('clienteId', clienteId)
      .set('inicio', inicio)
      .set('fin', fin);

    return this.http.get<Reporte>(this.baseUrl, { params });
  }

  descargarPdf(base64: string, nombreCliente: string): void {
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: 'application/pdf' });

    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `reporte_${nombreCliente}.pdf`;
    link.click();
    URL.revokeObjectURL(url);
  }
}
