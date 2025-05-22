package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import com.example.demo.models.enums.Supermercado;
import com.example.demo.models.enums.FormaPago;

@Entity
@Table(name = "tickets")
public class TicketModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Supermercado supermercado;

    private LocalDate fecha;

    private Double precioTotal;

    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioModel usuario;

    @ManyToMany
    @JoinTable(
        name = "ticket_productos",
        joinColumns = @JoinColumn(name = "ticket_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private Set<ProductoModel> productos;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supermercado getSupermercado() { return supermercado; }
    public void setSupermercado(Supermercado supermercado) { this.supermercado = supermercado; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(Double precioTotal) { this.precioTotal = precioTotal; }

    public FormaPago getFormaPago() { return formaPago; }
    public void setFormaPago(FormaPago formaPago) { this.formaPago = formaPago; }

    public UsuarioModel getUsuario() { return usuario; }
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }

    public Set<ProductoModel> getProductos() { return productos; }
    public void setProductos(Set<ProductoModel> productos) { this.productos = productos; }
}
