package com.example.codecamp.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//Tag Informa que é uma tabela no banco
@Entity
public class Curso {

    public Curso(){}
    //Atributos da entidade
    //Tag Identifica que é o identificador da tabela
    @Id
    //Tag para Gerador auto incremento
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Tag para o Campo não nulo
    @Column(nullable = false)
    private String nome;
    private long cargahoraria;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private String status;




    //Lista de alunos sem ficar recriando os itens, apenas será alterado um registro sem ipactar quqlquer outro registro
    @ManyToMany(mappedBy = "cursos")
    private Set<Usuario> alunos = new HashSet<>();
    //Relacionamento 1:1
    @OneToOne
    @JoinColumn(name = "usuariocadastro_id")
    private  Usuario usuarioCadastro;


    //Get and Seters


    public Set<Usuario> getAlunos() {
        return alunos;
    }

    public void setAlunos(Set<Usuario> alunos) {
        this.alunos = alunos;
    }

    public Usuario getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(Usuario usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

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

    public long getCargahoraria() {
        return cargahoraria;
    }

    public void setCargahoraria(long cargahoraria) {
        this.cargahoraria = cargahoraria;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    //Adicionar aluno
    public void adicionarUsuario(Usuario usuario){
        this.alunos.add(usuario);
        usuario.getCursos().add(this);
    }
}
