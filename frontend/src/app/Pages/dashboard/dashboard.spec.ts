import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, Subject, throwError } from 'rxjs';
import { EquipmentService } from '../../Services/equipment/equipment.service';
import { Dashboard } from './dashboard';

const requestors = [{
  id: 1, name: 'Alex', destination: 'Office', period: 'October',
  equipment: [
    { id: 1, equipmentName: 'Laptop', quantity: 1, equipmentCode: 'L-1' },
    { id: 2, equipmentName: 'Monitor', quantity: 1, equipmentCode: 'M-1', releasedAt: '2026-10-14T09:00:00Z' },
    { id: 3, equipmentName: 'Keyboard', quantity: 1, equipmentCode: 'K-1', releasedAt: '2026-10-14T09:00:00Z', returnedAt: '2026-10-15T09:00:00Z' }
  ]
}];

describe('Dashboard', () => {
  let component: Dashboard;
  let fixture: ComponentFixture<Dashboard>;
  let service: { getAllEquipment: ReturnType<typeof vi.fn>; releaseEquipment: ReturnType<typeof vi.fn>; returnEquipment: ReturnType<typeof vi.fn> };
  let snackBar: { open: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    service = {
      getAllEquipment: vi.fn(() => of(requestors)),
      releaseEquipment: vi.fn(() => of('Selected entries marked as released')),
      returnEquipment: vi.fn(() => of('Selected entries marked as returned'))
    };
    snackBar = { open: vi.fn() };
    await TestBed.configureTestingModule({
      imports: [Dashboard],
      providers: [{ provide: EquipmentService, useValue: service }]
    }).overrideComponent(Dashboard, {
      add: { providers: [{ provide: MatSnackBar, useValue: snackBar }] }
    }).compileComponents();
    fixture = TestBed.createComponent(Dashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('filters pending, outstanding, released and returned entries', () => {
    const cases = [
      ['pending', [1]], ['not-returned', [2]], ['released', [3, 2]], ['returned', [3]], ['all', [3, 2, 1]]
    ] as const;
    for (const [status, ids] of cases) {
      component.setStatusFilter(status);
      expect(component.dataSource.filteredData.map(row => row.id)).toEqual([...ids]);
    }
  });

  it('combines search with status, resets pagination and clears both filters', () => {
    const firstPage = vi.spyOn(component.paginator, 'firstPage');
    component.setStatusFilter('not-returned');
    const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
    input.value = '  MONITOR  ';
    input.dispatchEvent(new Event('input'));
    expect(component.dataSource.filteredData.map(row => row.id)).toEqual([2]);
    input.value = 'Laptop';
    input.dispatchEvent(new Event('input'));
    expect(component.dataSource.filteredData).toEqual([]);
    component.clearFilters();
    expect(component.searchTerm).toBe('');
    expect(component.dataSource.filteredData.length).toBe(3);
    expect(firstPage).toHaveBeenCalled();
  });

  it('does not select hidden rows when no records match', () => {
    component.toggleAll(true);
    expect(component.getSelectedCount()).toBe(3);
    component.searchTerm = 'missing equipment';
    component.setStatusFilter('pending');
    component.toggleAll(true);
    expect(component.getSelectedCount()).toBe(0);
    expect(component.isAllSelected()).toBe(false);
  });

  it('announces a release after success and keeps the active filter', () => {
    component.setStatusFilter('pending');
    const row = component.dataSource.filteredData[0];
    service.getAllEquipment.mockReturnValue(of([{
      ...requestors[0], equipment: requestors[0].equipment.map(eq => ({ ...eq, releasedAt: '2026-10-15T10:00:00Z' }))
    }]));
    component.releaseEntry(row);
    expect(service.releaseEquipment).toHaveBeenCalledWith([1]);
    expect(component.actionNotification?.message).toContain('Laptop (#1) for Alex was released');
    expect(component.actionNotification?.error).toBe(false);
    expect(component.dataSource.filteredData).toEqual([]);
    fixture.detectChanges();
    const banner = fixture.nativeElement.querySelector('.action-notification');
    expect(banner.textContent).toContain('released successfully');
    banner.querySelector('button').click();
    expect(component.actionNotification).toBeNull();
  });

  it('announces returns and prevents invalid actions', () => {
    const pending = component.dataSource.data.find(row => row.id === 1)!;
    const outstanding = component.dataSource.data.find(row => row.id === 2)!;
    component.returnEntry(pending);
    expect(service.returnEquipment).not.toHaveBeenCalled();
    component.releaseEntry(outstanding);
    expect(service.releaseEquipment).not.toHaveBeenCalled();
    component.returnEntry(outstanding);
    expect(service.returnEquipment).toHaveBeenCalledWith([2]);
    expect(component.actionNotification?.message).toContain('Monitor (#2) for Alex was returned');
  });

  it('blocks repeated actions while saving and shows errors without claiming success', () => {
    const response = new Subject<string>();
    service.releaseEquipment.mockReturnValue(response);
    const row = component.dataSource.data.find(row => row.id === 1)!;
    component.releaseEntry(row);
    component.releaseEntry(row);
    expect(service.releaseEquipment).toHaveBeenCalledTimes(1);
    expect(component.actionNotification).toBeNull();
    vi.spyOn(console, 'error').mockImplementation(() => {});
    response.error({ error: { message: 'Release failed' } });
    expect(component.actionInProgress).toBe(false);
    expect(component.actionNotification).toEqual({ message: 'Release failed', error: true });
    expect(service.getAllEquipment).toHaveBeenCalledTimes(1);
    service.returnEquipment.mockReturnValue(throwError(() => ({ error: 'Return failed' })));
    component.returnEntry(component.dataSource.data.find(row => row.id === 2)!);
    expect(component.actionNotification).toEqual({ message: 'Return failed', error: true });
    vi.restoreAllMocks();
  });
});
