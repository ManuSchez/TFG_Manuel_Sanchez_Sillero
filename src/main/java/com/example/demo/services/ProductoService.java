package com.example.demo.services;

import com.example.demo.models.ProductoModel;
import com.example.demo.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public ProductoModel crearProducto(ProductoModel producto) {
        return productoRepository.save(producto);
    }

    public List<ProductoModel> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<ProductoModel> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public Optional<ProductoModel> actualizarProducto(Long id, ProductoModel actualizado) {
        return productoRepository.findById(id)
            .map(producto -> {
                producto.setNombre(actualizado.getNombre());
                producto.setCantidad(actualizado.getCantidad());
                producto.setPrecio(actualizado.getPrecio());
                return productoRepository.save(producto);
            });
    }
}
