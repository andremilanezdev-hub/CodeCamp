package com.example.codecamp.DTO;

import com.example.codecamp.entities.Usuario;

public class UsuarioConsultaResponse {



    public UsuarioConsultaResponse() {
    }

    public UsuarioConsultaResponse(Usuario usuario){
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.id = id;
        if (usuario.getEmpresa() != null){
            this.empresa_id = usuario.getEmpresa().getId();
            this.razaoSocialEmprea = usuario.getEmpresa().getRazaoSocial();
        }
    }


    private String nome;
    private String cpf;
    private String dataNascimento;
    private Long empresa_id;
    private String razaoSocialEmprea;
    private Long id;

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Long getEmpresa_id() {
        return empresa_id;
    }

    public void setEmpresa_id(Long empresa_id) {
        this.empresa_id = empresa_id;
    }

    public String getRazaoSocialEmprea() {
        return razaoSocialEmprea;
    }

    public void setRazaoSocialEmprea(String razaoSocialEmprea) {
        this.razaoSocialEmprea = razaoSocialEmprea;
    }
}
