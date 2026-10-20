package com.example.codecamp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
// Representa um estudante persistido no banco de dados.
public class Estudante {

    // Construtor vazio exigido pelo JPA para carregar registros do banco.
    public Estudante() {
    }

    // Construtor para criar estudante com os principais dados e informações de auditoria.
    public Estudante(String nome, String email, String cpf, LocalDateTime dataCadastro, LocalDateTime dataAtualizacao, String status) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
        this.status = status;
    }

    // Identificador único gerado pelo banco ao inserir o estudante.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Nome completo do estudante.
    private String nome;
    // E-mail de contato do estudante.
    private String email;
    // CPF do estudante.
    private String cpf;
    // Data e hora em que o cadastro foi criado.
    private LocalDateTime dataCadastro;
    // Data e hora da última atualização do cadastro.
    private LocalDateTime dataAtualizacao;
    // Situação do cadastro, por exemplo ativo (A) ou desativado (D).
    private String status;

    // Retorna a situação atual do cadastro.
    public String getStatus() {
        return status;
    }
    // Atualiza a situação do cadastro.
    public void setStatus(String status) {
        this.status = status;
    }

    // Retorna a data e hora da última atualização.
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    // Define a data e hora da última atualização.
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    // Retorna a data e hora de criação do cadastro.
    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
    // Define a data e hora de criação do cadastro.
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    // Retorna o identificador do estudante.
    public Long getId() {
        return this.id;
    }

    // Retorna o nome do estudante.
    public String getNome(){
        return this.nome;
    }
    // Define o nome do estudante.
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Retorna o e-mail do estudante.
    public String getEmail() {
        return email;
    }

    // Define o e-mail do estudante.
    public void setEmail(String email) {
        this.email = email;
    }

    // Retorna o CPF do estudante.
    public String getCpf() {
        return cpf;
    }

    // Define o CPF do estudante.
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}//Fim da classe principal
