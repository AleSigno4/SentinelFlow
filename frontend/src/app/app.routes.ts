import { LoginComponent } from './components/login/login';
import { Dashboard } from './components/dashboard/dashboard';
import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: Dashboard },
];