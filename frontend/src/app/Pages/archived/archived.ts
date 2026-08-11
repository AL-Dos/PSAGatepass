import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EquipmentService } from '../../Services/equipment/equipment.service';

interface ArchivedEquipmentData {
  id: number;
  equipmentName: string;
  quantity: number;
  equipmentCode: string;
  requestorName: string;
  destination: string;
  period: string;
  released: boolean;
  returned: boolean;
  releasedAt: Date | null;
  returnedAt: Date | null;
}

@Component({
  selector: 'app-archived',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatSortModule, MatPaginatorModule, MatButtonModule, MatIconModule, MatSnackBarModule],
  templateUrl: './archived.html',
  styleUrl: './archived.css'
})
export class Archived implements OnInit {
  dataSource = new MatTableDataSource<ArchivedEquipmentData>();
  displayedColumns: string[] = ['id', 'equip', 'quan', 'pNum', 'dest', 'pCover', 'req', 'released', 'returned'];

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private equipmentService: EquipmentService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.loadArchivedEquipment();
  }

  loadArchivedEquipment() {
    this.equipmentService.getArchivedEquipment().subscribe({
      next: (requestors: any[]) => {
        const archivedData: ArchivedEquipmentData[] = [];
        requestors.forEach(requestor => {
          if (requestor.equipment && Array.isArray(requestor.equipment)) {
            requestor.equipment.forEach((eq: any) => {
              archivedData.push({
                id: eq.id,
                equipmentName: eq.equipmentName,
                quantity: eq.quantity,
                equipmentCode: eq.equipmentCode,
                requestorName: requestor.name,
                destination: requestor.destination,
                period: requestor.period,
                released: eq.releasedAt ? true : false,
                returned: eq.returnedAt ? true : false,
                releasedAt: eq.releasedAt ? new Date(eq.releasedAt) : null,
                returnedAt: eq.returnedAt ? new Date(eq.returnedAt) : null
              });
            });
          }
        });
        this.dataSource.data = archivedData;
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      },
      error: (err) => {
        console.error('Error loading archived equipment:', err);
        this.snackBar.open('Unable to load archived records', 'Close', { duration: 5000 });
      }
    });
  }
}
