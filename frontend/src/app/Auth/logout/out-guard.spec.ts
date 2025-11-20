import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { outGuard } from './out-guard';

describe('outGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => outGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
