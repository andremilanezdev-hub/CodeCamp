package com.example.codecamp.DTO;

/**
 * Resposta da operação de matrícula.
 * Como a matrícula é representada por uma associação N:M sem atributos próprios, não existe
 * uma entidade Matricula nem um identificador de matrícula; a resposta identifica os dois lados
 * da associação e informa o resultado da operação.
 */
public class MatriculaResponse {

    private Long estudante_id;
    private Long curso_id;
    private String mensagem;

    public MatriculaResponse() {
        // Construtor vazio para manter compatibilidade com serializadores e ferramentas do projeto.
    }

    public MatriculaResponse(Long estudanteId, Long cursoId, String mensagem) {
        this.estudante_id = estudanteId;
        this.curso_id = cursoId;
        this.mensagem = mensagem;
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

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
