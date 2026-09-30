package com.example.codecamp.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Empresa {

    public Empresa() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    //Define que é unico no banco
    @Column(unique = true)
    private String cnpj;
    private String inscricaoEstaual;
    @OneToMany(mappedBy = "empresa")
    private List<Usuario> usuarios;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
