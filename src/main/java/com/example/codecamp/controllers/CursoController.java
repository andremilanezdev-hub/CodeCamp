package com.example.codecamp.controllers;

import com.example.codecamp.DTO.*;
import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Usuario;
import com.example.codecamp.repository.CursoRepository;
import com.example.codecamp.service.InstrutorPermissaoService;
import com.example.codecamp.service.MatriculaService;
import com.example.codecamp.service.ModuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.time.LocalDateTime;
import java.util.List;
// Controller mantém os endpoints HTTP de curso e encaminha regras de matrícula para a camada de serviço.
@RestController
@RequestMapping("/curso")
@SecurityRequirement(name = "bearerAuth")
public class CursoController {
    //Injeção de dependencias: Alocar outros arquivos que necessita no momento depois remove de memória
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private InstrutorPermissaoService instrutorPermissaoService;
    @Autowired
    private MatriculaService matriculaService;
    @Autowired
    private ModuloService moduloService;
    //Lista todos os cursos
    @GetMapping
    public List<CursoConsultaResponse> ConsultaCuros() {
        return cursoRepository.findAll().stream().map(CursoConsultaResponse::new).toList();
    }


    @GetMapping("/{id}")
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
     * A consulta é delegada ao serviço para validar existência e converter associações JPA em DTOs.
     */
    @GetMapping("/{cursoId}/modulos")
    public ResponseEntity<List<ModuloConsultaResponse>> listarModulosDoCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(moduloService.listarPorCurso(cursoId));
    }

    /**
     * Cria um módulo já vinculado ao curso informado na rota. O serviço aplica a regra de dono
     * do curso a partir do CPF fornecido pelo filtro JWT, sem confiar apenas no ID do corpo.
     */
    @PostMapping("/{cursoId}/modulos")
    public ResponseEntity<ModuloResponse> cadastrarModuloNoCurso(
            @PathVariable Long cursoId,
            @RequestBody ModuloRequest moduloRequest,
            @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado) {
        var modulo = moduloService.cadastrar(cursoId, moduloRequest, cpfAutenticado);
        return ResponseEntity.ok(new ModuloResponse(modulo.getId(),
                "Módulo cadastrado no curso com sucesso"));
    }


    //Cadastro curso
    @PostMapping
    public ResponseEntity<CursoResponse>CadastrarCurso(@RequestBody CursoRequest cursoRequest,
                                                        @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado){

        Curso cursoBanco = new Curso();
        cursoBanco.setNome(cursoRequest.getNome());
        cursoBanco.setCargahoraria(cursoRequest.getCargahoraria());
        cursoBanco.setDataCadastro(LocalDateTime.now());
        cursoBanco.setStatus("A");
        Usuario instrutor = instrutorPermissaoService.buscarInstrutor(
                cursoRequest.getUsuario_id(), cpfAutenticado);
        // Curso.usuarioCadastro é o lado proprietário da relação 1:N e grava a chave estrangeira;
        // a coleção inversa Usuario.cursosCadastrados é reconstruída pelo JPA nas consultas seguintes.
        cursoBanco.setUsuarioCadastro(instrutor);


        cursoRepository.save(cursoBanco);

        return ResponseEntity.ok(new CursoResponse(cursoBanco.getId(),"Cadastro com sucesso"));

    }

    @PutMapping("/{id}")
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
     * Recebe a solicitação de matrícula e delega validações e persistência ao serviço.
     * O CPF autenticado é injetado pelo JwtFilter, portanto o cliente não pode substituir
     * a identidade usada para verificar se o curso pertence ao instrutor.
     */
    @PostMapping("/matricula")
    public ResponseEntity<MatriculaResponse> matricular(
            @RequestBody MatriculaRequest matriculaRequest,
            @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado) {
        Curso cursoBanco = matriculaService.matricular(
                matriculaRequest.getEstudante_id(),
                matriculaRequest.getCurso_id(),
                matriculaRequest.getUsuario_id(),
                cpfAutenticado
        );

        // A resposta identifica ambos os registros ligados pela tabela N:M, sem criar
        // um ID de matrícula inexistente no modelo atual.
        MatriculaResponse resposta = new MatriculaResponse(
                matriculaRequest.getEstudante_id(),
                cursoBanco.getId(),
                "Matrícula realizada com sucesso"
        );
        return ResponseEntity.ok(resposta);
    }
}
