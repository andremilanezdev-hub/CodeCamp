package com.example.codecamp.DTO;

public class EmpresaResponse {

    // Construtor vazio para permitir que o controller monte a resposta usando setters.
    public EmpresaResponse() {
    }

    // ID gerado pelo banco para a empresa cadastrada.
    private Long id;

    // Mensagem informativa sobre o resultado da operação.
    private String mensagem;

    // Retorna o ID da empresa incluído na resposta.
    public Long getId() {
        return id;
    }

    // Define o ID da empresa incluído na resposta.
    public void setId(Long id) {
        this.id = id;
    }

    // Retorna a mensagem da resposta.
    public String getMensagem() {
        return mensagem;
    }

    // Define a mensagem da resposta.
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
