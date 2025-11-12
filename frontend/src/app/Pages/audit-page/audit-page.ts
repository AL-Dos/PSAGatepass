import { CommonModule} from '@angular/common';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-audit-page',
  imports: [CommonModule, MatTableModule, MatPaginatorModule, MatSortModule,
            MatInputModule, MatButtonModule, MatIconModule, MatDatepickerModule,
            MatNativeDateModule],
  templateUrl: './audit-page.html',
  styleUrl: './audit-page.css'
})
export class AuditPage {

}
