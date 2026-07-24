import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((erro) => {
      if (erro.status === 401) {
        // Token ausente/expirado/inválido — desloga e manda pro login.
        authService.logout();
      }
      return throwError(() => erro);
    })
  );
};
