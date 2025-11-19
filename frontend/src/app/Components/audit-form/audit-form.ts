import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { warn } from 'console';
import { AutofocusDirective } from '../../Directive/autofocus.directive';

@Component({
  selector: 'app-audit-form',
  imports: [FormsModule, CommonModule, MatCardModule, MatIconModule, MatButtonModule,
            MatInputModule, MatFormFieldModule, MatDialogModule, AutofocusDirective],
  templateUrl: './audit-form.html',
  styleUrl: './audit-form.css'
})
export class AuditForm {
  showSuccess = false;

  constructor(private http: HttpClient) {}

  model = {
    name: '',
    destination: '',
    period: '',
    equipmentItems: [
      { equipmentName: '', quantity: 0, equipmentCode: '' }
    ]
  };

  addEquipment() {
    this.model.equipmentItems.push({
      equipmentName: '',
      quantity: 0,
      equipmentCode: ''
    });
  }

  removeEquipment(index: number) {
    this.model.equipmentItems.splice(index, 1);
  }

  private focusFirstInvalid(form: NgForm) {
    const controls = form.controls;

    for (const name in controls) {
      if (controls[name].invalid) {
        const element = document.querySelector(`[name="${name}"]`) as HTMLElement;
        if (element) {
          element.focus();
        }
        break;
      }
    }
  }

  resetForm(form: any) {
    form.resetForm();
    this.model.equipmentItems = [
      { equipmentName: '', quantity: 0, equipmentCode: '' }
    ];
  }

  submitForm(form: any) {
    if (!form.valid) {
      this.focusFirstInvalid(form);
      return;
    };

    // Filter out empty equipment items
    const filteredModel = {
      ...this.model,
      equipmentItems: this.model.equipmentItems.filter(item =>
        item.equipmentName.trim() !== '' &&
        item.equipmentCode.trim() !== '' &&
        item.quantity > 0
      )
    };

    // Prevent submission if no valid equipment items
    if (filteredModel.equipmentItems.length === 0) {
      console.warn('Please add at least one valid equipment item');
      return;
    }

    this.http.post('http://localhost:8090/api/submit', filteredModel)
      .subscribe({
        next: () => {
          this.showSuccess = true;
          setTimeout(() => this.showSuccess = false, 6000);
        },
        error: (err) => console.error(err)
      });
  }
}
