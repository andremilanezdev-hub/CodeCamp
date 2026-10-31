package com.example.codecamp.controllers;

import com.example.codecamp.DTO.AtualizaStatusModuloRequest;
import com.example.codecamp.DTO.ModuloConsultaResponse;
import com.example.codecamp.DTO.ModuloRequest;
import com.example.codecamp.DTO.ModuloResponse;
import com.example.codecamp.entities.Modulo;
import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Usuario;
import com.example.codecamp.repository.CursoRepository;
import com.example.codecamp.repository.ModuloRepository;
import com.example.codecamp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/modulo")
@Tag(name = "Módulos", description = "Operações de consulta e gerenciamento de módulos de cursos")
@SecurityRequirement(name = "bearerAuth")
public class ModuloController {

    @Autowired
    private ModuloRepository moduloRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    //Lista todos os cursos
    @GetMapping
    @Operation(summary = "Listar módulos", description = "Retorna os módulos cadastrados com seus dados de consulta.")
    public List<ModuloConsultaResponse> ConsultaModulo() {
        return moduloRepository.findAll().stream()
                .map(ModuloConsultaResponse::new)
                .toList();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar módulo por ID", description = "Retorna os dados do módulo informado.")
    public ResponseEntity<ModuloConsultaResponse> ConsultaMOduloPorID(@PathVariable Long id){
        var modulo = moduloRepository.findById(id);
        if (modulo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ModuloConsultaResponse(modulo.get()));
    }

    /** Rota alternativa dentro do recurso módulo para consultar um curso específico. */
    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Listar módulos por curso", description = "Retorna os módulos vinculados ao curso informado.")
    public List<ModuloConsultaResponse> listarPorCurso(@PathVariable Long cursoId) {
        validarId(cursoId, "curso_id");
        if (!cursoRepository.existsById(cursoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado");
        }
        return moduloRepository.findAllByCurso_Id(cursoId).stream()
                .map(ModuloConsultaResponse::new)
                .toList();
    }
    //Cadastro curso
    @PostMapping
    @Operation(summary = "Cadastrar módulo", description = "Cadastra um módulo em um curso pertencente ao instrutor autenticado.")
    public ResponseEntity<ModuloResponse>CadastrarModulo(@RequestBody ModuloRequest moduloRequest,
                                                           @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado){
        Modulo modulo = cadastrarModulo(moduloRequest.getCurso_id(), moduloRequest, cpfAutenticado);
        return ResponseEntity.ok(new ModuloResponse(modulo.getId(),
                "Modulo Cadastrado com sucesso!"));
    }

    /** Executa o cadastro do módulo sem camada de serviço nem transação explícita. */
    private Modulo cadastrarModulo(Long cursoId, ModuloRequest request, String cpfAutenticado) {
        validarId(cursoId, "curso_id");
        if (request == null || request.getUsuario_id() == null || request.getUsuario_id() <= 0
                || request.getNome() == null || request.getNome().isBlank()
                || request.getCargahoraria() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe instrutor, nome e carga horária válida para o módulo");
        }
        if (request.getCurso_id() != null && !request.getCurso_id().equals(cursoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "curso_id do corpo deve corresponder ao curso da rota");
        }

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
        validarPropriedadeDoCurso(curso, request.getUsuario_id(), cpfAutenticado);

        Modulo modulo = new Modulo();
        modulo.setNome(request.getNome().trim());
        modulo.setCargahoraria((long) request.getCargahoraria());
        curso.adicionarModulo(modulo);
        modulo.setUsuarioCadastro(buscarInstrutor(request.getUsuario_id(), cpfAutenticado));
        modulo.setDataCadastro(LocalDateTime.now());
        modulo.setStatus("A");
        return moduloRepository.save(modulo);
    }

    private void validarId(Long id, String campo) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe um " + campo + " válido");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar módulo", description = "Atualiza o nome e a carga horária do módulo, após validar sua propriedade.")
    public ResponseEntity<ModuloResponse> AtualizaModulo(@PathVariable Long id,
                                                          @RequestBody ModuloRequest moduloRequest,
                                                          @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado) {
        //Consuta no banco
        Modulo modulo = moduloRepository.findById(id).orElse(null);

        if (modulo !=null){
            validarPropriedadeDoModulo(
                    modulo, moduloRequest.getUsuario_id(), cpfAutenticado);
            modulo.setNome(moduloRequest.getNome());
            modulo.setCargahoraria((long) moduloRequest.getCargahoraria());
            modulo.setDataAtualizacao(LocalDateTime.now());
            moduloRepository.save(modulo);
            return ResponseEntity.ok(new ModuloResponse(modulo.getId(),
                    "Modulo Atualizado com sucesso!"));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do módulo", description = "Altera o status do módulo após validar sua propriedade.")
    public ResponseEntity<ModuloResponse> AtualizarStatusModulo(@PathVariable Long id,
                                                                 @RequestBody AtualizaStatusModuloRequest moduloRequest,
                                                                 @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado) {

        //Consuta no banco
        Modulo modulo = moduloRepository.findById(id).orElse(null);

        if (modulo !=null){
            validarPropriedadeDoModulo(
                    modulo, moduloRequest.getUsuario_id(), cpfAutenticado);
            modulo.setStatus(moduloRequest.getStatus());
            moduloRepository.save(modulo);
            return ResponseEntity.ok(new ModuloResponse(modulo.getId(),
                    "Status Modulo Atualizado com sucesso!"));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar módulo", description = "Desativa logicamente o módulo após validar sua propriedade.")
    public ResponseEntity<ModuloResponse> AtualizarStatus(@PathVariable Long id,
                                                           @RequestParam Long usuario_id,
                                                           @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado){
        //Consuta no banco
        Modulo modulo = moduloRepository.findById(id).orElse(null);

        if (modulo !=null){
            validarPropriedadeDoModulo(modulo, usuario_id, cpfAutenticado);
            modulo.setStatus("D");
            //salva no banco
            moduloRepository.save(modulo);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Usuario buscarInstrutor(Long usuarioId, String cpfAutenticado) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o usuario_id do instrutor");
        }
        if (cpfAutenticado == null || cpfAutenticado.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sem usuário associado");
        }

        Usuario instrutor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrutor não encontrado"));
        Usuario usuarioDoToken = usuarioRepository.getUsuarioByCpf(cpfAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuário do token não encontrado"));
        if (!Objects.equals(instrutor.getId(), usuarioDoToken.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso negado: usuario_id não corresponde ao usuário autenticado");
        }
        return instrutor;
    }

    private void validarPropriedadeDoCurso(Curso curso, Long usuarioId, String cpfAutenticado) {
        Usuario instrutor = buscarInstrutor(usuarioId, cpfAutenticado);
        if (curso.getUsuarioCadastro() == null
                || !Objects.equals(curso.getUsuarioCadastro().getId(), instrutor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso negado: o curso pertence a outro instrutor");
        }
    }

    private void validarPropriedadeDoModulo(Modulo modulo, Long usuarioId, String cpfAutenticado) {
        Usuario instrutor = buscarInstrutor(usuarioId, cpfAutenticado);
        boolean moduloDoInstrutor = modulo.getUsuarioCadastro() != null
                && Objects.equals(modulo.getUsuarioCadastro().getId(), instrutor.getId());
        boolean cursoDoInstrutor = modulo.getCurso() != null
                && modulo.getCurso().getUsuarioCadastro() != null
                && Objects.equals(modulo.getCurso().getUsuarioCadastro().getId(), instrutor.getId());
        if (!moduloDoInstrutor || !cursoDoInstrutor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso negado: o módulo ou seu curso pertence a outro instrutor");
        }
    }

}
