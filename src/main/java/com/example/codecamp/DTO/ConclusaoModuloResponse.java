package com.example.codecamp.DTO;

/** Resultado do registro de conclusão e, quando aplicável, da emissão do certificado. */
public class ConclusaoModuloResponse {

    private Long estudante_id;
    private Long modulo_id;
    private Long curso_id;
    private boolean modulo_concluido;
    private boolean curso_concluido;
    private String codigo_validacao;
    private String mensagem;

    public ConclusaoModuloResponse(Long estudanteId, Long moduloId, Long cursoId,
                                   boolean cursoConcluido, String codigoValidacao, String mensagem) {
        this.estudante_id = estudanteId;
        this.modulo_id = moduloId;
        this.curso_id = cursoId;
        this.modulo_concluido = true;
        this.curso_concluido = cursoConcluido;
        this.codigo_validacao = codigoValidacao;
        this.mensagem = mensagem;
    }

    public Long getEstudante_id() {
        return estudante_id;
    }

    public Long getModulo_id() {
        return modulo_id;
    }

    public Long getCurso_id() {
        return curso_id;
    }

    public boolean isModulo_concluido() {
        return modulo_concluido;
    }

    public boolean isCurso_concluido() {
        return curso_concluido;
    }

    public String getCodigo_validacao() {
        return codigo_validacao;
    }

    public String getMensagem() {
        return mensagem;
    }
}
