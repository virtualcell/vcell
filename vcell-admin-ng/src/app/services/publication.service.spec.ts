import { TestBed } from '@angular/core/testing';

import { PublicationService } from './publication.service';

describe('PublicationService', () => {
  let service: PublicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PublicationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
