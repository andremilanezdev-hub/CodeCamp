package com.example.codecamp.DTO;

import com.example.codecamp.entities.Curso;

import java.util.List;

/**
 * Formato de consulta de curso sem expor diretamente as entidades JPA.
 * Inclui o instrutor responsável e os estudantes matriculados, convertidos em DTOs para evitar
 * ciclos de serialização e manter o contrato HTTP independente dos detalhes de persistência.
 */
public class CursoConsultaResponse {

    private Long id;
    private String nome;
    private long cargahoraria;
    private String status;
    private UsuarioConsultaResponse usuarioCadastro;
    private List<EstudanteConsultaResponse> alunos;
    private List<ModuloConsultaResponse> modulos;

    public CursoConsultaResponse() {
        // Construtor vazio utilizado por frameworks de serialização.
    }

    /**
     * Copia os campos públicos do curso e transforma suas associações em DTOs.
     * O nome JSON "alunos" preserva o contrato usado pela listagem existente; os registros vêm
     * da associação Estudante-Curso, enquanto usuarioCadastro identifica quem criou o curso.
     */
    public CursoConsultaResponse(Curso curso) {
        this.id = curso.getId();
        this.nome = curso.getNome();
        this.cargahoraria = curso.getCargahoraria();
        this.status = curso.getStatus();
        this.usuarioCadastro = curso.getUsuarioCadastro() == null
                ? null
                : new UsuarioConsultaResponse(curso.getUsuarioCadastro());
        this.alunos = curso.getEstudantes().stream()
                .map(EstudanteConsultaResponse::new)
                .toList();
        // Cada módulo é convertido em DTO simples para expor a hierarquia sem serializar entidades JPA.
        this.modulos = curso.getModulos().stream()
                .map(ModuloConsultaResponse::new)
                .toList();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UsuarioConsultaResponse getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(UsuarioConsultaResponse usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public List<EstudanteConsultaResponse> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<EstudanteConsultaResponse> alunos) {
        this.alunos = alunos;
    }

    public List<ModuloConsultaResponse> getModulos() {
        return modulos;
    }

    public void setModulos(List<ModuloConsultaResponse> modulos) {
        this.modulos = modulos;
    }
}
