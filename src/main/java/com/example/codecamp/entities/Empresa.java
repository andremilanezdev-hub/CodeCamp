package com.example.codecamp.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
// Representa uma empresa e seus dados persistidos na tabela gerenciada pelo JPA.
public class Empresa {

    // Construtor vazio usado pelo JPA para criar objetos a partir dos registros do banco.
    public Empresa() {
    }

    // Identificador da empresa; o banco gera o valor ao inserir um novo registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome empresarial registrado oficialmente.
    private String razaoSocial;

    // Nome comercial pelo qual a empresa é conhecida.
    private String nomeFantasia;

    // CNPJ da empresa; a restrição unique impede CNPJs duplicados no banco.
    @Column(unique = true)
    private String cnpj;

    // Inscrição estadual da empresa.
    private String inscricaoEstaual;

    // Relação de um para muitos: uma empresa pode estar associada a vários usuários.
    // mappedBy indica que o relacionamento é controlado pelo campo "empresa" em Usuario.
    @OneToMany(mappedBy = "empresa")
    private List<Usuario> usuarios;


    // Retorna o identificador da empresa.
    public Long getId() {
        return id;
    }

    // Define o identificador da empresa.
    public void setId(Long id) {
        this.id = id;
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

    // Retorna os usuários associados à empresa.
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    // Define os usuários associados à empresa.
    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
