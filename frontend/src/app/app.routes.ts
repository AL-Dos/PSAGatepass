import { Routes } from '@angular/router';
import { Landing } from './Pages/landing/landing';
import { Dashboard } from './Pages/dashboard/dashboard';
import { AuthGuard } from './Auth/auth.login';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: Landing,
  },
  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [AuthGuard]
  },
  {
    path: 'guard',
    loadComponent: () => import('./Pages/guard-scan/guard-scan').then(m => m.GuardScan)
  }
];
