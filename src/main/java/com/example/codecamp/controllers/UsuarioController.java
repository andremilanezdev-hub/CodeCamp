package com.example.codecamp.controllers;

import com.example.codecamp.DTO.AtualizaStatusUsuarioRequest;
import com.example.codecamp.DTO.UsuarioRequest;
import com.example.codecamp.DTO.UsuarioResponse;
import com.example.codecamp.entities.Usuario;
import com.example.codecamp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    //Palavra magica para limpar meemória e fechar
    @Autowired
    //Toda Injeção de dependencia é privada
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> ConsultaUsuario() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> ConsultaUsuarioPorID(@PathVariable Long id){
        var usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null){
            return ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(usuario);
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
        usuarioBanco.setSenha(usuarioRequest.getSenha());

        //Salvando no banco
        usuarioRepository.save(usuarioBanco);

        return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(),
                "Usuario Atualizado com sucesso!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> AtualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest) {

        //Consuta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);

        if (usuarioBanco !=null){
            usuarioBanco.setNome(usuarioRequest.getNome());
            usuarioBanco.setCpf(usuarioRequest.getCpf());
            usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
            usuarioBanco.setDataAtualizacao(LocalDateTime.now());
            usuarioBanco.setSenha(usuarioRequest.getSenha());
            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(),
                    "Usuario Atualizado com sucesso!"));
        }

        return ResponseEntity.notFound().build();

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioResponse> AtualizarUsuario(@PathVariable Long id, @RequestBody AtualizaStatusUsuarioRequest usuarioRequest) {

        //Consuta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);

        if (usuarioBanco !=null){
            usuarioBanco.setStatus(usuarioBanco.getStatus());
            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(),
                    "Usuario Atualizado com sucesso!"));
        }

        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse> AtualizarStatus(@PathVariable Long id){
        //Consuta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);
        //Deletar do banco
        //usuarioRepository.delete(usuarioBanco);

        if (usuarioBanco !=null){
            usuarioBanco.setStatus("D");
            //salva no banco
            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
