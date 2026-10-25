package com.example.codecamp.DTO;

/**
 * Dados necessários para solicitar uma matrícula pela API.
 * Este DTO mantém entidades JPA fora do contrato HTTP e segue a convenção existente do projeto
 * de receber os identificadores em snake_case.
 */
public class MatriculaRequest {

    public MatriculaRequest() {
        // Construtor vazio exigido pelo Jackson para converter o JSON recebido em objeto Java.
    }

    // ID do instrutor declarado pelo cliente; o serviço compara esse ID com o usuário do JWT.
    private Long usuario_id;
    // ID do estudante que será associado ao curso.
    private Long estudante_id;
    // ID do curso de destino; o serviço confirma que pertence ao instrutor autenticado.
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
