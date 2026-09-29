package burgerflow.PNT.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteEmail;
    private String estado;
    private Double total;
    private String tipoEntrega;

    public Pedido() {} // Requerido por JPA

    public Pedido(Long id, String clienteEmail, String estado, Double total, String tipoEntrega) {
        this.id = id;
        this.clienteEmail = clienteEmail;
        this.estado = estado;
        this.total = total;
        this.tipoEntrega = tipoEntrega;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }
}