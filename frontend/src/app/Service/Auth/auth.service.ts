
// import { HttpClient } from '@angular/common/http';
// import { Injectable } from '@angular/core';
// import { BehaviorSubject, catchError, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';

// @Injectable({ providedIn: 'root' })
// export class AuthService {
//   private apiUrl = '/api/auth';
//   private userNameSubject = new BehaviorSubject<string | null>(null);
//   userName$ = this.userNameSubject.asObservable();
//   private authCache$?: Observable<boolean>;

//   constructor(private http: HttpClient) {}

//   login(user: { name: string; password: string }): Observable<boolean> {
//     return this.http.post<{ name: string, jti: string }>(`${this.apiUrl}/login`, user, { withCredentials: true }).pipe(
//       tap(res => {
//         // update state immediately
//         this.userNameSubject.next(res.name);
//         this.authCache$ = of(true); // cached as authenticated
//       }),
//       switchMap(() => this.me()), // sync with backend
//       map(res => !!res?.authenticated),
//       catchError(() => of(false))
//     );
//   }

//   logout(): void {
//     // clear immediately
//     this.userNameSubject.next(null);
//     this.authCache$ = of(false);

//     this.http.post<void>(`${this.apiUrl}/logout`, {}, { withCredentials: true })
//       .subscribe({
//         next: () => console.log('[AuthService] Logged out successfully on server'),
//         error: err => console.error('[AuthService] Logout error:', err)
//       });
//   }

//   isAuthenticated(): Observable<boolean> {
//     if (!this.authCache$) {
//       this.authCache$ = this.me().pipe(
//         map(res => !!res?.authenticated),
//         catchError(() => of(false)),
//         shareReplay(1)
//       );
//     }
//     return this.authCache$;
//   }

//     private me(): Observable<{ authenticated: boolean; name?: string } | null> {
//     return this.http.get<{ authenticated: boolean; name?: string }>(`${this.apiUrl}/me`, { withCredentials: true }).pipe(
//       tap(res => {
//         if (res?.authenticated) {
//           this.userNameSubject.next(res.name ?? null);
//         } else {
//           this.userNameSubject.next(null);
//         }
//       }),
//       catchError(() => {
//         this.userNameSubject.next(null);
//         return of(null);
//       })
//     );
//   }
// }
