package com.example.codecamp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Usuario {

    public Usuario(){}


    public Usuario(String nome, String cpf, String dataNascimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpf;
    private String dataNascimento;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private String status;
    private String senha;

    @ManyToOne
    @JoinColumn(name = "empresa_id", referencedColumnName = "id")
    private Empresa empresa;

    // Relação legada que representa cursos associados diretamente a Usuario.
    // A matrícula do domínio Estudante utiliza Estudante.cursos; este campo é mantido
    // para não quebrar a tabela Usuario_cursos já existente no banco.
    @ManyToMany(fetch = FetchType.LAZY)
    //EAGER: Traz as informação quando usuario for consultado
    //LAZy: Necessidade, só carrega se for necessário
    @JoinTable(
            //Necesita dessas 3 parametros
            name = "Usuario_cursos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    @JsonIgnore
    private Set<Curso> cursos = new HashSet<>();

    // Lado inverso da relação instrutor 1:N cursos cadastrados.
    // Curso.usuarioCadastro é o lado proprietário e guarda a chave estrangeira.
    // JsonIgnore impede serialização recursiva quando um curso expõe seu instrutor.
    @OneToMany(mappedBy = "usuarioCadastro", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Curso> cursosCadastrados = new HashSet<>();


    public Set<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(Set<Curso> cursos) {
        this.cursos = cursos;
    }

    public Set<Curso> getCursosCadastrados() {
        return cursosCadastrados;
    }

    public void setCursosCadastrados(Set<Curso> cursosCadastrados) {
        this.cursosCadastrados = cursosCadastrados;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Long getId() {
        return this.id;
    }

    public String getNome(){
        return this.nome;
    }
    public void setNome(String nome) {
            this.nome = nome;
    }

    public String getCpf(){
        return this.cpf;
    }
    public void setCpf(String cpf){
        this.cpf = cpf;
    }

    public String getDataNascimento(){
        return this.dataNascimento;
    }
    public void setDataNascimento(String dataNascimento){
        this.dataNascimento = dataNascimento;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
