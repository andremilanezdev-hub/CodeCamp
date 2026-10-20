package com.example.codecamp.DTO;

public class EmpresaRequest {

    // Construtor vazio usado pelo Spring/Jackson ao converter o JSON recebido em objeto.
    public EmpresaRequest() {
    }

    // Nome empresarial oficial enviado no cadastro.
    private String razaoSocial;

    // Nome comercial da empresa.
    private String nomeFantasia;

    // Cadastro Nacional da Pessoa Jurídica.
    private String cnpj;

    // Inscrição estadual da empresa.
    private String inscricaoEstaual;

    // Retorna a razão social recebida na requisição.
    public String getRazaoSocial() {
        return razaoSocial;
    }

    // Define a razão social recebida na requisição.
    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    // Retorna o nome fantasia recebido na requisição.
    public String getNomeFantasia() {
        return nomeFantasia;
    }

    // Define o nome fantasia recebido na requisição.
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    // Retorna o CNPJ recebido na requisição.
    public String getCnpj() {
        return cnpj;
    }

    // Define o CNPJ recebido na requisição.
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    // Retorna a inscrição estadual recebida na requisição.
    public String getInscricaoEstaual() {
        return inscricaoEstaual;
    }

    // Define a inscrição estadual recebida na requisição.
    public void setInscricaoEstaual(String inscricaoEstaual) {
        this.inscricaoEstaual = inscricaoEstaual;
    }
}
