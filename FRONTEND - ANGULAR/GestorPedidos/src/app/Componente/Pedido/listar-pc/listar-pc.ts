import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ServidorP } from '../../../Servidor/PedidoYDetalles/servidorP';
import { Pedido } from '../../../Entidad/pedido';
import { CommonModule } from '@angular/common';
import { ServidorC } from '../../../Servidor/Cliente/servidorC';
import { Cliente } from '../../../Entidad/cliente';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-listar-pc',
  imports: [FormsModule, CommonModule],
  templateUrl: './listar-pc.html',
  styleUrl: './listar-pc.css',
})
export class ListarPC implements OnInit {

   //Set para controlar múltiples menús abiertos simultáneamente
  //Puede contener varios valores de tipo string que representan los menús abiertos
  protected openMenus = new Set<string>();

  //Creamos nuestro contructor y le inyectamos
  constructor(private router: Router, private servicioP : ServidorP, private servicioC : ServidorC, private changeDetectorRef: ChangeDetectorRef) {}

  //Pedido cuyos detalles se van a mostrar
  pedidoSeleccionado?: Pedido;

  //Creamos variables para almacenar los datos que vienen del servidor
  pedidos!: Pedido[];
  pedido = new Pedido(); //Inicializamos un objeto pedido para usarlor
  // Array de estados posibles
  estadosPosibles = ['CREADO', 'CONFIRMADO', 'CANCELADO', 'ENTREGADO'];

  loading: boolean = false;//Bandera que indica si hay una operación en curso (cargando datos o guardando)
  clientes: Cliente[] = [];  // Lista completa

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
      //Carga la lista de los clientes de los pedidos para no colocar el id
      this.servicioC.ListarClientes().subscribe(clientes => {
        this.clientes = clientes;
      });

      //Tanto cuando se inicie el componente, se cargan los pedidos
      this.servicioP.ListarPedidosCancelados().subscribe({
        next: (data) => { //data es la respuesta del servidor
          this.pedidos = data; //Asignamos los datos a la variable pedidos
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
  
  // Editar estatus desde la lista
  editarEstatus(pedido: Pedido, nuevoEstatus: string): void {
    // Copia del pedido original (NO mutar el original)
    const pedidoActualizar = { ...pedido, estatus: nuevoEstatus };
    
    Swal.fire({
      title: `¿Cambiar estatus a "${nuevoEstatus}"?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, actualizar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.servicioP.actualizarPedido(pedidoActualizar).subscribe({
          next: () => {
            // Actualizar LOCALMENTE (sin recargar toda la lista)
            pedido.estatus = nuevoEstatus;
            Swal.fire({
              icon: 'success',
              title: '¡Estatus actualizado!',
              timer: 1500,
              showConfirmButton: false
            });
          },
          error: (err) => {
            Swal.fire({
              icon: 'error',
              title: 'Error al actualizar',
              text: err.error?.mensaje || 'Inténtalo de nuevo'
            });
          }
        });
      }
    });
  }
  
    //Metodo para eliminar un pedido
    btnEliminar(id: number){
      Swal.fire({
        title: '¿Estás seguro de eliminar este Pedido?',
        text: "¡Esta acción no se puede deshacer!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminarla'
      }).then((result) => {
        if (result.isConfirmed) {
          this.servicioP.eliminarPedido(id).subscribe({
            next: (response) => {
              const mensaje = response.body?.message || response.body || 'El pedido ha sido eliminada correctamente';
              Swal.fire(
                'Eliminada',
                mensaje,
                'success'
              );
              this.ngOnInit(); //Recargamos la lista de pedidos despues de eliminar
            },
            error: (err) => {
              const errorMsg = err.error?.mensaje || err.error || 'Hubo un problema al eliminar el pedido';
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
            text: 'El Pedido no ha sido eliminado',
            showConfirmButton: false,
            timer: 1500
          });
        }
      });
    }
  //Metodo para ver los detalles
  btnDetalles(pedido: Pedido) {
    this.pedidoSeleccionado = pedido;
  }

  //CERRAR MODAL
  cerrarDetalle() {
    this.pedidoSeleccionado = undefined;
  }
  
}