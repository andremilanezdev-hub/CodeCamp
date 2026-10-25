package com.example.codecamp.DTO;

import com.example.codecamp.entities.Modulo;

public class ModuloConsultaResponse {

    public ModuloConsultaResponse(Modulo modulo){

        this.id = modulo.getId();
        this.nome = modulo.getNome();
        this.status = modulo.getStatus();
        this.cargahoraria = modulo.getCargahoraria() == null
                ? 0
                : modulo.getCargahoraria().intValue();
        // Expõe os dados mínimos do curso sem serializar a entidade e criar referência circular.
        if (modulo.getCurso() != null) {
            this.curso_id = modulo.getCurso().getId();
            this.cursoNome = modulo.getCurso().getNome();
        }
        if (modulo.getEmpresa() != null) {
            this.empresa_id = modulo.getEmpresa().getId();
            this.razaosocialEmpresa = modulo.getEmpresa().getRazaoSocial();
        }
    }

    private Long id;
    private String nome;
    private int cargahoraria;
    private String status;
    private Long curso_id;
    private String cursoNome;
    private Long empresa_id;
    private String razaosocialEmpresa;


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

    public int getCargahoraria() {
        return cargahoraria;
    }

    public void setCargahoraria(int cargahoraria) {
        this.cargahoraria = cargahoraria;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCurso_id() {
        return curso_id;
    }

    public void setCurso_id(Long curso_id) {
        this.curso_id = curso_id;
    }

    public String getCursoNome() {
        return cursoNome;
    }

    public void setCursoNome(String cursoNome) {
        this.cursoNome = cursoNome;
    }

    public Long getEmpresa_id() {
        return empresa_id;
    }

    public void setEmpresa_id(Long empresa_id) {
        this.empresa_id = empresa_id;
    }

    public String getRazaosocialEmpresa() {
        return razaosocialEmpresa;
    }

    public void setRazaosocialEmpresa(String razaosocialEmpresa) {
        this.razaosocialEmpresa = razaosocialEmpresa;
    }
}
