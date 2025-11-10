import { Routes } from '@angular/router';
import { AuditPage } from './Pages/audit-page/audit-page';
import { LandingPage } from './Pages/landing-page/landing-page';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: 'login',
        component:LandingPage,
        // canActivate: [Login]
    },
    {
        path: 'database',
        component:AuditPage,
        // canActivate: [Logout]
    }
];
