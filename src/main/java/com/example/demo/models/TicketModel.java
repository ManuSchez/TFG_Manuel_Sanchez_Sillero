package com.example.demo.models;

import com.example.demo.models.enums.FormaPago;
import com.example.demo.models.enums.Supermercado;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Entity
@Table(name = "tickets")
public class TicketModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Supermercado supermercado;

    @JsonFormat(pattern = "yyyy-MM-dd") // JSON: formato consistente
    private LocalDate fecha;

    private BigDecimal precioTotal;

    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore // Previene recursión infinita en JSON
    private UsuarioModel usuario;

    @ManyToMany
    @JoinTable(
        name = "ticket_productos",
        joinColumns = @JoinColumn(name = "ticket_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private Set<ProductoModel> productos;

    public TicketModel() {}

    public TicketModel(String fechaStr, String supermercadoStr, Double precioTotal, String formaPagoStr) {
        setFechaFromString(fechaStr);
        setSupermercadoFromString(supermercadoStr);
        this.precioTotal = precioTotal != null ? BigDecimal.valueOf(precioTotal) : null;
        setFormaPagoFromString(formaPagoStr);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supermercado getSupermercado() { return supermercado; }
    public void setSupermercado(Supermercado supermercado) { this.supermercado = supermercado; }

    public void setSupermercadoFromString(String supermercadoStr) {
        this.supermercado = Supermercado.fromString(supermercadoStr);
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public void setFechaFromString(String fechaStr) {
        try {
            this.fecha = (fechaStr != null) ? LocalDate.parse(fechaStr, DateTimeFormatter.ISO_DATE) : null;
        } catch (Exception e) {
            this.fecha = null;
        }
    }

    public BigDecimal getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(BigDecimal precioTotal) { this.precioTotal = precioTotal; }

    public FormaPago getFormaPago() { return formaPago; }
    public void setFormaPago(FormaPago formaPago) { this.formaPago = formaPago; }

    public void setFormaPagoFromString(String formaPagoStr) {
        this.formaPago = FormaPago.fromString(formaPagoStr).orElse(null);
    }

    public UsuarioModel getUsuario() { return usuario; }
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }

    public Set<ProductoModel> getProductos() { return productos; }
    public void setProductos(Set<ProductoModel> productos) { this.productos = productos; }

    @Override
    public String toString() {
        return "TicketModel{" +
                "id=" + id +
                ", supermercado=" + supermercado +
                ", fecha=" + fecha +
                ", precioTotal=" + precioTotal +
                ", formaPago=" + formaPago +
                '}';
    }
}
