package com.example.codecamp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Modulo {

    public Modulo(){}

    public Modulo(String nome, Long cargahoraria, LocalDateTime dataCadastro, LocalDateTime dataAtualizacao, String status) {
        this.nome = nome;
        this.cargahoraria = cargahoraria;
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Long cargahoraria;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private String status;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getCargahoraria() {
        return cargahoraria;
    }

    public void setCargahoraria(Long cargahoraria) {
        this.cargahoraria = cargahoraria;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
