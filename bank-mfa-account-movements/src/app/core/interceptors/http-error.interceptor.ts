import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';
import { ApiError } from '../models/api-error.model';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let message = 'Error inesperado. Intente nuevamente.';

      if (error.error) {
        const apiError = error.error as ApiError;
        if (apiError.mensaje) {
          message = apiError.mensaje;
        } else if (apiError.errores) {
          message = Object.values(apiError.errores).join('. ');
        }
      } else if (error.status === 0) {
        message = 'No se puede conectar al servidor.';
      }

      notificationService.error(message);
      return throwError(() => error);
    })
  );
};
