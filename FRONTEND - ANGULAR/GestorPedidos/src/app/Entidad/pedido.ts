import { DetallePedido } from "./detalle-pedido";

export class Pedido {

    idPedido! : number;
    fecha! : Date;
    total! : number;
    estatus! : string;
    clienteId! : number;
    detalles!: DetallePedido[]; // Creamos un arreglo para los detalles de x pedido
}
