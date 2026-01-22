import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidosCancelados } from './pedidos-cancelados';

describe('PedidosCancelados', () => {
  let component: PedidosCancelados;
  let fixture: ComponentFixture<PedidosCancelados>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidosCancelados]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PedidosCancelados);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
