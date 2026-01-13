import { Injectable } from '@angular/core';
import { CanActivate, CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../Services/auth/auth.service';
import { Observable } from 'rxjs/internal/Observable';
import { map, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class OutGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): Observable<boolean> {
    return this.auth.checkSession().pipe(
      map(result => {
        if (result.authenticated) {
          this.router.navigate(['/dashboard']);
          return false;
        }
        return true;
      })
    );
  }
}
