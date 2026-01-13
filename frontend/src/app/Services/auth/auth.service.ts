import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = '/api';

  constructor(private http: HttpClient, private router: Router) {}

  login(name: string, password: string) {
    return this.http.post(`${this.apiUrl}/login`, { name, password }, {
      withCredentials: true
    });
  }

  logout() {
    return this.http.post(`${this.apiUrl}/logout`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => this.router.navigate(['/login']))
    );
  }

  checkSession() {
    return this.http.get<{ authenticated: boolean }>(
      `${this.apiUrl}/me`,
      { withCredentials: true }
    );
  }
}
