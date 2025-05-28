package com.example.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "productos")
public class ProductoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    @PositiveOrZero(message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    @PositiveOrZero(message = "El precio no puede ser negativo")
    private Double precio;

    @ManyToMany(mappedBy = "productos")
    @JsonIgnore
    private Set<TicketModel> tickets;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Set<TicketModel> getTickets() { return tickets; }
    public void setTickets(Set<TicketModel> tickets) { this.tickets = tickets; }
}
