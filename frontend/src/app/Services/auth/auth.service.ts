import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, map, Observable, of, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = '/api';
  private authCache$?: Observable<boolean>;
  private userNameSubject = new BehaviorSubject<string | null>(null);
  userName$ = this.userNameSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(user: { name: string; password: string }): Observable<boolean> {
    return this.http.post<{ authenticated: boolean }>(`${this.apiUrl}/login`, user, { withCredentials: true }).pipe(
      tap(res => { this.authCache$ = of(res.authenticated) }),
      map(res => !!res?.authenticated),
      catchError(() => of(false))
    );
  }

  logout(): void {
    // clear immediately
    this.userNameSubject.next(null);
    this.authCache$ = of(false);

    this.http.post<void>(`${this.apiUrl}/logout`, {}, { withCredentials: true })
      .subscribe({
        next: () => console.log('Logged out successfully on server'),
        error: err => console.error('Logout error:', err)
      });
  }

  checkSession(): Observable<boolean> {
    console.log('[AuthService] checkSession() called');
    return this.me().pipe(
      map(res => {
        const isAuth = !!res?.authenticated;
        console.log('[AuthService] me() response:', res, 'isAuth:', isAuth);
        return isAuth;
      }),
      tap(isAuth => {
        this.authCache$ = of(isAuth);
      }),
      catchError((err) => {
        console.error('[AuthService] checkSession error:', err);
        this.authCache$ = of(false);
        return of(false);
      })
    );
  }

  private me(): Observable<{ authenticated: boolean; name?: string } | null> {
    console.log('[AuthService] me() endpoint called');
    return this.http.get<{ authenticated: boolean; name?: string }>(`${this.apiUrl}/me`, { withCredentials: true }).pipe(
      tap(res => {
        console.log('[AuthService] me() response received:', res);
        if (res?.authenticated) {
          this.userNameSubject.next(res.name ?? null);
        } else {
          this.userNameSubject.next(null);
        }
      }),
      catchError((err) => {
        console.error('[AuthService] me() error:', err);
        this.userNameSubject.next(null);
        return of(null);
      })
    );
  }
}
