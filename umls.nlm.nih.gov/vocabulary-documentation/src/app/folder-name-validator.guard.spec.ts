import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { folderNameValidatorGuard } from './folder-name-validator.guard';

describe('folderNameValidatorGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => folderNameValidatorGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
