package com.example.codecamp.DTO;

public class EmpresaRequest {

    public EmpresaRequest() {
    }

    private String razaoSocial;

    private String nomeFantasia;

    private String cnpj;

    private String inscricaoEstaual;

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
}
