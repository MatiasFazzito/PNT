package burgerflow.PNT.model;

public class Pedido {
    private Long id;
    private String clienteEmail;
    private String estado;
    private double total;
    private String tipoEntrega;

    public Pedido() {}

    public Pedido(Long id, String clienteEmail, String estado, double total, String tipoEntrega) {
        this.id = id;
        this.clienteEmail = clienteEmail;
        this.estado = estado;
        this.total = total;
        this.tipoEntrega = tipoEntrega;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }
}
