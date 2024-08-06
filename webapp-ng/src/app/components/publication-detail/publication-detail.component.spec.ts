import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PublicationDetailComponent } from './publication-detail.component';

describe('PublicationDetailComponent', () => {
  let component: PublicationDetailComponent;
  let fixture: ComponentFixture<PublicationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PublicationDetailComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PublicationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
