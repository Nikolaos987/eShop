import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuantityNumberComponent } from './quantity-number.component';

describe('QuantityNumberComponent', () => {
  let component: QuantityNumberComponent;
  let fixture: ComponentFixture<QuantityNumberComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuantityNumberComponent]
    });
    fixture = TestBed.createComponent(QuantityNumberComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
