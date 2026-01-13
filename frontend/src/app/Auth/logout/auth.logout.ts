import { CanActivateFn } from '@angular/router';

export const outGuard: CanActivateFn = (route, state) => {
  return true;
};
