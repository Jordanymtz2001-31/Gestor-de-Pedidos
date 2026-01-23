import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Cliente } from '../../Entidad/cliente';

@Injectable({
  providedIn: 'root',
})
export class ServidorC {

  //Creamos un constructor para inicializar el servidor y inyectar las dependencias
  constructor(private http: HttpClient) {} // Inyectamos el HttpClient
  
  url = 'http://localhost:9000/'; // Creamos la url del servidor

  //Observable nos permite observar todas las respuestas del servidor
  //LA MEJOR PRACTICA ES USAR HttpResponse<T> para tipar la respuesta

  // Método para obtener los clientes
  ListarClientes(): Observable<Cliente[]> { 
    return this.http.get<Cliente[]>(this.url + 'cliente/listar'); // Retornamos los clientes
  }

  //Metodo para guardar un cliente
  guardarCliente(cliente: Cliente): Observable<any> { //Any para evitar errores y capturar todo tipo de respuestas
    return this.http.post<Cliente>(this.url + 'cliente/guardar', cliente, {observe: 'response', responseType: 'json'}); // Retornamos el cliente guardado
  }

  //Metodo para actualizar un cliente
  actualizarCliente(cliente: Cliente): Observable<HttpResponse<Cliente>> { //Colocamos HttpResponse<T> para tipar la respuesta
    return this.http.put<Cliente>(this.url + 'cliente/editar', cliente, {observe: 'response'}); // Retornamos el cliente actualizado
  }

  //Metodo para eliminar un cliente
  eliminarCliente(id: number): Observable<HttpResponse<any>> { //Colocamos HttpResponse<T> para tipar la respuesta
    return this.http.delete<Cliente>(this.url + 'cliente/eliminar/' + id, {observe: 'response'}); // Retornamos el cliente eliminado
  }

  //Metodo para buscar un cliente
  buscarCliente(id: number): Observable<Cliente>{ 
    return this.http.get<Cliente>(this.url + 'cliente/buscar/' + id); // Retornamos el cliente buscado
  }

}
