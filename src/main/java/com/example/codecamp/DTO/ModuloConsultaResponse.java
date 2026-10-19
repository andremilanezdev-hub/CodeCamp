package com.example.codecamp.DTO;

import com.example.codecamp.entities.Modulo;

public class ModuloConsultaResponse {

    public ModuloConsultaResponse(Modulo modulo){

        this.id = getId();
        this.nome = getNome();
        this.cargahoraria = getCargahoraria();
        this.empresa_id = getEmpresa_id();
        this.razaosocialEmpresa = getRazaosocialEmpresa();
    }

    private Long id;
    private String nome;
    private int cargahoraria;
    private Long empresa_id;
    private String razaosocialEmpresa;


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

    public int getCargahoraria() {
        return cargahoraria;
    }

    public void setCargahoraria(int cargahoraria) {
        this.cargahoraria = cargahoraria;
    }

    public Long getEmpresa_id() {
        return empresa_id;
    }

    public void setEmpresa_id(Long empresa_id) {
        this.empresa_id = empresa_id;
    }

    public String getRazaosocialEmpresa() {
        return razaosocialEmpresa;
    }

    public void setRazaosocialEmpresa(String razaosocialEmpresa) {
        this.razaosocialEmpresa = razaosocialEmpresa;
    }
}
