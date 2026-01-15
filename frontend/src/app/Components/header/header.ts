import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../Services/auth/auth.service';

@Component({
  selector: 'app-header',
  imports: [CommonModule, MatButtonModule],
  standalone: true,
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  constructor(private router: Router, private auth: AuthService) {}

  get isDashboard(): boolean {
    return this.router.url === '/dashboard';
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
