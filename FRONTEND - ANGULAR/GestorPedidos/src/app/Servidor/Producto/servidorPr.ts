import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Producto } from '../../Entidad/producto';

@Injectable({
  providedIn: 'root',
})
export class ServidorPr {

  //Creamos un constructor para inicializar el servidor y inyectar las dependencias
  constructor(private http: HttpClient) {} // Inyectamos el HttpClient
  
  url = 'http://localhost:9000/'; // Creamos la url del servidor

  //Observable nos permite observar todas las respuestas del servidor
  //LA MEJOR PRACTICA ES USAR HttpResponse<T> para tipar la respuesta

  // Método para obtener los Productos
  ListarProductos(): Observable<Producto[]> { 
    return this.http.get<Producto[]>(this.url + 'producto/listar'); // Retornamos los productos
  }

  //Metodo para guardar un producto
  guardarProducto(producto: Producto): Observable<any> { //Any para evitar errores y capturar todo tipo de respuestas
    return this.http.post<Producto>(this.url + 'producto/guardar', producto, {observe: 'response', responseType: 'json'}); // Retornamos el producto guardado
  }

  //Metodo para actualizar un productos
  actualizarProducto(producto: Producto): Observable<HttpResponse<Producto>> { //Colocamos HttpResponse<T> para tipar la respuesta
    return this.http.put<Producto>(this.url + 'producto/editar', producto, {observe: 'response'}); // Retornamos el producto actualizado
  }

  //Metodo para eliminar un producto
  eliminarProducto(id: number): Observable<HttpResponse<any>> { //Colocamos HttpResponse<T> para tipar la respuesta
    return this.http.delete<Producto>(this.url + 'producto/eliminar/' + id, {observe: 'response'}); // Retornamos el producto eliminado
  }

  //Metodo para buscar un producto
  buscarProducto(id: number): Observable<Producto>{ 
    return this.http.get<Producto>(this.url + 'producto/buscar/' + id); // Retornamos el producto buscado
  }

  
}
