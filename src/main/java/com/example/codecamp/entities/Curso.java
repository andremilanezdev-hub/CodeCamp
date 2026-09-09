package com.example.codecamp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Curso {

    public Curso(){}

    public Curso(String nome, long cargahoraria, LocalDateTime dataCadastro, LocalDateTime dataAtualizacao, String status) {
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
    private long cargahoraria;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private String status;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Long getId() {
        return this.id;
    }

    public String getNome(){
        return this.nome;
    }
    public void setNome(String nome) {
        if (this.nome.isEmpty()) {
            this.nome = nome;
        }
    }

    public long getCargahoraria() {
        return cargahoraria;
    }
    public void setCargahoraria(long cargahoraria) {
        this.cargahoraria = cargahoraria;
    }
}
