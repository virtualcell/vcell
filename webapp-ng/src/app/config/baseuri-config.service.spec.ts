import { TestBed } from '@angular/core/testing';

import { BaseuriConfigService } from './baseuri-config.service';

describe('BaseuriConfigService', () => {
  let service: BaseuriConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BaseuriConfigService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
