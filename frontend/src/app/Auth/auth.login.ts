// import { Injectable } from '@angular/core';
// import { CanActivate, Router } from '@angular/router';
// import { AuthService } from '../services/auth-service/auth.service';
// import { map, Observable, tap } from 'rxjs';

// @Injectable({ providedIn: 'root' })
// export class Logout implements CanActivate {
//   constructor(private auth: AuthService, private router: Router) {}

//   canActivate(): Observable<boolean> {
//     return this.auth.isAuthenticated().pipe(
//       tap(authenticated => {
//         if (authenticated) {
//           this.router.navigate(['/database']);
//         }
//       }),
//       map(authenticated => !authenticated)
//     );
//   }
// }
