import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Pedido } from '../../Entidad/pedido';
import { Observable } from 'rxjs/internal/Observable';
import { DetallePedido } from '../../Entidad/detalle-pedido';

@Injectable({
  providedIn: 'root',
})
export class ServidorP {

  //Creamos un constructor para inicializar el servidor y inyectar las dependencias
  constructor(private http: HttpClient) {} // Inyectamos el HttpClient
  
  url = 'http://localhost:9000/'; // Creamos la url del servidor

  //Observable nos permite observar todas las respuestas del servidor
  //LA MEJOR PRACTICA ES USAR HttpResponse<T> para tipar la respuesta

  //METODOS PARA SOLO DE PEDIDO ------------------------------------------------------------------------------------------------------------------

  // Método para obtener los Pedidos
  ListarPedidos(): Observable<Pedido[]> { 
    return this.http.get<Pedido[]>(this.url + 'pedido/pedido/listar'); // Retornamos los pedidos
  }

  //Metodo para guardar un pedido
  guardarPedido(pedido: Pedido): Observable<any> { //Any para evitar errores y capturar todo tipo de respuestas
    return this.http.post<Pedido>(this.url + 'pedido/pedido/guardar', pedido, {observe: 'response', responseType: 'json'}); // Retornamos el pedido guardado
  }

  //Metodo para actualizar un pedido
  actualizarPedido(pedido: Pedido): Observable<HttpResponse<Pedido>> { //Colocamos HttpResponse<T> para tipar la respuesta
    return this.http.put<Pedido>(this.url + 'pedido/pedido/editar', pedido, {observe: 'response'}); // Retornamos el pedido actualizado
  }

  //Metodo para eliminar un pedido
  eliminarPedido(id: number): Observable<HttpResponse<any>> { //Colocamos HttpResponse<T> para tipar la respuesta
    return this.http.delete<Pedido>(this.url + 'pedido/pedido/eliminar/' + id, {observe: 'response'}); // Retornamos el pedido eliminado
  }

  //Metodo para buscar un pedido
  buscarPedido(id: number): Observable<Pedido>{ 
    return this.http.get<Pedido>(this.url + 'pedido/pedido/buscar/' + id); // Retornamos el producto buscado
  }

  //METODOS PERSONALIZADOS ------------------------------------------------------------------------------------------------------------------

  //Metodo para listar Pedidos por Cliente
  ListarPedidosPorCliente(id: number): Observable<any> { 
    return this.http.get<any>(this.url + 'pedido/pedido/listarXCliente/' + id); // Retornamos los pedidos
  }
  
  //Metodo para listar detalle por pedido
  ListarDetallesPorPedido(id: number): Observable<any> { 
    return this.http.get<any>(this.url + 'pedido/pedido/listarDetalleXPediod/' + id); // Retornamos los detalles
  }

  //METODOS PARA SOLO DE DETALLE ------------------------------------------------------------------------------------------------------------------

  // Método para obtener los DETALES 
  ListarDetalles(): Observable<DetallePedido[]> { 
    return this.http.get<DetallePedido[]>(this.url + 'pedido/detalle/listar'); // Retornamos los detalles
  }

  //Metodo para guardar un detalle
  guardarDetalle(detalle: DetallePedido): Observable<any> { //Any para evitar errores y capturar todo tipo de respuestas
    return this.http.post<DetallePedido>(this.url + 'pedido/detalle/guardar', detalle, {observe: 'response', responseType: 'json'}); // Retornamos el detalle guardado
  }



  //Metodo para eliminar un detalle
  eliminarDetalle(id: number): Observable<HttpResponse<any>> { //Colocamos HttpResponse<T> para tipar la respuesta
    return this.http.delete<DetallePedido>(this.url + 'pedido/detalle/eliminar/' + id, {observe: 'response'}); // Retornamos el detalle eliminado
  }

  //Metodo para buscar un detalle
  buscarDetalle(id: number): Observable<DetallePedido>{ 
    return this.http.get<DetallePedido>(this.url + 'pedido/detalle/buscar/' + id); // Retornamos el detalle buscado
  }

}
