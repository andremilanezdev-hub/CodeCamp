package com.example.codecamp.controllers;

import com.example.codecamp.DTO.*;
import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Modulo;
import com.example.codecamp.entities.Estudante;
import com.example.codecamp.entities.Usuario;
import com.example.codecamp.repository.CursoRepository;
import com.example.codecamp.repository.EstudanteRepository;
import com.example.codecamp.repository.ModuloRepository;
import com.example.codecamp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
// Controller reúne os endpoints HTTP de curso e as regras associadas a esses fluxos.
@RestController
@RequestMapping("/curso")
@Tag(name = "Cursos", description = "Operações de consulta e gerenciamento de cursos, módulos e matrículas")
@SecurityRequirement(name = "bearerAuth")
public class CursoController {
    //Injeção de dependencias: Alocar outros arquivos que necessita no momento depois remove de memória
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EstudanteRepository estudanteRepository;
    @Autowired
    private ModuloRepository moduloRepository;
    //Lista todos os cursos
    @GetMapping
    @Operation(summary = "Listar cursos", description = "Retorna os cursos cadastrados com seus dados de consulta.")
    public List<CursoConsultaResponse> ConsultaCuros() {
        return cursoRepository.findAll().stream().map(CursoConsultaResponse::new).toList();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar curso por ID", description = "Retorna os dados do curso e suas associações pelo identificador informado.")
    public ResponseEntity<CursoConsultaResponse> ConsultaCursoPorID(@PathVariable Long id){
        var curso = cursoRepository.findById(id).orElse(null);
        if (curso == null){
            return ResponseEntity.notFound().build();
        }
        // Retorna o mesmo DTO da listagem, incluindo os estudantes matriculados sem serializar
        // diretamente as relações bidirecionais da entidade JPA.
        return ResponseEntity.ok(new CursoConsultaResponse(curso));
    }

    /**
     * Lista os módulos filhos de um curso pela rota hierárquica /curso/{cursoId}/modulos.
     * A consulta valida a existência do curso e converte as associações JPA em DTOs.
     */
    @GetMapping("/{cursoId}/modulos")
    @Operation(summary = "Listar módulos do curso", description = "Retorna os módulos vinculados ao curso informado.")
    public ResponseEntity<List<ModuloConsultaResponse>> listarModulosDoCurso(@PathVariable Long cursoId) {
        validarId(cursoId, "curso_id");
        if (!cursoRepository.existsById(cursoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado");
        }
        List<ModuloConsultaResponse> modulos = moduloRepository.findAllByCurso_Id(cursoId).stream()
                .map(ModuloConsultaResponse::new)
                .toList();
        return ResponseEntity.ok(modulos);
    }

    /**
     * Cria um módulo já vinculado ao curso informado na rota e confere o dono pelo CPF do JWT.
     */
    @PostMapping("/{cursoId}/modulos")
    @Operation(summary = "Cadastrar módulo no curso", description = "Cria um módulo vinculado ao curso, após validar a propriedade do instrutor autenticado.")
    public ResponseEntity<ModuloResponse> cadastrarModuloNoCurso(
            @PathVariable Long cursoId,
            @RequestBody ModuloRequest moduloRequest,
            @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado) {
        Modulo modulo = cadastrarModulo(cursoId, moduloRequest, cpfAutenticado);
        return ResponseEntity.ok(new ModuloResponse(modulo.getId(),
                "Módulo cadastrado no curso com sucesso"));
    }

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


    //Cadastro curso
    @PostMapping
    @Operation(summary = "Cadastrar curso", description = "Cadastra um curso associado ao instrutor autenticado.")
    public ResponseEntity<CursoResponse>CadastrarCurso(@RequestBody CursoRequest cursoRequest,
                                                        @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado){

        Curso cursoBanco = new Curso();
        cursoBanco.setNome(cursoRequest.getNome());
        cursoBanco.setCargahoraria(cursoRequest.getCargahoraria());
        cursoBanco.setDataCadastro(LocalDateTime.now());
        cursoBanco.setStatus("A");
        Usuario instrutor = buscarInstrutor(
                cursoRequest.getUsuario_id(), cpfAutenticado);
        // Curso.usuarioCadastro é o lado proprietário da relação 1:N e grava a chave estrangeira;
        // a coleção inversa Usuario.cursosCadastrados é reconstruída pelo JPA nas consultas seguintes.
        cursoBanco.setUsuarioCadastro(instrutor);


        cursoRepository.save(cursoBanco);

        return ResponseEntity.ok(new CursoResponse(cursoBanco.getId(),"Cadastro com sucesso"));

    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar curso", description = "Atualiza o nome e a carga horária do curso informado.")
    public ResponseEntity<CursoResponse> AtualizarCurso(@PathVariable Long id, @RequestBody CursoRequest cursoRequest) {
        //Consuta no banco
        Curso curso = cursoRepository.findById(id).orElse(null);

        if (curso !=null){
            // Usa o nome recebido no corpo; atribuir o valor atual impedia alterações pelo PUT.
            curso.setNome(cursoRequest.getNome());
            curso.setCargahoraria(cursoRequest.getCargahoraria());
            curso.setDataAtualizacao(LocalDateTime.now());
            cursoRepository.save(curso);
            return ResponseEntity.ok(new CursoResponse(curso.getId(),
                    "Curso Atualizado com sucesso!"));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do curso", description = "Altera o status do curso para o valor enviado na requisição.")
    public ResponseEntity<CursoResponse> AtualizarStatusCurso(@PathVariable Long id, @RequestBody AtualizaStatusCursoRequest cursoRequest) {

        //Consuta no banco
        Curso curso = cursoRepository.findById(id).orElse(null);

        if (curso !=null){
            // PATCH altera somente o status e permite reativar cursos desativados.
            curso.setStatus(cursoRequest.getStatus());
            cursoRepository.save(curso);
            return ResponseEntity.ok(new CursoResponse(curso.getId(),
                    "Status Curso Atualizado com sucesso!"));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar curso", description = "Desativa logicamente o curso, preservando o registro no banco.")
    public ResponseEntity<CursoResponse> AtualizarStatus(@PathVariable Long id){
        //Consuta no banco
        Curso curso = cursoRepository.findById(id).orElse(null);
        //Deletar do banco
        //usuarioRepository.delete(curso);

        if (curso !=null){
            curso.setStatus("D");
            //salva no banco
            cursoRepository.save(curso);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    /**
     * Valida e persiste a matrícula. O CPF autenticado vem do JwtFilter, então o cliente não
     * pode substituir a identidade usada para verificar se o curso pertence ao instrutor.
     */
    @PostMapping("/matricula")
    @Operation(summary = "Matricular estudante", description = "Matricula um estudante em um curso pertencente ao instrutor autenticado.")
    public ResponseEntity<MatriculaResponse> matricular(
            @RequestBody MatriculaRequest matriculaRequest,
            @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado) {
        Long estudanteId = matriculaRequest.getEstudante_id();
        Long cursoId = matriculaRequest.getCurso_id();
        Long usuarioId = matriculaRequest.getUsuario_id();
        validarIdentificadoresMatricula(estudanteId, cursoId, usuarioId);

        Estudante estudante = estudanteRepository.findById(estudanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado"));
        Curso cursoBanco = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
        validarPropriedadeDoCurso(cursoBanco, usuarioId, cpfAutenticado);

        if (estudanteRepository.existsByIdAndCursos_Id(estudanteId, cursoId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estudante já matriculado no curso");
        }
        estudante.matricularEm(cursoBanco);
        estudanteRepository.save(estudante);

        // A resposta identifica ambos os registros ligados pela tabela N:M, sem criar
        // um ID de matrícula inexistente no modelo atual.
        MatriculaResponse resposta = new MatriculaResponse(
                matriculaRequest.getEstudante_id(),
                cursoBanco.getId(),
                "Matrícula realizada com sucesso"
        );
        return ResponseEntity.ok(resposta);
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

    private void validarIdentificadoresMatricula(Long estudanteId, Long cursoId, Long usuarioId) {
        if (estudanteId == null || estudanteId <= 0 || cursoId == null || cursoId <= 0
                || usuarioId == null || usuarioId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe estudante_id, curso_id e usuario_id com valores válidos");
        }
    }
}
