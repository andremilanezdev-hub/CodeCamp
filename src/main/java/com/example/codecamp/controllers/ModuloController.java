package com.example.codecamp.controllers;

import com.example.codecamp.DTO.AtualizaStatusModuloRequest;
import com.example.codecamp.DTO.ModuloRequest;
import com.example.codecamp.DTO.ModuloResponse;
import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Modulo;
import com.example.codecamp.service.InstrutorPermissaoService;
import com.example.codecamp.repository.CursoRepository;
import com.example.codecamp.repository.ModuloRepository;
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
    private CursoRepository cursoRepository;
    @Autowired
    private InstrutorPermissaoService instrutorPermissaoService;

    //Lista todos os cursos
    @GetMapping
    public List<Modulo> ConsultaModulo() {
        return moduloRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Modulo> ConsultaMOduloPorID(@PathVariable Long id){
        var modulo = moduloRepository.findById(id).orElse(null);
        if (modulo == null){
            return ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(modulo);
    }
    //Cadastro curso
    @PostMapping
    public ResponseEntity<ModuloResponse>CadastrarModulo(@RequestBody ModuloRequest moduloRequest,
                                                           @RequestAttribute("usuarioAutenticadoCpf") String cpfAutenticado){
        if (moduloRequest.getCurso_id() == null) {
            return ResponseEntity.badRequest().build();
        }
        Curso curso = cursoRepository.findById(moduloRequest.getCurso_id()).orElse(null);
        if (curso == null) {
            return ResponseEntity.notFound().build();
        }

        instrutorPermissaoService.validarPropriedadeDoCurso(
                curso, moduloRequest.getUsuario_id(), cpfAutenticado);

        Modulo modulo = new Modulo();
        modulo.setNome(moduloRequest.getNome());
        modulo.setCargahoraria((long) moduloRequest.getCargahoraria());
        modulo.setCurso(curso);
        modulo.setUsuarioCadastro(instrutorPermissaoService.buscarInstrutor(
                moduloRequest.getUsuario_id(), cpfAutenticado));
        modulo.setDataCadastro(LocalDateTime.now());
        modulo.setStatus("A");
        //Salva no Banco
        moduloRepository.save(modulo);
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
