package com.example.codecamp.DTO;

// Formato das respostas de sucesso das operações de cadastro e atualização.
public class EstudanteResponse {

    // Construtor vazio para desserialização e criação por frameworks.
    public EstudanteResponse(){}

    // Cria uma resposta com o ID do estudante e uma mensagem informativa.
    public EstudanteResponse(Long id, String mensagem) {
        this.id = id;
        this.mensagem = mensagem;
    }

    // ID do estudante afetado pela operação.
    private Long id;
    // Resultado em texto da operação.
    private String mensagem;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getMensagem() { return mensagem;}
    public void setMensagem(String mensagem) {this.mensagem = mensagem;}
}
