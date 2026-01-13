import { Routes } from '@angular/router';
import { Landing } from './Pages/landing/landing';
import { Dashboard } from './Pages/dashboard/dashboard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: Landing
  },
  {
    path: 'dashboard',
    component: Dashboard
  }
];
