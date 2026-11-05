package com.example.codecamp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/** Certificado simulado emitido após a conclusão de todos os módulos de um curso. */
@Entity
@Table(name = "certificado", uniqueConstraints = {
        @UniqueConstraint(name = "uk_certificado_estudante_curso",
                columnNames = {"estudante_id", "curso_id"})
})
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudante_id", nullable = false)
    private Estudante estudante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "codigo_validacao", nullable = false, unique = true, length = 36)
    private String codigoValidacao;

    @Column(nullable = false)
    private LocalDateTime dataEmissao;

    protected Certificado() {
    }

    public Certificado(Estudante estudante, Curso curso, String codigoValidacao, LocalDateTime dataEmissao) {
        this.estudante = estudante;
        this.curso = curso;
        this.codigoValidacao = codigoValidacao;
        this.dataEmissao = dataEmissao;
    }

    public Long getId() {
        return id;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public Curso getCurso() {
        return curso;
    }

    public String getCodigoValidacao() {
        return codigoValidacao;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }
}
