import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PublicationListComponentComponent } from './publication-list-component.component';

describe('PublicationListComponentComponent', () => {
  let component: PublicationListComponentComponent;
  let fixture: ComponentFixture<PublicationListComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PublicationListComponentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PublicationListComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
