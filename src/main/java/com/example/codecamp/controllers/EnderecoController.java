package com.example.codecamp.controllers;

import com.example.codecamp.DTO.EnderecoResponse;
import com.example.codecamp.utils.ViaCEPUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller que recebe a requisição de consulta de endereço.
 * A busca externa é feita pela classe ViaCEPUtils, chamada diretamente abaixo.
 */
@Controller
@RequestMapping("/api/enderecos")
@Tag(name = "Endereços", description = "Consulta de endereços pelo CEP")
@SecurityRequirement(name = "bearerAuth")
public class EnderecoController {

    /**
     * Atende uma chamada GET para /api/enderecos/{cep}.
     *
     * @param cep CEP informado na URL
     * @return endereço encontrado, convertido para JSON pelo Spring
     */
    @GetMapping("/{cep}")
    @Operation(summary = "Consultar endereço por CEP", description = "Consulta o ViaCEP e retorna os dados de endereço associados ao CEP informado.")
    @ResponseBody // Indica que o retorno é o corpo da resposta, e não o nome de uma página.
    public EnderecoResponse buscarEndereco(@PathVariable String cep) {
        // Chama diretamente o método estático da classe utilitária, sem injetar um Service.
        return ViaCEPUtils.consultar(cep);
    }
}
