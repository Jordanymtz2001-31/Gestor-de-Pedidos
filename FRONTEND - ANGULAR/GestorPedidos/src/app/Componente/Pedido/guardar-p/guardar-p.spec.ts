import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuardarP } from './guardar-p';

describe('GuardarP', () => {
  let component: GuardarP;
  let fixture: ComponentFixture<GuardarP>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuardarP]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GuardarP);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
