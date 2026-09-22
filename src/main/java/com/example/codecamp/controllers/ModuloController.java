package com.example.codecamp.controllers;

import com.example.codecamp.DTO.AtualizaStatusModuloRequest;
import com.example.codecamp.DTO.ModuloRequest;
import com.example.codecamp.DTO.ModuloResponse;
import com.example.codecamp.entities.Modulo;
import com.example.codecamp.repository.CursoRepository;
import com.example.codecamp.repository.ModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/modulo")
public class ModuloController {

    @Autowired
    private ModuloRepository moduloRepository;

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
    public ResponseEntity<ModuloResponse>CadastrarModulo(@RequestBody Modulo moduloRequest){
        Modulo modulo = new Modulo();
        modulo.setNome(moduloRequest.getNome());
        modulo.setCargahoraria(moduloRequest.getCargahoraria());
        modulo.setDataCadastro(LocalDateTime.now());
        modulo.setStatus("A");
        //Salva no Banco
        moduloRepository.save(modulo);
        return ResponseEntity.ok(new ModuloResponse(modulo.getId(),
                "Modulo Cadastrado com sucesso!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuloResponse> AtualizaModulo(@PathVariable Long id, @RequestBody ModuloRequest moduloRequest) {
        //Consuta no banco
        Modulo modulo = moduloRepository.findById(id).orElse(null);

        if (modulo !=null){
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
    public ResponseEntity<ModuloResponse> AtualizarStatusModulo(@PathVariable Long id, @RequestBody AtualizaStatusModuloRequest moduloRequest) {

        //Consuta no banco
        Modulo modulo = moduloRepository.findById(id).orElse(null);

        if (modulo !=null){
            modulo.setStatus(modulo.getStatus());
            moduloRepository.save(modulo);
            return ResponseEntity.ok(new ModuloResponse(modulo.getId(),
                    "Status Modulo Atualizado com sucesso!"));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ModuloResponse> AtualizarStatus(@PathVariable Long id){
        //Consuta no banco
        Modulo modulo = moduloRepository.findById(id).orElse(null);

        if (modulo !=null){
            modulo.setStatus("D");
            //salva no banco
            moduloRepository.save(modulo);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
