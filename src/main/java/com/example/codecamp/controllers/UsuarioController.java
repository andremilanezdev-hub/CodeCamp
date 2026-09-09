package com.example.codecamp.controllers;


import com.example.codecamp.DTO.AtualizaStatusUsuarioRequest;
import com.example.codecamp.DTO.UsuarioResponse;
import com.example.codecamp.entities.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @GetMapping
    public String ConsultaUsuario() {
        return "Hellor Word";
    }
    @GetMapping("/{id}")
    public Usuario ConsultaUsuarioPorID(@PathVariable Long id){
        Usuario usuario = new Usuario();
        usuario.setNome("Leandro");
        usuario.setCpf("0213154545");
        usuario.setDataNascimento("03/05/1982");

        return  usuario;
    }

    @GetMapping("/empresa/{empresaId}")
    public Usuario ConsultaUsuarioPorEmpresa(@PathVariable Long empresaId){
        Usuario usuarioContrutorCompleto =
                new Usuario("Leandro","0213154545","03/05/1982");
        return  usuarioContrutorCompleto;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse>CadastrarUsuario(@RequestBody Usuario usuarioRequest){
        Usuario usuarioBanco = new Usuario();
        usuarioBanco.setNome(usuarioRequest.getNome());
        usuarioBanco.setCpf(usuarioRequest.getCpf());
        usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
        usuarioBanco.setDataCadastro(LocalDateTime.now());
        usuarioBanco.setStatus("A");

        return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(),
                "Usuario Atualizado com sucesso!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> AtualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioRequest) {

        //Consuta no banco
        Usuario usuarioBanco = new Usuario();

        usuarioBanco = usuarioRequest;

        if (usuarioBanco !=null){
            usuarioBanco.setNome(usuarioRequest.getNome());
            usuarioBanco.setCpf(usuarioRequest.getCpf());
            usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
            usuarioBanco.setDataAtualizacao(LocalDateTime.now());

            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(),
                    "Usuario Atualizado com sucesso!"));
        }

        return ResponseEntity.notFound().build();

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioResponse> AtualizarUsuario(@PathVariable Long id, @RequestBody AtualizaStatusUsuarioRequest usuarioRequest) {

        //Consuta no banco
        Usuario usuarioBanco = new Usuario();

        if (usuarioBanco !=null){
            usuarioBanco.setStatus(usuarioBanco.getStatus());

            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(),
                    "Usuario Atualizado com sucesso!"));
        }

        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse> AtualizarStatus(@PathVariable Long id){
        //Consuta no banco
        Usuario usuarioBanco = new Usuario();

        if (usuarioBanco !=null){
            usuarioBanco.setStatus("D");

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
