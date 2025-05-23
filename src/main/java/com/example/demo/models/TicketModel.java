package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public TicketModel() {
    }

    // Constructor simple para crear con datos básicos (útil desde el parseo IA)
    public TicketModel(String fechaStr, String supermercadoStr, Double precioTotal, String formaPagoStr) {
        setFechaFromString(fechaStr);
        setSupermercadoFromString(supermercadoStr);
        this.precioTotal = precioTotal;
        setFormaPagoFromString(formaPagoStr);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supermercado getSupermercado() { return supermercado; }
    public void setSupermercado(Supermercado supermercado) { this.supermercado = supermercado; }

    // Setter desde String para supermercado con mapeo seguro
    public void setSupermercadoFromString(String supermercadoStr) {
        if (supermercadoStr == null) {
            this.supermercado = null;
            return;
        }
        try {
            this.supermercado = Supermercado.valueOf(supermercadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.supermercado = null;
        }
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    // Setter para fecha desde String con formato ISO (ajustable)
    public void setFechaFromString(String fechaStr) {
        if (fechaStr == null) {
            this.fecha = null;
            return;
        }
        try {
            this.fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            this.fecha = null;
        }
    }

    public Double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(Double precioTotal) { this.precioTotal = precioTotal; }

    public FormaPago getFormaPago() { return formaPago; }
    public void setFormaPago(FormaPago formaPago) { this.formaPago = formaPago; }

    // Setter desde String para formaPago con mapeo seguro
    public void setFormaPagoFromString(String formaPagoStr) {
        if (formaPagoStr == null) {
            this.formaPago = null;
            return;
        }
        try {
            this.formaPago = FormaPago.valueOf(formaPagoStr.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            this.formaPago = null;
        }
    }

    public UsuarioModel getUsuario() { return usuario; }
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }

    public Set<ProductoModel> getProductos() { return productos; }
    public void setProductos(Set<ProductoModel> productos) { this.productos = productos; }
}
