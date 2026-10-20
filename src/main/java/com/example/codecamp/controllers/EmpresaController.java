package com.example.codecamp.controllers;


import com.example.codecamp.DTO.EmpresaConsultaResponse;
import com.example.codecamp.DTO.EmpresaRequest;
import com.example.codecamp.DTO.EmpresaResponse;
import com.example.codecamp.DTO.UsuarioConsultaResponse;
import com.example.codecamp.entities.Empresa;
import com.example.codecamp.entities.Usuario;
import com.example.codecamp.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/empresas")
// Recebe as requisições HTTP relacionadas ao cadastro e à consulta de empresas.
public class EmpresaController {

    // Repositório usado para consultar e persistir empresas no banco.
    @Autowired
    private EmpresaRepository empresaRepository;


    // GET /empresas: lista empresas e converte cada entidade para o DTO de consulta.
    @GetMapping
    public List<EmpresaConsultaResponse> listarTodos(){

        return empresaRepository.findAll().stream().map(EmpresaConsultaResponse::new).toList();
    }


    // POST /empresas/criar: recebe os dados da empresa, salva no banco e devolve ID e mensagem.
    @PostMapping("/criar")
    public ResponseEntity<EmpresaResponse> cadastrarEmpresa(@RequestBody EmpresaRequest empresaRequest){

        // Cria a entidade e transfere para ela os dados recebidos no corpo da requisição.
        Empresa empresaBanco = new Empresa();

        empresaBanco.setRazaoSocial(empresaRequest.getRazaoSocial());
        empresaBanco.setCnpj(empresaRequest.getCnpj());
        empresaBanco.setNomeFantasia(empresaRequest.getNomeFantasia());
        empresaBanco.setInscricaoEstaual(empresaRequest.getInscricaoEstaual());


        // Persiste a empresa; após salvar, o ID gerado pelo banco fica disponível na entidade.
        empresaRepository.save(empresaBanco);

        // Monta a resposta da API sem expor diretamente a entidade JPA.
        EmpresaResponse empresaResponse = new EmpresaResponse();

        empresaResponse.setId(empresaBanco.getId());
        empresaResponse.setMensagem("Cadastro da empresa realizado com sucesso!");

        return ResponseEntity.ok(empresaResponse);
    }


    // GET /empresas/cnpj/{cnpj}/usuarios: busca uma empresa pelo CNPJ e lista seus usuários.
    // @PathVariable obtém o CNPJ informado na URL.
    @GetMapping("/cnpj/{cnpj}/usuarios")
    public ResponseEntity<List<UsuarioConsultaResponse>> buscarUsuariosPorCnpjEmpresa (@PathVariable String cnpj){

        // Consulta a empresa pelo CNPJ; se não existir, retorna HTTP 404.
        var empresa = empresaRepository.getEmpresaByCnpj(cnpj).orElse(null);

        if (empresa == null){
            return ResponseEntity.notFound().build();
        }

        // Converte os usuários relacionados à empresa para o DTO de consulta.
        var usuarioEmpresa = empresa.getUsuarios().stream().map(UsuarioConsultaResponse::new).toList();
        return ResponseEntity.ok(usuarioEmpresa);
    }

}
