package com.example.codecamp.controllers;

import com.example.codecamp.DTO.*;
import com.example.codecamp.entities.Estudante;
import com.example.codecamp.repository.EstudanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/estudante")
@SecurityRequirement(name = "bearerAuth")
// Define os endpoints HTTP para cadastrar, consultar e atualizar estudantes.
public class EstudanteController {

    // Repositório usado para acessar os registros de estudantes.
    @Autowired

    private EstudanteRepository estudanteRepository;

    // GET /estudante: lista estudantes e converte as entidades para o DTO de consulta.
    @GetMapping
    public List<EstudanteConsultaResponse> ConsultaEstudante() {
        return estudanteRepository.findAll().stream()
                .map(EstudanteConsultaResponse::new)
                .toList();
    }


    // GET /estudante/{id}: consulta um estudante pelo ID; responde 404 se não existir.
    @GetMapping("/{id}")
    public ResponseEntity<EstudanteConsultaResponse> ConsultaEstudantePorID(@PathVariable Long id){
        var estudante = estudanteRepository.findById(id).orElse(null);
        if (estudante == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new EstudanteConsultaResponse(estudante));
    }
    // POST /estudante: cadastra um estudante usando os dados recebidos no corpo da requisição.
    @PostMapping
    public ResponseEntity<EstudanteResponse>CadastrarEstudante(@RequestBody EstudanteRequest estudanteRequest){
        Estudante estudante = new Estudante();
        estudante.setNome(estudanteRequest.getNome());
        estudante.setCpf(estudanteRequest.getCpf());
        estudante.setCep(estudanteRequest.getCep());
        estudante.setLogradouro(estudanteRequest.getLogradouro());
        estudante.setComplemento(estudanteRequest.getComplemento());
        estudante.setBairro(estudanteRequest.getBairro());
        estudante.setLocalidade(estudanteRequest.getLocalidade());
        estudante.setUf(estudanteRequest.getUf());
        estudante.setDdd(estudanteRequest.getDdd());
        estudante.setDataCadastro(LocalDateTime.now());
        estudante.setEmail(estudanteRequest.getEmail());
        estudante.setStatus("A");
        // Persiste o estudante; o banco gera seu identificador.
        estudanteRepository.save(estudante);
        return ResponseEntity.ok(new EstudanteResponse(estudante.getId(),
                "Estudante Cadastrado com sucesso!"));
    }

    // PUT /estudante/{id}: atualiza os dados editáveis de um estudante existente.
    @PutMapping("/{id}")
    public ResponseEntity<EstudanteResponse> AtualizarEstudante(@PathVariable Long id, @RequestBody EstudanteRequest estudanteRequest) {
        // Procura o estudante que será atualizado.
        Estudante estudante = estudanteRepository.findById(id).orElse(null);

        if (estudante !=null){
            estudante.setNome(estudanteRequest.getNome());
            estudante.setCpf(estudanteRequest.getCpf());
            estudante.setCep(estudanteRequest.getCep());
            estudante.setLogradouro(estudanteRequest.getLogradouro());
            estudante.setComplemento(estudanteRequest.getComplemento());
            estudante.setBairro(estudanteRequest.getBairro());
            estudante.setLocalidade(estudanteRequest.getLocalidade());
            estudante.setUf(estudanteRequest.getUf());
            estudante.setDdd(estudanteRequest.getDdd());
            estudante.setEmail(estudanteRequest.getEmail());
            estudante.setDataAtualizacao(LocalDateTime.now());
            estudanteRepository.save(estudante);
            return ResponseEntity.ok(new EstudanteResponse(estudante.getId(),
                    "Estudante Atualizado com sucesso!"));
        }
        return ResponseEntity.notFound().build();
    }

    // PATCH /estudante/{id}/status: altera somente a situação do estudante.
    @PatchMapping("/{id}/status")
    public ResponseEntity<EstudanteResponse> AtualizarStatusEstudante(@PathVariable Long id, @RequestBody AtualizaStatusEstudanteRequest estudanteRequest) {

        // Procura o estudante cuja situação será alterada.
        Estudante estudante = estudanteRepository.findById(id).orElse(null);

        if (estudante !=null){
            estudante.setStatus(estudanteRequest.getStatus());
            estudanteRepository.save(estudante);
            return ResponseEntity.ok(new EstudanteResponse(estudante.getId(),
                    "Status Estudante Atualizado com sucesso!"));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /estudante/{id}: marca o cadastro como desativado, sem apagar o registro.
    @DeleteMapping("/{id}")
    public ResponseEntity<EstudanteResponse> AtualizarStatus(@PathVariable Long id){
        // Procura o estudante que será desativado.
        Estudante estudante = estudanteRepository.findById(id).orElse(null);
        //Deletar do banco
        //usuarioRepository.delete(curso);

        if (estudante !=null){
            estudante.setStatus("D");
            // Salva a situação atualizada no banco.
            estudanteRepository.save(estudante);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
