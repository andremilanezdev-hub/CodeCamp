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
    // Endereço salvo no cadastro do estudante após a consulta do CEP.
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private String ddd;
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
        this.cep = estudante.getCep();
        this.logradouro = estudante.getLogradouro();
        this.complemento = estudante.getComplemento();
        this.bairro = estudante.getBairro();
        this.localidade = estudante.getLocalidade();
        this.uf = estudante.getUf();
        this.ddd = estudante.getDdd();
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
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
