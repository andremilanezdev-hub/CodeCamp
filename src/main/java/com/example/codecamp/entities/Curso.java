package com.example.codecamp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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




    // Relacionamento legado entre usuários e cursos, mantido para compatibilidade com dados e respostas existentes.
    // As matrículas do domínio Estudante são persistidas pela relação estudantes/cursos abaixo.
    @ManyToMany(mappedBy = "cursos")
    @JsonIgnore
    private Set<Usuario> alunos = new HashSet<>();

    // Lado inverso da matrícula N:M; Estudante.cursos controla a tabela de junção.
    // JsonIgnore evita ciclos quando a entidade Curso é serializada diretamente pela API.
    @ManyToMany(mappedBy = "cursos")
    @JsonIgnore
    private Set<Estudante> estudantes = new HashSet<>();

    // Lado inverso da relação 1:N Curso-Módulo. A chave estrangeira curso_id fica em Modulo,
    // que é o lado proprietário; não há cascade para evitar apagar módulos automaticamente
    // quando uma operação sobre o curso não pretende removê-los.
    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Modulo> modulos = new HashSet<>();

    // Lado proprietário da relação 1:N: cada curso aponta para o instrutor que o cadastrou.
    // A chave estrangeira usuariocadastro_id fica na tabela de cursos.
    @ManyToOne
    @JoinColumn(name = "usuariocadastro_id")
    private Usuario usuarioCadastro;


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


    // Mantém os dois lados da relação legada Usuario-Curso coerentes em memória.
    public void adicionarUsuario(Usuario usuario){
        this.alunos.add(usuario);
        usuario.getCursos().add(this);
    }

    // Associa um estudante à matrícula nos dois lados da relação N:M.
    // O método é mantido como conveniência para chamadas existentes; a tabela é persistida
    // pelo lado proprietário Estudante.cursos.
    public void adicionarEstudante(Estudante estudante) {
        estudante.matricularEm(this);
    }

    public Set<Estudante> getEstudantes() {
        return estudantes;
    }

    public void setEstudantes(Set<Estudante> estudantes) {
        this.estudantes = estudantes;
    }

    public Set<Modulo> getModulos() {
        return modulos;
    }

    public void setModulos(Set<Modulo> modulos) {
        this.modulos = modulos;
    }

    /**
     * Mantém a coleção inversa sincronizada ao associar um módulo a este curso.
     * O vínculo que persiste no banco é gravado pelo campo Modulo.curso, evitando duplicar
     * a definição da chave estrangeira nos dois lados do relacionamento JPA.
     */
    public void adicionarModulo(Modulo modulo) {
        if (modulo == null) {
            throw new IllegalArgumentException("O módulo é obrigatório");
        }
        modulo.setCurso(this);
    }
}
