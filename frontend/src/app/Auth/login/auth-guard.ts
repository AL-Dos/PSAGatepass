import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../../Service/Auth/auth.service';
import { map, Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService,private router: Router) {}

  canActivate() {
    return this.auth.checkSession().pipe(
      map(result => {
        if (result.authenticated) return true;
        this.router.navigate(['/login']);
        return false;
      })
    );
  }
}
