import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ServidorC } from '../../../Servidor/Cliente/servidorC';
import { Router } from '@angular/router';
import { Cliente } from '../../../Entidad/cliente';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-listas-c',
  imports: [FormsModule],
  templateUrl: './listas-c.html',
  styleUrl: './listas-c.css',
})

//OninInit es un ciclo de vida del componente que se ejecuta al inicializar 
export class ListasC implements OnInit {

  //Set para controlar múltiples menús abiertos simultáneamente
  //Puede contener varios valores de tipo string que representan los menús abiertos
  protected openMenus = new Set<string>();

  //Creamos nuestro contructor y le inyectamos 
  constructor(private router: Router, private servicioC :ServidorC) {}

  //Creamos variables para almacenar los datos que vienen del servidor Cliente
  clientes!: Cliente[];
  cliente = new Cliente(); //Inicializamos un objeto cliente para usarlor

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
    this.servicioC.ListarClientes().subscribe({
      next: (data) => { //data es la respuesta del servidor
        this.clientes = data; //Asignamos los datos a la variable clientes
      },
      error: (err) => { 
        const mensaje = err.error?.mensaje || err.error?.error || 'Ocurrio un error al cargar los clientes';
        console.error(mensaje);
        Swal.fire({
          icon: 'error',
          title: 'Error de auntenticación',
          text: mensaje,
          confirmButtonText: 'Aceptar'
        }).then(() => {
          this.router.navigate(['login']);
        })
      }
    })
  }

  //Metodo para editar una tienda
  btnEditar(id: number){
    this.router.navigate(['editar-cliente', id]);
  }

  //Metodo para eliminar una tienda
  btnEliminar(id: number){
  Swal.fire({
    title: '¿Estás seguro de eliminar este cliente?',
    text: "¡Esta acción no se puede deshacer!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Sí, eliminarla'
  }).then((result) => {
    if (result.isConfirmed) {
      this.servicioC.eliminarCliente(id).subscribe({
        next: (response) => {
          // ← EXTRAE el STRING del objeto
          const mensaje = response.body?.message || response.body || 'El cliente ha sido eliminada correctamente';
          Swal.fire(
            'Eliminada',
            mensaje,  
            'success'
          );
          this.ngOnInit(); 
        },
        error: (err) => {
          const errorMsg = err.error?.mensaje || err.error || 'Hubo un problema al eliminar el cliente';
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
      text: 'El cliente no ha sido eliminado  ',
      showConfirmButton: false,
      timer: 1500
      });
    }
  });
}

}
