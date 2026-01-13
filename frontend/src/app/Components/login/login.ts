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
  user = {
    name: '',
    password: ''
  };

  loginValid = true;
  showPassword = false;

  constructor(private auth: AuthService, private router: Router) {}

  login(form: NgForm) {
    if (!form.valid) return;

    this.auth.login(this.user.name, this.user.password).subscribe({
      next: () => {
        this.loginValid = true;
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.loginValid = false;
      }
    });
  }
}
