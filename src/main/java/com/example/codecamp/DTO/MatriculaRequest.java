package com.example.codecamp.DTO;

public class MatriculaRequest {

    public MatriculaRequest() {
    }

    // ID do instrutor informado para a validação da matrícula.
    private Long usuario_id;
    // ID do estudante que será matriculado.
    private Long estudante_id;
    // ID do curso de destino.
    private Long curso_id;

    public Long getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(Long usuario_id) {
        this.usuario_id = usuario_id;
    }

    public Long getEstudante_id() {
        return estudante_id;
    }

    public void setEstudante_id(Long estudante_id) {
        this.estudante_id = estudante_id;
    }

    public Long getCurso_id() {
        return curso_id;
    }

    public void setCurso_id(Long curso_id) {
        this.curso_id = curso_id;
    }
}
