package com.mx.Pedido.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mx.Pedido.Entity.EEstado;
import com.mx.Pedido.Entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
	
	//JPA nos proporciona los metodos CRUD basicos
	
	//Metodo para listar pedidos por clienteId
	List<Pedido> findByClienteId(Integer clienteId);
	
	// Consulta para traer los detalles de un pedido mediante el id del pedido
	@Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.detalles WHERE p.idPedido = :id")
    Pedido findByIdConDetalles(@Param("id") Integer id);
	
	//Metodo para listar por estatus
	List<Pedido> findByEstatus(EEstado estatus);

}
