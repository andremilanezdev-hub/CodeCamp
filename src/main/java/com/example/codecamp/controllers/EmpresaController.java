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
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;


    @GetMapping
    public List<EmpresaConsultaResponse> listarTodos(){

        return empresaRepository.findAll().stream().map(EmpresaConsultaResponse::new).toList();
    }


    @PostMapping("/criar")
    public ResponseEntity<EmpresaResponse> cadastrarEmpresa(@RequestBody EmpresaRequest empresaRequest){

        Empresa empresaBanco = new Empresa();

        empresaBanco.setRazaoSocial(empresaRequest.getRazaoSocial());
        empresaBanco.setCnpj(empresaRequest.getCnpj());
        empresaBanco.setNomeFantasia(empresaRequest.getNomeFantasia());
        empresaBanco.setInscricaoEstaual(empresaRequest.getInscricaoEstaual());


        empresaRepository.save(empresaBanco);

        EmpresaResponse empresaResponse = new EmpresaResponse();

        empresaResponse.setId(empresaBanco.getId());
        empresaResponse.setMensagem("Cadastro da empresa realizado com sucesso!");

        return ResponseEntity.ok(empresaResponse);
    }


    //Se vir pela url utilizar @PathVariable se vir pelo json vem pelo @RequestBody
    @GetMapping("/cnpj/{cnpj}/usuarios")
    public ResponseEntity<List<UsuarioConsultaResponse>> buscarUsuariosPorCnpjEmpresa (@PathVariable String cnpj){

        var empresa = empresaRepository.getEmpresaByCnpj(cnpj).orElse(null);

        if (empresa == null){
            return ResponseEntity.notFound().build();
        }
        var usuarioEmpresa = empresa.getUsuarios().stream().map(UsuarioConsultaResponse::new).toList();
        return ResponseEntity.ok(usuarioEmpresa);
    }

}
