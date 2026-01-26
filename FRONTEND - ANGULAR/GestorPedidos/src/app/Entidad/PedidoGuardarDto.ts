import { DetalleGuardarDto } from "./DetalleGuardarDto";

export class PedidoGuardarDto {
  clienteId!: number;
  total!: number;
  detalles!: DetalleGuardarDto[];
}