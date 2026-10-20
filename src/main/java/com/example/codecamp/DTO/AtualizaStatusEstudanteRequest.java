package com.example.codecamp.DTO;

// Transporta a nova situação solicitada para um estudante.
public class AtualizaStatusEstudanteRequest {

    // Construtor vazio usado pelo Spring/Jackson ao converter o JSON recebido.
    public AtualizaStatusEstudanteRequest() {
    }

    // Novo código de situação do cadastro, por exemplo ativo (A) ou desativado (D).
    public String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
