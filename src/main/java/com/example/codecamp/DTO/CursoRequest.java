package com.example.codecamp.DTO;

import java.time.LocalDateTime;

public class CursoRequest {

    public CursoRequest(){}

    private String nome;
    private long cargahoraria;
    private String status;

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public Long getCargahoraria() {return cargahoraria;}
    public void setCargahoraria(Long Cargahoraria) {this.cargahoraria = cargahoraria;}

}
