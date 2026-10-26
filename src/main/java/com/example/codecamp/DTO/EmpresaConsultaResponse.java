package com.example.codecamp.DTO;

import com.example.codecamp.entities.Empresa;

public class EmpresaConsultaResponse {

    // Converte a entidade Empresa em um objeto de resposta para a API.
    public EmpresaConsultaResponse(Empresa empresa) {

        this.id = empresa.getId();
        this.razaoSocial = empresa.getRazaoSocial();
        this.nomeFantasia = empresa.getNomeFantasia();
        this.cnpj = empresa.getCnpj();
        this.inscricaoEstaual = empresa.getInscricaoEstaual();
        this.status = empresa.getStatus();
        // Conta os usuários associados à empresa; se a lista não estiver disponível, retorna zero.
        if(empresa.getUsuarios() != null){
            this.quantidadeUsuarios = empresa.getUsuarios().size();
        }else {
            this.quantidadeUsuarios = 0;
        }
    }



    // Identificador da empresa no banco.
    private Long id;
    // Nome empresarial oficial.
    private String razaoSocial;
    // Nome comercial da empresa.
    private String nomeFantasia;
    // Cadastro Nacional da Pessoa Jurídica.
    private String cnpj;
    // Inscrição estadual da empresa.
    private String inscricaoEstaual;
    // Total de usuários associados à empresa.
    private int quantidadeUsuarios;
    // Situação usada pelo frontend para distinguir empresas ativas das desativadas.
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Retorna o total de usuários associados.
    public int getQuantidadeUsuarios() {
        return quantidadeUsuarios;
    }

    // Define o total de usuários associado à empresa.
    public void setQuantidadeUsuarios(int quantidadeUsuarios) {
        this.quantidadeUsuarios = quantidadeUsuarios;
    }

    // Retorna a razão social.
    public String getRazaoSocial() {
        return razaoSocial;
    }
    // Define a razão social.
    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }
    // Retorna o nome fantasia.
    public String getNomeFantasia() {
        return nomeFantasia;
    }
    // Define o nome fantasia.
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }
    // Retorna o CNPJ.
    public String getCnpj() {
        return cnpj;
    }
    // Define o CNPJ.
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    // Retorna a inscrição estadual.
    public String getInscricaoEstaual() {
        return inscricaoEstaual;
    }
    // Define a inscrição estadual.
    public void setInscricaoEstaual(String inscricaoEstaual) {
        this.inscricaoEstaual = inscricaoEstaual;
    }
    // Retorna o ID da empresa.
    public Long getId() {
        return id;
    }
    // Define o ID da empresa.
    public void setId(Long id) {
        this.id = id;
    }
}
