package com.example.codecamp.controllers;

import com.example.codecamp.DTO.AtualizaStatusUsuarioRequest;
import com.example.codecamp.DTO.UsuarioConsultaResponse;
import com.example.codecamp.DTO.UsuarioRequest;
import com.example.codecamp.DTO.UsuarioResponse;
import com.example.codecamp.entities.Usuario;
import com.example.codecamp.repository.EmpresaRepository;
import com.example.codecamp.repository.UsuarioRepository;
import com.example.codecamp.utils.CpfUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Operações de consulta e gerenciamento de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {
    //Palavra magica para limpar meemória e fechar
    @Autowired
    //Toda Injeção de dependencia é privada
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EmpresaRepository empresaRepository;

    @GetMapping
    @Operation(
            summary = "Listar usuários",
            description = "Retorna a lista de usuários cadastrados com os dados de consulta."
    )
    public List<UsuarioConsultaResponse> ConsultaUsuario() {
        return usuarioRepository.findAll().stream().map(UsuarioConsultaResponse::new).toList();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados do usuário correspondente ao identificador informado."
    )
    public ResponseEntity<Usuario> ConsultaUsuarioPorID(@PathVariable Long id){
        var usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null){
            return ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(usuario);
    }

    @GetMapping("/empresa/{empresaId}")
    @Operation(
            summary = "Consultar usuário por empresa",
            description = "Consulta usuário associado ao identificador da empresa informado."
    )
    public Usuario ConsultaUsuarioPorEmpresa(@PathVariable Long empresaId){
        Usuario usuarioContrutorCompleto =
                new Usuario("Leandro","0213154545","03/05/1982");
        return  usuarioContrutorCompleto;
    }

    /**
     * Cadastra um instrutor associado à empresa informada na requisição.
     * A validação do CPF ocorre no controller, junto ao fluxo de entrada já existente,
     * para rejeitar dados inválidos antes de consultar ou persistir entidades.
     *
     * @param usuarioRequest dados enviados pelo cliente para o novo usuário
     * @return identificador do usuário criado e mensagem de sucesso
     * @throws ResponseStatusException com HTTP 400 quando o CPF for inválido
     */
    @PostMapping
    @Operation(
            summary = "Cadastrar usuário",
            description = "Valida o CPF e cadastra um usuário vinculado a uma empresa existente."
    )
    public ResponseEntity<UsuarioResponse>CadastrarUsuario(@RequestBody UsuarioRequest usuarioRequest){

        // Usa a regra de validação compartilhada já existente; CPF pode chegar formatado ou somente com dígitos.
        validarCpf(usuarioRequest.getCpf());

        // Busca e valida a empresa
        var empresaBanco = empresaRepository.findById(usuarioRequest.getEmpresa_id()).orElse(null);

        if (empresaBanco == null){
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioBanco = new Usuario();
        usuarioBanco.setNome(usuarioRequest.getNome());
        usuarioBanco.setCpf(usuarioRequest.getCpf());
        usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
        usuarioBanco.setSenha(usuarioRequest.getSenha());
        usuarioBanco.setEmpresa(empresaBanco); //vincula a empresa
        usuarioBanco.setDataCadastro(LocalDateTime.now());
        usuarioBanco.setStatus("A");

        //Salvando no banco
        usuarioRepository.save(usuarioBanco);

        return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(),
                "Cadastro com sucesso!"));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza os dados do usuário informado. Uma senha vazia mantém a senha atual."
    )
    public ResponseEntity<UsuarioResponse> AtualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest) {

        // A edição também pode substituir o CPF, portanto aplica a mesma validação antes de persistir.
        validarCpf(usuarioRequest.getCpf());

        //Consuta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);

        if (usuarioBanco !=null){
            var empresaBanco = empresaRepository.findById(usuarioRequest.getEmpresa_id()).orElse(null);

            if (empresaBanco == null){
                return ResponseEntity.notFound().build();
            }

            usuarioBanco.setNome(usuarioRequest.getNome());
            usuarioBanco.setCpf(usuarioRequest.getCpf());
            usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
            usuarioBanco.setEmpresa(empresaBanco);
            usuarioBanco.setDataAtualizacao(LocalDateTime.now());
            // Senha vazia em uma edição significa manter a credencial atual.
            if (usuarioRequest.getSenha() != null && !usuarioRequest.getSenha().isBlank()) {
                usuarioBanco.setSenha(usuarioRequest.getSenha());
            }
            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(),
                    "Usuario Atualizado com sucesso!"));
        }

        return ResponseEntity.notFound().build();

    }

    /**
     * Confere o CPF informado utilizando exclusivamente o utilitário existente no projeto.
     * Lança uma exceção HTTP 400 para que o cliente receba uma resposta de requisição inválida,
     * em vez de permitir que um documento incorreto seja gravado.
     *
     * @param cpf CPF recebido no corpo da requisição, formatado ou não
     * @throws ResponseStatusException quando o valor não passar na validação de CPF
     */
    private void validarCpf(String cpf) {
        if (!CpfUtils.isValidCpf(cpf)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status do usuário",
            description = "Altera o status do usuário para o valor enviado na requisição."
    )
    public ResponseEntity<UsuarioResponse> AtualizarUsuario(@PathVariable Long id, @RequestBody AtualizaStatusUsuarioRequest usuarioRequest) {

        //Consuta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);

        if (usuarioBanco !=null){
            // Atualiza o status informado para ativar ou desativar a conta.
            usuarioBanco.setStatus(usuarioRequest.getStatus());
            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(),
                    "Usuario Atualizado com sucesso!"));
        }

        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Desativar usuário",
            description = "Desativa logicamente o usuário informado, preservando o registro no banco."
    )
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
