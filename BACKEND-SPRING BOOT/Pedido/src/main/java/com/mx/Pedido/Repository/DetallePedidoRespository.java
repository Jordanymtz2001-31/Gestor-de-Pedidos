package com.mx.Pedido.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mx.Pedido.Entity.Detalle_Pedido;


@Repository
public interface DetallePedidoRespository extends JpaRepository<Detalle_Pedido, Integer> {
	
	//JPA nos proporciona los metodos CRUD basicos
	


}
