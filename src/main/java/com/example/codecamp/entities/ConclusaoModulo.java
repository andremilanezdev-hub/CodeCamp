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

/** Persiste a conclusão individual de um módulo por um estudante. */
@Entity
@Table(name = "conclusao_modulo", uniqueConstraints = {
        @UniqueConstraint(name = "uk_conclusao_estudante_modulo",
                columnNames = {"estudante_id", "modulo_id"})
})
public class ConclusaoModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudante_id", nullable = false)
    private Estudante estudante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    @Column(nullable = false)
    private boolean concluido;

    @Column(nullable = false)
    private LocalDateTime dataConclusao;

    protected ConclusaoModulo() {
    }

    public ConclusaoModulo(Estudante estudante, Modulo modulo, LocalDateTime dataConclusao) {
        this.estudante = estudante;
        this.modulo = modulo;
        this.concluido = true;
        this.dataConclusao = dataConclusao;
    }

    public Long getId() {
        return id;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }
}
