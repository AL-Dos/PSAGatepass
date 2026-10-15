import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { EquipmentService } from './equipment.service';

describe('EquipmentService action responses', () => {
  it('handles the plain text responses returned by release and return endpoints', () => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const service = TestBed.inject(EquipmentService);
    const http = TestBed.inject(HttpTestingController);
    for (const action of ['release', 'return'] as const) {
      const success = vi.fn();
      const request = action === 'release' ? service.releaseEquipment([42]) : service.returnEquipment([42]);
      request.subscribe(success);
      const pending = http.expectOne(`/api/admin/${action}`);
      expect(pending.request.responseType).toBe('text');
      expect(pending.request.body).toEqual({ equipmentIds: [42] });
      pending.flush('Selected entries updated');
      expect(success).toHaveBeenCalledWith('Selected entries updated');
    }
    http.verify();
  });
});
