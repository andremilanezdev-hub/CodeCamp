package com.example.codecamp.DTO;

import com.example.codecamp.entities.Estudante;

import java.time.LocalDateTime;

// DTO usado para devolver os dados do estudante nas consultas.
public class EstudanteConsultaResponse {

    // Identificador do estudante.
    private Long id;
    // Nome completo do estudante.
    private String nome;
    // E-mail de contato.
    private String email;
    // CPF do estudante.
    private String cpf;
    // Data e hora de criação do cadastro.
    private LocalDateTime dataCadastro;
    // Data e hora da última atualização.
    private LocalDateTime dataAtualizacao;
    // Situação atual do cadastro.
    private String status;

    // Copia os campos da entidade para o formato de resposta da API.
    public EstudanteConsultaResponse(Estudante estudante) {
        this.id = estudante.getId();
        this.nome = estudante.getNome();
        this.email = estudante.getEmail();
        this.cpf = estudante.getCpf();
        this.dataCadastro = estudante.getDataCadastro();
        this.dataAtualizacao = estudante.getDataAtualizacao();
        this.status = estudante.getStatus();
    }

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
