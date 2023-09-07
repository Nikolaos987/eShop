import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubFootComponent } from './sub-foot.component';

describe('SubFootComponent', () => {
  let component: SubFootComponent;
  let fixture: ComponentFixture<SubFootComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SubFootComponent]
    });
    fixture = TestBed.createComponent(SubFootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
