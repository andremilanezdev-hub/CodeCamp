package com.example.codecamp.controllers;

import com.example.codecamp.DTO.*;
import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Usuario;
import com.example.codecamp.repository.CursoRepository;
import com.example.codecamp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/curso")
public class CursoController {

    @Autowired

    private CursoRepository cursoRepository;

    //Lista todos os cursos
    @GetMapping
    public List<Curso> ConsultaCuros() {
        return cursoRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Curso> ConsultaCursoPorID(@PathVariable Long id){
        var curso = cursoRepository.findById(id).orElse(null);
        if (curso == null){
            return ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(curso);
    }
    //Cadastro curso
    @PostMapping
    public ResponseEntity<CursoResponse>CadastrarCurso(@RequestBody Curso cursoRequest){
        Curso curso = new Curso();
        curso.setNome(cursoRequest.getNome());
        curso.setCargahoraria(cursoRequest.getCargahoraria());
        curso.setDataCadastro(LocalDateTime.now());
        curso.setStatus("A");
        //Salvando no banco
        cursoRepository.save(curso);
        return ResponseEntity.ok(new CursoResponse(curso.getId(),
                "Curso Cadastrado com sucesso!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponse> AtualizarCurso(@PathVariable Long id, @RequestBody CursoRequest cursoRequest) {
        //Consuta no banco
        Curso curso = cursoRepository.findById(id).orElse(null);

        if (curso !=null){
            curso.setNome(curso.getNome());
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
            curso.setStatus(curso.getStatus());
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

}
