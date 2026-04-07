import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AutofocusDirective } from '../../Directive/autofocus.directive';

@Component({
  selector: 'app-form',
  imports: [FormsModule, MatCardModule, MatIconModule, MatButtonModule, MatInputModule, MatFormFieldModule, MatDialogModule, MatSnackBarModule, AutofocusDirective,],
  templateUrl: './form.html',
  styleUrl: './form.css',
})
export class Form {
  showSuccess = false;

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  model = {
    name: '',
    destination: '',
    period: '',
    purpose: '',
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
    const filteredEquipment = this.model.equipmentItems.filter(item =>
      item.equipmentName.trim() !== '' &&
      item.equipmentCode.trim() !== '' &&
      item.quantity > 0
    );

    // Prevent submission if no valid equipment items
    if (filteredEquipment.length === 0) {
      console.warn('Please add at least one valid equipment item');
      return;
    }

    const payload = {
      name: this.model.name,
      destination: this.model.destination,
      period: this.model.period,
      purpose: this.model.purpose,
      equipment: filteredEquipment
    };

    // Use same-origin API path so it works in dev (proxy) and prod (nginx)
    const baseUrl = `/api/submit`;
    this.http.post(baseUrl, payload, { responseType: 'blob' })
      .subscribe({
        next: () => {
          this.showSuccess = true;
          setTimeout(() => this.showSuccess = false, 6000);
          this.snackBar.open('Request saved successfully.', 'OK', {
            duration: 4000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
        },
        error: (err) => console.error(err)
      });
  }
}
