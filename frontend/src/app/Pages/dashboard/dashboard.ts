import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { EquipmentService } from '../../Services/equipment/equipment.service';

export const DASHBOARD_TABLE_COLUMNS = [
  CommonModule,
  FormsModule,
  MatTableModule,
  MatSortModule,
  MatPaginatorModule,
  MatButtonModule,
  MatCheckboxModule,
  MatIconModule,
  MatInputModule,
  MatDatepickerModule,
  MatNativeDateModule,
  MatSnackBarModule
] as const;

interface EquipmentData {
  id: number;
  requestorId: number;
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
  selector: 'app-dashboard',
  standalone: true,
  imports: [...DASHBOARD_TABLE_COLUMNS],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  dataSource = new MatTableDataSource<EquipmentData>();
  displayedColumns: string[] = ['select', 'id', 'equip', 'quan', 'pNum', 'dest', 'pCover', 'req', 'released', 'returned', 'actions'];
  isExporting = false;
  selectedIds = new Set<number>();

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private equipmentService: EquipmentService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadEquipment();

    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'id':
          return item.id;
        case 'equip':
          return item.equipmentName.toLowerCase();
        case 'quan':
          return item.quantity;
        case 'pNum':
          return item.equipmentCode.toLowerCase();
        case 'dest':
          return item.destination.toLowerCase();
        case 'pCover':
          return item.period;
        case 'req':
          return item.requestorName.toLowerCase();
        case 'released':
          return item.released ? 1 : 0;
        case 'returned':
          return item.returned ? 1 : 0;
        default:
          return '';
      }
    };
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
                requestorId: requestor.id,
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

        this.dataSource.data = equipmentData;
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.selectedIds.clear();
      },
      error: (err) => {
        console.error('Error loading equipment:', err);
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

  getReleasedCount(): number {
    return this.dataSource.data.filter(item => item.released).length;
  }

  getReturnedCount(): number {
    return this.dataSource.data.filter(item => item.returned).length;
  }

  getPendingCount(): number {
    return this.dataSource.data.filter(item => !item.released).length;
  }

  getSelectedCount(): number {
    return this.selectedIds.size;
  }

  hasMultipleRequestorsSelected(): boolean {
    return this.getSelectedRequestorIdSet().size > 1;
  }

  isAllSelected(): boolean {
    const rows = this.getSelectableRows();
    if (rows.length === 0) return false;
    return rows.every(row => this.selectedIds.has(row.id));
  }

  isSomeSelected(): boolean {
    const rows = this.getSelectableRows();
    if (rows.length === 0) return false;
    const selected = rows.filter(row => this.selectedIds.has(row.id)).length;
    return selected > 0 && selected < rows.length;
  }

  toggleAll(checked: boolean): void {
    if (!checked) {
      this.selectedIds.clear();
      return;
    }
    const rows = this.getSelectableRows();
    if (rows.length === 0) return;

    const currentRequestorId = this.getSingleSelectedRequestorId();
    if (currentRequestorId !== null) {
      rows.filter(row => row.requestorId === currentRequestorId)
        .forEach(row => this.selectedIds.add(row.id));
      return;
    }

    const requestorIds = new Set(rows.map(row => row.requestorId));
    if (requestorIds.size > 1) {
      this.showRequestorMismatchWarning();
      return;
    }

    rows.forEach(row => this.selectedIds.add(row.id));
  }

  toggleRow(row: EquipmentData, checked: boolean): void {
    if (row.returned) return;
    if (checked) {
      const currentRequestorId = this.getSingleSelectedRequestorId();
      if (currentRequestorId !== null && row.requestorId !== currentRequestorId) {
        this.showRequestorMismatchWarning();
        return;
      }
      this.selectedIds.add(row.id);
    } else {
      this.selectedIds.delete(row.id);
    }
  }

  private getSelectableRows(): EquipmentData[] {
    const rows = this.dataSource.filteredData && this.dataSource.filteredData.length > 0
      ? this.dataSource.filteredData
      : this.dataSource.data;
    return rows.filter(row => !row.returned);
  }

  private getSelectedRequestorIdSet(): Set<number> {
    const ids = new Set<number>();
    for (const row of this.dataSource.data) {
      if (this.selectedIds.has(row.id)) {
        ids.add(row.requestorId);
      }
    }
    return ids;
  }

  private getSingleSelectedRequestorId(): number | null {
    const ids = this.getSelectedRequestorIdSet();
    if (ids.size === 1) {
      return ids.values().next().value as number;
    }
    return null;
  }

  private showRequestorMismatchWarning(): void {
    this.snackBar.open('You can only select items from one requestor at a time.', 'OK', {
      duration: 3500,
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }

  archiveSelected(): void {
    const ids = Array.from(this.selectedIds.values());
    if (ids.length === 0) return;
    if (this.hasMultipleRequestorsSelected()) {
      this.showRequestorMismatchWarning();
      return;
    }
    this.equipmentService.archiveEquipment(ids).subscribe({
      next: () => {
        this.snackBar.open('Selected gatepass entries archived', 'Close', { duration: 3000 });
        this.loadEquipment();
      },
      error: (err) => {
        console.error('Error archiving entries:', err);
        this.snackBar.open(err.error || 'Error archiving entries', 'Close', { duration: 5000 });
      }
    });
  }

  gotoArchived(): void {
    this.router.navigate(['/archived']);
  }

  exportLogs(): void {
    if (this.hasMultipleRequestorsSelected()) {
      this.showRequestorMismatchWarning();
      return;
    }
    const ids = Array.from(this.selectedIds.values());
    if (ids.length === 0) return;
    this.isExporting = true;
    this.equipmentService.exportLogs(ids).subscribe({
      next: (response: Blob) => {
        const url = window.URL.createObjectURL(response);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'transmittal.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        this.isExporting = false;
      },
      error: (err) => {
        console.error('Error exporting logs:', err);
        this.isExporting = false;
      }
    });
  }

  releaseEntry(element: EquipmentData): void {
    if (element.released) return;
    this.equipmentService.releaseEquipment([element.id]).subscribe({
      next: () => {
        this.snackBar.open('Entry marked as released', 'Close', { duration: 3000 });
        this.loadEquipment();
      },
      error: (err) => {
        console.error('Error releasing entry:', err);
        this.snackBar.open(err.error || 'Error releasing entry', 'Close', { duration: 5000 });
      }
    });
  }

  returnEntry(element: EquipmentData): void {
    if (!element.released || element.returned) return;
    this.equipmentService.returnEquipment([element.id]).subscribe({
      next: () => {
        this.snackBar.open('Entry marked as returned', 'Close', { duration: 3000 });
        this.loadEquipment();
      },
      error: (err) => {
        console.error('Error returning entry:', err);
        this.snackBar.open(err.error || 'Error returning entry', 'Close', { duration: 5000 });
      }
    });
  }

  deleteEntry(element: EquipmentData): void {
    if (!window.confirm('Delete this equipment entry?')) {
      return;
    }
    this.equipmentService.deleteEquipment([element.id]).subscribe({
      next: () => {
        this.snackBar.open('Entry deleted', 'Close', { duration: 3000 });
        this.loadEquipment();
      },
      error: (err) => {
        console.error('Error deleting entry:', err);
        this.snackBar.open(err.error || 'Error deleting entry', 'Close', { duration: 5000 });
      }
    });
  }
}
