package com.Farmaline.Farmaline.repository;

import com.Farmaline.Farmaline.model.Carrito_Anadido;
import com.Farmaline.Farmaline.model.Carrito; // Importar si necesitas buscar por el objeto Carrito directamente
import com.Farmaline.Farmaline.model.Producto; // Importar si necesitas buscar por el objeto Producto directamente
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Carrito_AnadidoRepository extends JpaRepository<Carrito_Anadido, Integer> {

    List<Carrito_Anadido> findByCarrito(Carrito carrito);

    List<Carrito_Anadido> findByCarritoIdCarrito(Integer carritoId);

    Optional<Carrito_Anadido> findByCarritoIdCarritoAndProductoIdProducto(Integer carritoId, Integer productoId);

    List<Carrito_Anadido> findByProducto(Producto producto);

    List<Carrito_Anadido> findByProductoIdProducto(Integer productoId);

    List<Carrito_Anadido> findByCantidadGreaterThan(int cantidad);
}