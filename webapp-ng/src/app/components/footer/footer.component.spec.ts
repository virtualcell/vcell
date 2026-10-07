import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FooterComponent } from './footer.component';

describe('FooterComponent', () => {
  let component: FooterComponent;
  let fixture: ComponentFixture<FooterComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ FooterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('links to Accessibility at the University of Connecticut', () => {
    const link: HTMLAnchorElement | null = fixture.nativeElement.querySelector(
      'a[href="https://accessibility.uconn.edu/"]'
    );
    expect(link).toBeTruthy();
    expect(link?.textContent).toContain('Accessibility at the University of Connecticut');
  });
});
