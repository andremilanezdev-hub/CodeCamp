package com.example.codecamp.DTO;

import java.time.LocalDateTime;

// Transporta os dados recebidos pela API para criar ou atualizar um estudante.
public class EstudanteRequest {

    // Construtor vazio usado pelo Spring/Jackson ao converter o JSON recebido.
    public EstudanteRequest(){}

    // ID opcional; os endpoints atuais identificam o registro pela URL.
    private Long id;
    // Nome enviado para cadastro ou atualização.
    private String nome;
    // E-mail enviado para cadastro ou atualização.
    private String email;
    // CPF enviado para cadastro ou atualização.
    private String cpf;
    // Campos de auditoria mantidos no DTO conforme o padrão atual do projeto.
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    // Situação do cadastro, usada por operações específicas.
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
