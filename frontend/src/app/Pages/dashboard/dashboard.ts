import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { EquipmentService } from '../../Services/equipment/equipment.service';

export const DASHBOARD_TABLE_COLUMNS = [CommonModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule] as const;

interface EquipmentData {
  id: number;
  equipmentName: string;
  quantity: number;
  equipmentCode: string;
  requestorName: string;
  destination: string;
  period: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [...DASHBOARD_TABLE_COLUMNS],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  dataSource = new MatTableDataSource<EquipmentData>();
  displayedColumns: string[] = ['id', 'equip', 'pNum', 'dest', 'pCover'];
  
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private equipmentService: EquipmentService) {}

  ngOnInit() {
    this.loadEquipment();
  }

  loadEquipment() {
    this.equipmentService.getAllEquipment().subscribe({
      next: (requestors: any[]) => {
        const equipmentData: EquipmentData[] = [];
        
        // Flatten the nested equipment from each requestor
        requestors.forEach(requestor => {
          if (requestor.equipment && Array.isArray(requestor.equipment)) {
            requestor.equipment.forEach((eq: any) => {
              equipmentData.push({
                id: eq.id,
                equipmentName: eq.equipmentName,
                quantity: eq.quantity,
                equipmentCode: eq.equipmentCode,
                requestorName: requestor.name,
                destination: requestor.destination,
                period: requestor.period
              });
            });
          }
        });

        this.dataSource.data = equipmentData;
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      },
      error: (err) => {
        console.error('Error loading equipment:', err);
      }
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
}
