package com.example.codecamp.controllers;

import com.example.codecamp.DTO.*;
import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Estudante;
import com.example.codecamp.repository.CursoRepository;
import com.example.codecamp.repository.EstudanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/estudante")
public class EstudanteController {

    @Autowired

    private EstudanteRepository estudanteRepository;

    //Lista todos os cursos
    @GetMapping
    public List<Estudante> ConsultaEstudante() {
        return estudanteRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Estudante> ConsultaEstudantePorID(@PathVariable Long id){
        var estudante = estudanteRepository.findById(id).orElse(null);
        if (estudante == null){
            return ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(estudante);
    }
    //Cadastro curso
    @PostMapping
    public ResponseEntity<EstudanteResponse>CadastrarEstudante(@RequestBody Estudante estudanteRequest){
        Estudante estudante = new Estudante();
        estudante.setNome(estudanteRequest.getNome());
        estudante.setCpf(estudante.getCpf());
        estudante.setDataCadastro(LocalDateTime.now());
        estudante.setEmail(estudanteRequest.getEmail());
        estudante.setStatus("A");
        //Salvando no banco
        estudanteRepository.save(estudante);
        return ResponseEntity.ok(new EstudanteResponse(estudante.getId(),
                "Estudante Cadastrado com sucesso!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudanteResponse> AtualizarEstudante(@PathVariable Long id, @RequestBody EstudanteRequest estudanteRequest) {
        //Consuta no banco
        Estudante estudante = estudanteRepository.findById(id).orElse(null);

        if (estudante !=null){
            estudante.setNome(estudante.getNome());
            estudante.setCpf(estudanteRequest.getCpf());
            estudante.setEmail(estudanteRequest.getEmail());
            estudante.setDataAtualizacao(LocalDateTime.now());
            estudanteRepository.save(estudante);
            return ResponseEntity.ok(new EstudanteResponse(estudante.getId(),
                    "Estudante Atualizado com sucesso!"));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EstudanteResponse> AtualizarStatusEstudante(@PathVariable Long id, @RequestBody AtualizaStatusEstudanteRequest estudanteRequest) {

        //Consuta no banco
        Estudante estudante = estudanteRepository.findById(id).orElse(null);

        if (estudante !=null){
            estudante.setStatus(estudante.getStatus());
            estudanteRepository.save(estudante);
            return ResponseEntity.ok(new EstudanteResponse(estudante.getId(),
                    "Status Estudante Atualizado com sucesso!"));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EstudanteResponse> AtualizarStatus(@PathVariable Long id){
        //Consuta no banco
        Estudante estudante = estudanteRepository.findById(id).orElse(null);
        //Deletar do banco
        //usuarioRepository.delete(curso);

        if (estudante !=null){
            estudante.setStatus("D");
            //salva no banco
            estudanteRepository.save(estudante);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
