package com.example.codecamp.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Identifica o estudante e o módulo que está sendo marcado como concluído. */
public class ConcluirModuloRequest {

    @NotNull(message = "Informe o ID do estudante")
    @Positive(message = "O ID do estudante deve ser maior que zero")
    private Long estudante_id;

    @NotNull(message = "Informe o ID do módulo")
    @Positive(message = "O ID do módulo deve ser maior que zero")
    private Long modulo_id;

    public Long getEstudante_id() {
        return estudante_id;
    }

    public void setEstudante_id(Long estudante_id) {
        this.estudante_id = estudante_id;
    }

    public Long getModulo_id() {
        return modulo_id;
    }

    public void setModulo_id(Long modulo_id) {
        this.modulo_id = modulo_id;
    }
}
