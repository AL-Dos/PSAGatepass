import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Router } from '@angular/router';
import { AuthService } from '../../Services/auth/auth.service';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-login',
  imports: [FormsModule, MatCardModule, MatInputModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatCheckboxModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  user = { name: '', password: '' };

  loginValid = true;
  showPassword = false;
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  login(): void {
    this.loading = true;
    this.auth.login(this.user).subscribe({
      next: success => {
        this.loading = false;
        if (success) {
          this.loginValid = true;
          console.log('Login success');
          queueMicrotask(() => this.router.navigate(['/dashboard']));
        } else {
          this.loginValid = false;
        }
      },
      error: err => {
        this.loading = false;
        console.error('Login failed:', err);
        this.loginValid = false;
      }
    });
  }
}
