import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const cloned = req.clone({ withCredentials: true });

  console.log('%c[AuthInterceptor] Outgoing request with credentials:', 'color: green; font-weight: bold;', cloned.url);

  return next(cloned).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401 || err.status === 403) {
        document.cookie = 'jwt=; Max-Age=0; path=/';
        router.navigate(['/login']);
      }
      return throwError(() => err);
    })
  );
}
