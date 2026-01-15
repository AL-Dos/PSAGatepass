import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../Services/auth/auth.service';
import { map, Observable, tap, } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService,private router: Router) {}

  canActivate(): Observable<boolean> {
    return this.auth.checkSession().pipe(
      map(isAuth => {
        console.log('[AuthGuard] checkSession returned:', isAuth);
        if (!isAuth) {
          console.log('[AuthGuard] Not authenticated, redirecting to login');
          this.router.navigate(['/login']);
          return false;
        }
        console.log('[AuthGuard] Authenticated, allowing access');
        return true;
      })
    );
  }
}
