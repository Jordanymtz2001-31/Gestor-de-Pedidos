import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListarDT } from './listar-dt';

describe('ListarDT', () => {
  let component: ListarDT;
  let fixture: ComponentFixture<ListarDT>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListarDT]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListarDT);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
