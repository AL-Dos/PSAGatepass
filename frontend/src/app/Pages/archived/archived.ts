import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EquipmentService } from '../../Services/equipment/equipment.service';
import { MatFormField } from "@angular/material/select";
import { MatInputModule } from "@angular/material/input";

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
  imports: [CommonModule, MatTableModule, MatSortModule, MatPaginatorModule, MatButtonModule, MatCheckboxModule, MatIconModule, MatSnackBarModule, MatTooltipModule, MatFormField, MatInputModule],
  templateUrl: './archived.html',
  styleUrl: './archived.css'
})
export class Archived implements OnInit {
  dataSource = new MatTableDataSource<ArchivedEquipmentData>();
  selectedIds = new Set<number>();
  displayedColumns: string[] = ['select', 'id', 'equip', 'quan', 'pNum', 'dest', 'pCover', 'req', 'released', 'returned', 'actions'];

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private equipmentService: EquipmentService,
    private router: Router,
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

  isAllSelected() {
    return this.dataSource.data.length > 0 && this.selectedIds.size === this.dataSource.data.length;
  }

  isSomeSelected() {
    return this.selectedIds.size > 0 && this.selectedIds.size < this.dataSource.data.length;
  }

  toggleAll(checked: boolean) {
    this.selectedIds.clear();
    if (checked) {
      this.dataSource.data.forEach(row => this.selectedIds.add(row.id));
    }
  }

  toggleRow(element: ArchivedEquipmentData, checked: boolean) {
    if (checked) {
      this.selectedIds.add(element.id);
    } else {
      this.selectedIds.delete(element.id);
    }
  }

  getSelectedCount() {
    return this.selectedIds.size;
  }

  gotoDashboard() {
    this.router.navigate(['/dashboard']);
  }

  unarchiveEntry(element: ArchivedEquipmentData) {
    this.equipmentService.unarchiveEquipment([element.id]).subscribe({
      next: () => {
        this.snackBar.open('Entry unarchived', 'Close', { duration: 4000 });
        this.selectedIds.delete(element.id);
        this.loadArchivedEquipment();
      },
      error: (err) => {
        console.error('Error unarchiving entry:', err);
        this.snackBar.open('Unable to unarchive this record', 'Close', { duration: 5000 });
      }
    });
  }

  deleteEntry(element: ArchivedEquipmentData) {
    this.equipmentService.deleteEquipment([element.id]).subscribe({
      next: () => {
        this.snackBar.open('Entry deleted', 'Close', { duration: 4000 });
        this.selectedIds.delete(element.id);
        this.loadArchivedEquipment();
      },
      error: (err) => {
        console.error('Error deleting entry:', err);
        this.snackBar.open('Unable to delete this record', 'Close', { duration: 5000 });
      }
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
