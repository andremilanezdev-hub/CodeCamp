package com.example.codecamp.controllers;

import com.example.codecamp.DTO.AtualizaStatusModuloRequest;
import com.example.codecamp.DTO.ModuloConsultaResponse;
import com.example.codecamp.DTO.ModuloRequest;
import com.example.codecamp.DTO.ModuloResponse;
import com.example.codecamp.entities.Modulo;
import com.example.codecamp.service.InstrutorPermissaoService;
import com.example.codecamp.repository.ModuloRepository;
import com.example.codecamp.service.ModuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/modulo")
@SecurityRequirement(name = "bearerAuth")
public class ModuloController {

    @Autowired
    private ModuloRepository moduloRepository;
    @Autowired
    private ModuloService moduloService;
    @Autowired
    private InstrutorPermissaoService instrutorPermissaoService;

    //Lista todos os cursos
    @GetMapping
    public List<ModuloConsultaResponse> ConsultaModulo() {
        return moduloService.listarTodos();
    }


    @GetMapping("/{id}")
    public ResponseEntity<ModuloConsultaResponse> ConsultaMOduloPorID(@PathVariable Long id){
        var modulo = moduloService.buscarPorId(id);
        if (modulo.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modulo.get());
    }

    /** Rota alternativa dentro do recurso módulo para consultar um curso específico. */
    @GetMapping("/curso/{cursoId}")
    public List<ModuloConsultaResponse> listarPorCurso(@PathVariable Long cursoId) {
        return moduloService.listarPorCurso(cursoId);
    }
    //Cadastro curso
    @PostMapping
    public ResponseEntity<ModuloResponse>CadastrarModulo(@RequestBody ModuloRequest moduloRequest,
                                                           @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado){
        // Mantém o endpoint legado /modulo; o curso informado no DTO passa pelo mesmo serviço
        // e pelas mesmas validações usadas pela rota aninhada em CursoController.
        Modulo modulo = moduloService.cadastrar(
                moduloRequest.getCurso_id(), moduloRequest, cpfAutenticado);
        return ResponseEntity.ok(new ModuloResponse(modulo.getId(),
                "Modulo Cadastrado com sucesso!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuloResponse> AtualizaModulo(@PathVariable Long id,
                                                          @RequestBody ModuloRequest moduloRequest,
                                                          @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado) {
        //Consuta no banco
        Modulo modulo = moduloRepository.findById(id).orElse(null);

        if (modulo !=null){
            instrutorPermissaoService.validarPropriedadeDoModulo(
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
    public ResponseEntity<ModuloResponse> AtualizarStatusModulo(@PathVariable Long id,
                                                                 @RequestBody AtualizaStatusModuloRequest moduloRequest,
                                                                 @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado) {

        //Consuta no banco
        Modulo modulo = moduloRepository.findById(id).orElse(null);

        if (modulo !=null){
            instrutorPermissaoService.validarPropriedadeDoModulo(
                    modulo, moduloRequest.getUsuario_id(), cpfAutenticado);
            modulo.setStatus(moduloRequest.getStatus());
            moduloRepository.save(modulo);
            return ResponseEntity.ok(new ModuloResponse(modulo.getId(),
                    "Status Modulo Atualizado com sucesso!"));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ModuloResponse> AtualizarStatus(@PathVariable Long id,
                                                           @RequestParam Long usuario_id,
                                                           @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado){
        //Consuta no banco
        Modulo modulo = moduloRepository.findById(id).orElse(null);

        if (modulo !=null){
            instrutorPermissaoService.validarPropriedadeDoModulo(modulo, usuario_id, cpfAutenticado);
            modulo.setStatus("D");
            //salva no banco
            moduloRepository.save(modulo);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
