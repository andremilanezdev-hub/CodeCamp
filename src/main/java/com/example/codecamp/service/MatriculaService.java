package com.example.codecamp.service;

import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Estudante;
import com.example.codecamp.repository.CursoRepository;
import com.example.codecamp.repository.EstudanteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementa as regras de negócio da matrícula entre Estudante e Curso.
 * A camada de serviço concentra autorização e validações para que a regra de propriedade do curso
 * continue aplicada mesmo que a operação seja chamada por outro controller no futuro.
 */
@Service
public class MatriculaService {

    private final CursoRepository cursoRepository;
    private final EstudanteRepository estudanteRepository;
    private final InstrutorPermissaoService instrutorPermissaoService;

    /**
     * Recebe as dependências pelo construtor e as mantém finais.
     * A injeção por construtor explicita o que o serviço precisa e impede a criação de uma instância
     * parcialmente configurada pelo Spring.
     */
    public MatriculaService(CursoRepository cursoRepository,
                            EstudanteRepository estudanteRepository,
                            InstrutorPermissaoService instrutorPermissaoService) {
        this.cursoRepository = cursoRepository;
        this.estudanteRepository = estudanteRepository;
        this.instrutorPermissaoService = instrutorPermissaoService;
    }

    /**
     * Associa um estudante a um curso se o instrutor autenticado for o responsável pelo curso.
     * A transação mantém a validação de duplicidade e a gravação da tabela de junção na mesma unidade:
     * qualquer exceção antes do commit desfaz a alteração em vez de deixar uma matrícula parcial.
     *
     * @param estudanteId estudante que será matriculado
     * @param cursoId curso de destino
     * @param usuarioId ID de instrutor recebido no DTO, conferido contra a identidade do token
     * @param cpfAutenticado CPF extraído do JWT validado pelo JwtFilter
     * @return curso ao qual o estudante foi associado, usado pelo controller para montar a resposta
     * @throws ResponseStatusException 400 para IDs inválidos, 404 para registros ausentes,
     *                                 403 para curso de outro instrutor e 409 para matrícula duplicada
     */
    @Transactional
    public Curso matricular(Long estudanteId, Long cursoId, Long usuarioId, String cpfAutenticado) {
        validarIdentificadores(estudanteId, cursoId, usuarioId);

        // JpaRepository.findById é usado para obter entidades gerenciadas pela transação atual.
        // Isso permite alterar a associação JPA sem criar consultas manuais ou outra camada de persistência.
        Estudante estudante = estudanteRepository.findById(estudanteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudante não encontrado"));
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso não encontrado"));

        // Confere o ID informado no corpo contra o CPF confiável extraído do JWT e depois verifica
        // que o instrutor é o dono persistido do curso. Não confiamos apenas em usuario_id enviado pelo cliente.
        instrutorPermissaoService.validarPropriedadeDoCurso(curso, usuarioId, cpfAutenticado);

        // A consulta derivada lê a tabela de junção pelo repositório da entidade proprietária.
        // Isso detecta a associação já persistida e permite responder 409 sem depender do conteúdo
        // enviado pelo cliente nem tratar uma solicitação repetida como sucesso silencioso.
        if (estudanteRepository.existsByIdAndCursos_Id(estudanteId, cursoId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Estudante já matriculado no curso");
        }

        // Estudante é o lado proprietário da relação N:M e atualiza a tabela Estudante_cursos.
        // O método também sincroniza Curso.estudantes para manter os dois lados coerentes em memória.
        estudante.matricularEm(curso);
        estudanteRepository.save(estudante);

        return curso;
    }

    /**
     * Rejeita requisições incompletas antes de consultar o banco.
     * IDs nulos ou não positivos representam erro de entrada do cliente e, por isso, retornam HTTP 400.
     */
    private void validarIdentificadores(Long estudanteId, Long cursoId, Long usuarioId) {
        if (estudanteId == null || estudanteId <= 0
                || cursoId == null || cursoId <= 0
                || usuarioId == null || usuarioId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe estudante_id, curso_id e usuario_id com valores válidos");
        }
    }
}
