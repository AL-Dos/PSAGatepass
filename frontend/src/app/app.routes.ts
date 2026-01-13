import { Routes } from '@angular/router';
import { Landing } from './Pages/landing/landing';
import { Dashboard } from './Pages/dashboard/dashboard';
import { AuthGuard } from './Auth/auth.login';
import { OutGuard } from './Auth/auth.logout';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: Landing,
    canActivate: [OutGuard]
  },
  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [AuthGuard]
  }
];
