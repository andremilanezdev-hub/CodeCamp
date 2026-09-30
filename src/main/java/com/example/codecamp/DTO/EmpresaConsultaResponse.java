package com.example.codecamp.DTO;

import com.example.codecamp.entities.Empresa;

public class EmpresaConsultaResponse {

    public EmpresaConsultaResponse(Empresa empresa) {

        this.id = getId();
        this.razaoSocial = getRazaoSocial();
        this.nomeFantasia = getNomeFantasia();
        this.cnpj = getCnpj();
        this.inscricaoEstaual = getInscricaoEstaual();
        if(empresa.getUsuarios() != null){
            this.quantidadeUsuarios = empresa.getUsuarios().size();
        }else {
            this.quantidadeUsuarios = 0;
        }
    }



    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstaual;
    private int quantidadeUsuarios;

    //Get and Seter

    public int getQuantidadeUsuarios() {
        return quantidadeUsuarios;
    }

    public void setQuantidadeUsuarios(int quantidadeUsuarios) {
        this.quantidadeUsuarios = quantidadeUsuarios;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }
    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }
    public String getNomeFantasia() {
        return nomeFantasia;
    }
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }
    public String getCnpj() {
        return cnpj;
    }
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    public String getInscricaoEstaual() {
        return inscricaoEstaual;
    }
    public void setInscricaoEstaual(String inscricaoEstaual) {
        this.inscricaoEstaual = inscricaoEstaual;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
