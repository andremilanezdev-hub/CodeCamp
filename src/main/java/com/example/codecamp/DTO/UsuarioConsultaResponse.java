package com.example.codecamp.DTO;

import com.example.codecamp.entities.Usuario;

public class UsuarioConsultaResponse {



    public UsuarioConsultaResponse() {
    }

    // Converte um usuário em DTO e inclui os dados básicos da empresa vinculada, se houver.
    public UsuarioConsultaResponse(Usuario usuario){
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.dataNascimento = usuario.getDataNascimento();
        this.id = usuario.getId();
        this.status = usuario.getStatus();
        // A associação pode ser nula caso o usuário não esteja vinculado a uma empresa.
        if (usuario.getEmpresa() != null){
            this.empresa_id = usuario.getEmpresa().getId();
            this.razaoSocialEmprea = usuario.getEmpresa().getRazaoSocial();
        }
    }


    private String nome;
    private String cpf;
    private String dataNascimento;
    // ID da empresa associada ao usuário.
    private Long empresa_id;
    // Razão social da empresa associada ao usuário.
    private String razaoSocialEmprea;
    private Long id;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
