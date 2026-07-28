import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Bloqueia rotas restritas ao perfil ADMIN (ex.: gestão de usuários). */
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.ehAdmin()) {
    return true;
  }

  router.navigate(['/']);
  return false;
};
