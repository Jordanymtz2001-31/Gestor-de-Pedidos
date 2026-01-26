import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ServidorP } from '../../../Servidor/PedidoYDetalles/servidorP';
import { Pedido } from '../../../Entidad/pedido';
import Swal from 'sweetalert2';
import { ServidorC } from '../../../Servidor/Cliente/servidorC';
import { Cliente } from '../../../Entidad/cliente';
import { PedidoGuardarDto } from '../../../Entidad/PedidoGuardarDto';
import { DetalleGuardarDto } from '../../../Entidad/DetalleGuardarDto';
import { Producto } from '../../../Entidad/producto';
import { ServidorPr } from '../../../Servidor/Producto/servidorPr';
import { CommonModule } from '@angular/common';
import { ServidorAuth } from '../../../Auth/servidor-auth';

@Component({
  selector: 'app-guardar-p',
  imports: [FormsModule, CommonModule],
  templateUrl: './guardar-p.html',
  styleUrl: './guardar-p.css',
})
export class GuardarP implements OnInit{

  //Contructor para inicializar con sus inyecciones
  constructor (private auth: ServidorAuth ,private router: Router,private servicioPr: ServidorPr, private servicioP: ServidorP, private servicioC: ServidorC, private changeDetectorRef: ChangeDetectorRef) {}

  
  Pedido: PedidoGuardarDto  = new PedidoGuardarDto(); //Intancia de la clase PedidoGuardarDto
  NuevoDetalle: DetalleGuardarDto = new DetalleGuardarDto(); //Intancia de la clase DetalleGuardarDto

  loading: boolean = false;//Bandera que indica si hay una operación en curso (cargando datos o guardando)
  clientes: Cliente[] = [];  // Lista de clientes
  productos: Producto[] = []; // Lista de productos
  producto = new Producto();

  //Metodo de OnInit
  ngOnInit(): void {
    this.Pedido.detalles = []; // INICIALIZAR EL ARRAY
    this.cargarClientes(); //Cargamos los clientes
    this.cargarProductos(); //Cargamos los productos
    
  }

  // Metodo para cargar los clientes
  cargarClientes() {
    console.log('DEBUG - isLogged:', this.auth.isLogeo()); // ← AGREGAR
    console.log('DEBUG - getAuthHeader:', this.auth.getAuthHeader());
    this.loading = true; // Activar el estado de carga
    this.servicioC.ListarClientes().subscribe({
      //Se suscribe al Observable para recibir la respuesta
      next: (data) => {
        console.log('Clientes cargados:', data); // ← AGREGAR
        this.clientes = data;//Guarda los datos en el array
        this.loading = false;//Desactiva el estado de carga
        this.changeDetectorRef.detectChanges();// Fuerza a Angular a actualizar la vista
      },
      error: (err) => {
        console.error('Error al cargar los clientes', err);
        console.error('Error clientes:', err);
        this.loading = false;
        //Muestra error en consola.Desactiva el estado de carga
      }
    });
  }
  // Metodo para cargar los productos
  cargarProductos() {
    console.log('DEBUG - isLogged:', this.auth.isLogeo()); // ← AGREGAR
    console.log('DEBUG - getAuthHeader:', this.auth.getAuthHeader());
    this.loading = true; // Activar el estado de carga
    this.servicioPr.ListarProductos().subscribe({
      //Se suscribe al Observable para recibir la respuesta
      next: (data) => {
        console.log('Productos cargados:', data);
        this.productos = data;//Guarda los datos en el array
        this.loading = false;//Desactiva el estado de carga
        this.changeDetectorRef.detectChanges();// Fuerza a Angular a actualizar la vista
      },
      error: (err) => {
        console.error('Error al cargar los productos', err);
        this.loading = false;
        //Muestra error en consola.Desactiva el estado de carga
      }
    });
  }

  //Metodo para agregar Detalles y validaciones
  agregarDetalle(): void {

    // Creamos dos constantes: productoId y producto
    const productoId = parseInt(String(this.NuevoDetalle.productoId), 10); // Este funciona para cambiar de string a number
    const producto = this.productos.find(p => p.idProducto === productoId); // Busca el producto en la lista de productos que coincida con el ID de productoId de Detalles
    
    if (!producto) { // Si es diferente 
      Swal.fire('Error', `Producto no encontrado`, 'error');
      return;
    }

    // TODO en un paso: precio DEL PRODUCTO directo
    const detalle: DetalleGuardarDto = { // Metemos el productoId, cantidad y precioUnitario a la clase de DetalleGuardarDto
      productoId,
      cantidad: this.NuevoDetalle.cantidad,
      precioUnitario: producto.precio  // Aqui asignas el precio del producto
    };

    this.Pedido.detalles.push(detalle); // Y de ahi agregamos el detalle completo a la lista de detalle de la clase pedido
    this.recalcularTotal();
    this.NuevoDetalle = new DetalleGuardarDto(); // Reset
  }

  recalcularTotal(): void {
    this.Pedido.total = this.Pedido.detalles.reduce((acc, d) => acc + (d.cantidad * d.precioUnitario), 0);
  }

  eliminarDetalle(det: DetalleGuardarDto): void {
    this.Pedido.detalles = this.Pedido.detalles.filter(d => d !== det);
    this.recalcularTotal();
  }


  //Boton de Guardar
  btnGuardar(): void{
    //Primero validamos que se haya seleccionado un cliente y que haya al menos un detalle
    if (!this.Pedido.clienteId || this.Pedido.detalles.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Datos incompletos',
        text: 'Selecciona un cliente y agrega al menos un detalle'
      });
      return;
    }
    // Si la validacion pasa, guardamos el pedido
    this.servicioP.guardarPedido(this.Pedido).subscribe({
      next: (response: any) =>{
      Swal.fire({
        title: 'Guardado...✓',
        text: response.body.message || 'Pedido guardado con éxito',
        icon: 'success',
        showConfirmButton: false,
        timer: 1500
      }),
        this.router.navigate(['listar-pedidos']); //Navegamos a la lista de Pedidos despues de guardar
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err.error?.mensaje || 'Error desconocido', //Mostramos el error del backend
          confirmButtonText: 'Aceptar'
        });
      }
    });
    }
  }