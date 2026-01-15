import { Component } from '@angular/core';
import { AuthService } from '../../Services/auth/auth.service';
import { Router } from '@angular/router';
import { MatDialogRef } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-logout',
  imports: [MatIconModule, MatButtonModule, MatCardModule],
  templateUrl: './logout.html',
  styleUrl: './logout.css',
})
export class Logout {
  constructor(private auth: AuthService, private router: Router, private dialogRef: MatDialogRef<Logout>) {}

  logout(){
    this.auth.logout();
    this.dialogRef.close();
    this.router.navigate(['/login']);
  }

  close() {
    this.dialogRef.close();
  }
}
