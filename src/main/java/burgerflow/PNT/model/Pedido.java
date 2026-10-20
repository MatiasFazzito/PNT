package burgerflow.PNT.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteEmail;
    private String clienteNombre;
    // Email del empleado/admin que cargó el pedido (null si lo hizo el propio cliente)
    private String cargadoPor;
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;
    private Double total;
    private String tipoEntrega;
    private String direccionEntrega;

    @Column(length = 500)
    private String observaciones;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetallePedido> items = new ArrayList<>();

    public Pedido() {} // Requerido por JPA

    public Pedido(Long id, String clienteEmail, EstadoPedido estado, Double total, String tipoEntrega) {
        this.id = id;
        this.clienteEmail = clienteEmail;
        this.estado = estado;
        this.total = total;
        this.tipoEntrega = tipoEntrega;
    }

    public void agregarItem(DetallePedido item) {
        item.setPedido(this);
        items.add(item);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getCargadoPor() { return cargadoPor; }
    public void setCargadoPor(String cargadoPor) { this.cargadoPor = cargadoPor; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }

    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<DetallePedido> getItems() { return items; }
    public void setItems(List<DetallePedido> items) { this.items = items; }
}
