package com.example.codecamp.DTO;

public class AtualizaStatusModuloRequest {
    public AtualizaStatusModuloRequest() {
    }

    public String status;
    // ID do instrutor que está solicitando a alteração.
    private Long usuario_id;

    public Long getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(Long usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
