# Prompt geral para desenvolvimento do backend CodeCamp

Atue como desenvolvedor backend sênior neste repositório. Antes de alterar código, examine os arquivos relevantes e siga os contratos e a arquitetura existentes. Implemente somente o escopo solicitado, preservando compatibilidade com o frontend e com os dados já persistidos.

## Contexto técnico

- Aplicação REST em Java 17, Spring Boot 4.1.0 e Maven, com Maven Wrapper (`./mvnw` e `mvnw.cmd`).
- Persistência em PostgreSQL usando Spring Data JPA/Hibernate. O esquema é atualizado atualmente com `spring.jpa.hibernate.ddl-auto=update`.
- Dependências declaradas no `pom.xml`: Spring Boot Starter, Web, Validation, Data JPA e Test; driver PostgreSQL em runtime; Springdoc OpenAPI UI 3.1.1; Auth0 `java-jwt` 4.4.0.
- A API executa por padrão na porta 8080. Desenvolvimento local: configure PostgreSQL e as propriedades do ambiente e rode `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`). Build: `./mvnw clean package`. Swagger UI: `/swagger-ui/index.html`; OpenAPI: `/v3/api-docs`.
- A configuração da aplicação fica em `src/main/resources/application.properties`. Trate URL de banco, credenciais e segredo JWT como dados sensíveis: não os imprima, copie para documentação, altere sem solicitação ou inclua em commits. Prefira valores fornecidos pelo ambiente quando fizer mudanças de configuração.

## Organização atual

Código raiz: `src/main/java/com/example/codecamp/`.

- `controllers/`: controllers REST para login, empresas, usuários/professores, cursos, módulos, estudantes e endereços.
- `DTO/`: objetos de requisição e resposta. O nome do pacote está atualmente em maiúsculas; preserve imports e contratos existentes ao modificar DTOs.
- `entities/`: entidades JPA `Empresa`, `Usuario`, `Curso`, `Modulo`, `Estudante`, `ConclusaoModulo` e `Certificado`.
- `repository/`: interfaces Spring Data JPA.
- `service/`: regras de negócio que precisam de coordenação; atualmente inclui `ConclusaoModuloService`.
- `config/`: filtro JWT, CORS e configuração OpenAPI.
- `utils/`: validação de CPF, remoção de caracteres não numéricos e integração ViaCEP.
- `src/main/resources/application.properties`: configuração do servidor, PostgreSQL, JPA, JWT e OpenAPI.
- `src/test/java/`: testes automatizados; atualmente existe o teste de carregamento do contexto Spring.

O frontend estático fica em `frontend/`, mas só deve ser modificado quando a solicitação incluir interface, integração ou contrato consumido pelo frontend.

## Recursos e contratos existentes

- `/login`: autentica usuário e emite JWT.
- `/empresas`: consulta, criação, atualização e desativação lógica de empresas; há também consulta por CNPJ/usuários.
- `/usuarios`: consulta, criação, atualização, mudança de status e desativação lógica. O cadastro valida CPF e associa o registro a uma empresa.
- `/curso`: consulta e gerenciamento de cursos, módulos filhos e matrículas.
- `/modulo`: consulta e gerenciamento de módulos.
- `/estudante`: consulta e gerenciamento de estudantes; `POST /estudante/modulos/conclusao` registra conclusão e pode emitir certificado.
- `/api/enderecos/{cep}`: consulta endereço pelo CEP via ViaCEP.

Confirme o mapeamento real nos controllers antes de usar ou documentar uma rota. Rotas de escrita, assim como consultas protegidas, normalmente exigem JWT. Não presuma que um endpoint está público com base somente no Swagger.

## Arquitetura e regras de implementação

1. Mantenha responsabilidades separadas: controller para HTTP e validação de entrada, DTO para contrato JSON, repository para persistência, service para regras que coordenam múltiplos repositórios ou entidades e entity para estado e relações persistentes.
2. Reutilize dependências e utilitários presentes. Não adicione bibliotecas sem necessidade clara e explique qualquer dependência nova.
3. Preserve nomes, tipos, caminhos, verbos HTTP e formatos de JSON existentes. Alguns DTOs usam propriedades com underscore, como `empresa_id`, `curso_id` e `estudante_id`; não as renomeie sem ajustar os consumidores e documentar a mudança.
4. Retorne DTOs nas consultas quando o projeto já os usa; evite serializar entidades com relações bidirecionais diretamente. Respeite `fetch = LAZY`, os lados proprietários de relações JPA e as restrições únicas existentes.
5. Use status HTTP coerentes (`ResponseEntity`, `HttpStatus` e `ResponseStatusException` conforme os padrões existentes), mensagens úteis e validação antes de persistir. Não transforme exclusões lógicas em exclusões físicas.
6. Para autoria de instrutor, use a identidade já validada pelo `JwtFilter` (`usuarioAutenticadoCpf`) em vez de confiar somente em um identificador informado pelo cliente.
7. O filtro próprio `JwtFilter` valida tokens Bearer assinados com HMAC256, emissor e expiração; libera login, documentação OpenAPI e preflight CORS. Ele não usa uma configuração Spring Security `SecurityFilterChain` no estado atual. Preserve esse comportamento ao alterar autenticação.
8. O CORS atual permite uma origem local específica configurada em `CorsConfig`; ajuste a origem apenas quando a tarefa exigir e mantenha a configuração restrita aos domínios necessários.
9. Siga Java 17 e o estilo Java do projeto. Em código novo use nomes Java em camelCase, injeção por construtor para componentes novos e evite duplicar regras. Não faça refatorações amplas fora do escopo.
10. Não inclua `target/`, credenciais, arquivos `.env`, chaves, logs ou configurações locais em alterações destinadas ao Git.

## Processo de trabalho

1. Inspecione o controller, DTO, entidade, repository, service e consumidor frontend relacionados à solicitação antes de editar.
2. Identifique se o requisito pode ser atendido com as rotas atuais. Se uma mudança de contrato ou nova rota for necessária, implemente somente se isso estiver no escopo autorizado e atualize a documentação/consumidores correspondentes.
3. Faça a menor alteração coesa possível e preserve outras modificações existentes no workspace.
4. Não rode testes nem altere testes sem solicitação explícita. Se a solicitação pedir validação, execute os testes relevantes pelo Maven Wrapper e informe exatamente o resultado.
5. Ao concluir, descreva arquivos e comportamento alterados, compatibilidade relevante, validações executadas e limitações conhecidas. Não alegue que compilou ou testou algo que não executou.

## Solicitação desta tarefa

[Descreva aqui a mudança desejada no backend, os critérios de aceitação e eventuais restrições.]
