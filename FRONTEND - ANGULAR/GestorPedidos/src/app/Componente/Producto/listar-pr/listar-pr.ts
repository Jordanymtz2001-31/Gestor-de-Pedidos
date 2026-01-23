import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ServidorPr } from '../../../Servidor/Producto/servidorPr';
import { Producto } from '../../../Entidad/producto';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-listar-pr',
  imports: [FormsModule],
  templateUrl: './listar-pr.html',
  styleUrl: './listar-pr.css',
})
export class ListarPr implements OnInit {

   //Set para controlar múltiples menús abiertos simultáneamente
  //Puede contener varios valores de tipo string que representan los menús abiertos
  protected openMenus = new Set<string>();

  //Creamos nuestro contructor y le inyectamos
  constructor(private router: Router, private servicioPr : ServidorPr) {}

  //Creamos variables para almacenar los datos que vienen del servidor
    productos!: Producto[];
    producto = new Producto(); //Inicializamos un objeto producto para usarlor

    //Método para alternar menús
  //Si ago clic en un menú y está abierto, lo cierra
  //Si ago clic en un menú y está cerrado, lo abre
  toggleMenu(menu: string) {
    if (this.openMenus.has(menu)) { //Si el menu esta abierto
      this.openMenus.delete(menu); //Lo cerramos
    } else {
      this.openMenus.add(menu); //Lo abrimos
    }
  }

  //Método para verificar si un menú está abierto
  isMenuOpen(menu: string): boolean {
    return this.openMenus.has(menu);
  }
  
    //Creamos el metodo ngOnInit para cargar los datos al inicializar el componente
    ngOnInit(): void{
      this.servicioPr.ListarProductos().subscribe({
        next: (data) => { //data es la respuesta del servidor
          this.productos = data; //Asignamos los datos a la variable productos
        },
        error: (err) => { console.error('Error al cargar los productos', err);
        }
      })
    }
  
    //Metodo para editar un producto
    btnEditar(id: number){
      this.router.navigate(['editar-producto', id]);
    }
  
    //Metodo para eliminar un producto
    btnEliminar(id: number){
      Swal.fire({
        title: '¿Estás seguro de eliminar este producto?',
        text: "¡Esta acción no se puede deshacer!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminarla'
      }).then((result) => {
        if (result.isConfirmed) {
          this.servicioPr.eliminarProducto(id).subscribe({
            next: (response) => {
              const mensaje = response.body?.message || response.body || 'El producto ha sido eliminada correctamente';
              Swal.fire(
                'Eliminada',
                mensaje,
                'success'
              );
              this.ngOnInit(); //Recargamos la lista de productos despues de eliminar
            },
            error: (err) => {
              const errorMsg = err.error?.mensaje || err.error || 'Hubo un problema al eliminar el producto';
              Swal.fire(
                'Error',
                errorMsg,
                'error'
              );
            }
          });
        }else if (result.isDismissed) {
          Swal.fire({
            title: 'Cancelado',
            icon : 'info',
            text: 'El producto no ha sido eliminado',
            showConfirmButton: false,
            timer: 1500
          });
        }
      });
    }
  }