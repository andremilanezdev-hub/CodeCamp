package com.example.codecamp.DTO;

public class LoginResponse {

    public LoginResponse(){}
    private String mensagem;
    // Token que o cliente envia no cabeçalho Authorization das próximas requisições.
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
