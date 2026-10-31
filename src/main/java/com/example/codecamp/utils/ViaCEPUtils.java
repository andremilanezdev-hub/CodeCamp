package com.example.codecamp.utils;

import com.example.codecamp.DTO.EnderecoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * Classe utilitária que concentra a consulta ao ViaCEP.
 *
 * O método é estático para que o controller possa chamá-lo diretamente, sem
 * criar um objeto, usar injeção de dependência ou adicionar uma camada Service.
 */
public class ViaCEPUtils {

    /**
     * Busca no ViaCEP o endereço relacionado ao CEP informado.
     *
     * @param cep CEP recebido com ou sem hífen
     * @return dados do endereço devolvidos pela API
     */
    public static EnderecoResponse consultar(String cep) {
        // Remove hífen, espaços e outros caracteres, deixando somente os dígitos.
        String cepSomenteNumeros = cep == null ? "" : cep.replaceAll("\\D", "");

        // Um CEP brasileiro precisa ter oito dígitos; caso contrário, a entrada é inválida.
        if (cepSomenteNumeros.length() != 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP deve conter 8 dígitos");
        }

        // Monta a URL esperada pelo serviço externo usando o CEP normalizado.
        String url = "https://viacep.com.br/ws/" + cepSomenteNumeros + "/json/";

        // RestTemplate é o cliente HTTP do Spring usado para enviar uma requisição GET.
        RestTemplate clienteHttp = new RestTemplate();

        try {
            // Faz a chamada e converte automaticamente o JSON recebido para EnderecoResponse.
            EnderecoResponse endereco = clienteHttp.getForObject(url, EnderecoResponse.class);

            // CEP inexistente retorna um JSON com "erro" e sem a propriedade "cep".
            if (endereco == null || endereco.cep() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado");
            }

            // Retorna o endereço para o controller, que o enviará ao cliente da API.
            return endereco;
        } catch (RestClientException erro) {
            // Informa que a consulta externa falhou sem expor detalhes técnicos ao cliente.
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Não foi possível consultar o ViaCEP neste momento"
            );
        }
    }
}
