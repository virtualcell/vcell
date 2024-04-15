import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VcellIdentityComponent } from './vcell-identity.component';

describe('VCellIdentityComponentComponent', () => {
  let component: VcellIdentityComponent;
  let fixture: ComponentFixture<VcellIdentityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VcellIdentityComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(VcellIdentityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
