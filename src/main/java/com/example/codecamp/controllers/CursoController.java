package com.example.codecamp.controllers;


import com.example.codecamp.entities.Curso;
import com.example.codecamp.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/curso")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    //Lista todos os cursos
    @GetMapping
    public List<Curso> ConsultaCursos(){
        return cursoRepository.findAll();
    }

    //Busca Curso por ID
    @GetMapping("/{id}")
    public ResponseEntity<Curso> ConsultaCursoPorID(@PathVariable Long id){
        var curso = cursoRepository.findById(id).orElse(null);
        if (curso == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(curso);
    }

    //Consulta por empresa
    @GetMapping("/empresa/{empresa}")
    public Curso ConsultaCursoPorEmpresa(@PathVariable Long empresaId){
        Curso cursocontrutor = new Curso()
    }

}//Fim da classe principal
