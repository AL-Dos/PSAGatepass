import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-audit-form',
  imports: [FormsModule, CommonModule, MatCardModule, MatIconModule, MatButtonModule,
            MatInputModule, MatFormFieldModule, MatDialogModule],
  templateUrl: './audit-form.html',
  styleUrl: './audit-form.css'
})
export class AuditForm {
description: any;

}
