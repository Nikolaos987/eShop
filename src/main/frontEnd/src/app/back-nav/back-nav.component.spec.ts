import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackNavComponent } from './back-nav.component';

describe('BackNavComponent', () => {
  let component: BackNavComponent;
  let fixture: ComponentFixture<BackNavComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BackNavComponent]
    });
    fixture = TestBed.createComponent(BackNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
