# CodeCamp — Plataforma de Gestão de Bootcamps

Plataforma web para organizar os principais cadastros e fluxos de um bootcamp de tecnologia, conectando instrutores e estudantes em um ambiente centralizado.

![Java 17](https://img.shields.io/badge/Java-17-orange)
![Spring Boot 4.1.0](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-banco%20de%20dados-4169E1)

## 🎯 Sobre o projeto

Administrar um bootcamp envolve manter cadastros de empresas, instrutores, cursos, módulos e estudantes, além de acompanhar quem participa de cada curso. Quando essas informações ficam dispersas, é mais difícil manter os dados consistentes e garantir que cada instrutor gerencie apenas os cursos sob sua responsabilidade.

O CodeCamp reúne esses fluxos em uma aplicação com API REST em Java e uma interface web estática. O projeto busca simplificar a administração de cursos e estudantes, organizar módulos por curso e controlar matrículas, mantendo a autenticação por token e a documentação da API acessível pelo Swagger UI.

O repositório também descreve uma visão mais ampla de gestão de bootcamps — como cronogramas, entregas de projetos, mentoria e métricas. Esses recursos são objetivos possíveis de evolução; não foram identificados como funcionalidades implementadas no código atual.

## ✨ Principais funcionalidades

Funcionalidades identificadas no código:

- Cadastro, consulta, edição e desativação de empresas e usuários.
- Cadastro e gerenciamento de cursos e módulos vinculados a um curso.
- Cadastro e gerenciamento de estudantes, incluindo endereço consultado pelo CEP.
- Matrícula de estudantes em cursos.
- Validação de negócio para permitir ao instrutor matricular estudantes apenas nos cursos que ele próprio cadastrou.
- Autenticação com JWT para proteger os endpoints da API.
- Interface web em HTML, CSS e JavaScript para os fluxos disponíveis.
- Documentação interativa dos endpoints com OpenAPI e Swagger UI.
- Desativação lógica de registros nos endpoints de status ou exclusão, conforme a implementação de cada controller.

Visão de evolução apresentada para a plataforma, mas não identificada como implementada neste repositório: cronograma de aulas, submissão e correção de desafios ou projetos, painel do mentor e métricas de engajamento e desempenho em tempo real.

## 🧰 Tecnologias utilizadas

- **Java 17** e **Spring Boot 4.1.0**.
- **Spring Web MVC** para os endpoints REST.
- **Spring Data JPA / Hibernate** para persistência e mapeamento objeto-relacional.
- **PostgreSQL** como banco de dados.
- **Auth0 `java-jwt`** para geração e validação de tokens JWT.
- **Springdoc OpenAPI** para documentação e interface Swagger.
- **Maven Wrapper** para compilar e executar o backend.
- **HTML, CSS e JavaScript** sem framework de frontend identificado.
- **ViaCEP** para consulta de endereço a partir do CEP.

## ✅ Pré-requisitos

- Git.
- JDK 17.
- PostgreSQL instalado e em execução, com um banco chamado `code_camp` (ou outro nome configurado na variável de conexão).
- Python 3, somente para servir localmente os arquivos estáticos do frontend.
- Acesso à internet na primeira compilação para baixar as dependências Maven e para usar a consulta externa do ViaCEP.

Node.js e Docker não são necessários para executar a configuração atual do projeto. O repositório não contém `package.json`, `Dockerfile` ou `docker-compose.yml`.

## ⚙️ Instalação e configuração

### 1. Clone o repositório

```bash
git clone https://github.com/andremilanezdev-hub/CodeCamp.git
cd CodeCamp
```

### 2. Crie o banco de dados

No PostgreSQL, crie o banco esperado pela configuração padrão:

```sql
CREATE DATABASE code_camp;
```

O Hibernate está configurado para atualizar o esquema (`spring.jpa.hibernate.ddl-auto=update`) durante a execução. Em produção, revise a estratégia de migração e o gerenciamento do esquema antes de usar essa configuração.

### 3. Configure as variáveis de ambiente

As propriedades podem ser fornecidas pelo ambiente para substituir os valores locais de `application.properties`. Crie um `.env` na raiz do projeto — esse arquivo é ignorado pelo Git — com seus valores:

```dotenv
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/code_camp
SPRING_DATASOURCE_USERNAME=seu_usuario_postgres
SPRING_DATASOURCE_PASSWORD=sua_senha_postgres
SPRING_SECRET=troque_por_uma_chave_forte_e_longa
SPRING_EMISSOR=CodeCamp
SPRING_TEMPO=3600000
```

O Spring Boot não carrega arquivos `.env` automaticamente. No Bash, carregue as variáveis no terminal antes de iniciar a aplicação:

```bash
set -a
. ./.env
set +a
```

No PowerShell, defina as variáveis na sessão antes de executar o Maven:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/code_camp"
$env:SPRING_DATASOURCE_USERNAME = "seu_usuario_postgres"
$env:SPRING_DATASOURCE_PASSWORD = "sua_senha_postgres"
$env:SPRING_SECRET = "troque_por_uma_chave_forte_e_longa"
$env:SPRING_EMISSOR = "CodeCamp"
$env:SPRING_TEMPO = "3600000"
```

O `application.properties` também contém valores destinados ao ambiente local/de teste, incluindo uma chave JWT fixa. Não publique esses valores nem reutilize a chave fixa em produção; configure uma chave forte e exclusiva por variável de ambiente.

### 4. Baixe as dependências e compile

O Maven Wrapper baixa a versão necessária do Maven e as dependências do projeto:

```bash
./mvnw clean package
```

No Windows:

```powershell
.\mvnw.cmd clean package
```

## ▶️ Como executar

### Backend em desenvolvimento

Com PostgreSQL em execução e as variáveis configuradas:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

A API fica disponível, por padrão, em `http://localhost:8080`.

### Frontend em desenvolvimento

Em outro terminal, na raiz do repositório, sirva a pasta `frontend` em uma origem permitida pela configuração CORS atual:

```bash
python3 -m http.server 5500 --bind 127.0.0.1 --directory frontend
```

Abra `http://127.0.0.1:5500` no navegador. A configuração atual do frontend aponta para a API em `http://localhost:8080`. Se alterar a origem ou publicar a interface em outro endereço, atualize a URL base em `frontend/js/api.js` e permita a origem correspondente na configuração CORS do backend.

### Testes

Execute os testes automatizados disponíveis no projeto com:

```bash
./mvnw test
```

### Build e execução do pacote

Gere o JAR:

```bash
./mvnw clean package
```

Execute o artefato gerado:

```bash
java -jar target/CodeCamp-0.0.1-SNAPSHOT.jar
```

Para produção, configure as variáveis de ambiente com credenciais e segredo exclusivos do ambiente antes de iniciar o JAR. Sirva o frontend por um servidor web estático e ajuste a URL da API e o CORS para os domínios publicados.

## 🔌 API e exemplos

Com a aplicação ativa, a documentação interativa está disponível em:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Documento OpenAPI: `http://localhost:8080/v3/api-docs`

Principais grupos de rotas identificados:

| Recurso | Base | Operações disponíveis |
|---|---|---|
| Login | `/login` | Autenticação e obtenção de token |
| Empresas | `/empresas` | Listar, consultar, criar, atualizar, excluir/desativar e consultar usuários por CNPJ |
| Cursos | `/curso` | Listar, consultar, criar, atualizar, alterar status, excluir, gerenciar módulos e matricular estudante |
| Módulos | `/modulo` | Listar, consultar, criar, atualizar, alterar status e excluir |
| Estudantes | `/estudante` | Listar, consultar, criar, atualizar, alterar status e excluir |
| Usuários | `/usuarios` | Listar, consultar, criar, atualizar, alterar status, excluir e consultar por empresa |
| Endereços | `/api/enderecos/{cep}` | Consultar endereço pelo CEP |

Exemplo de autenticação — ajuste os dados conforme um usuário existente no banco:

```http
POST /login
Content-Type: application/json

{
  "login": "12345678901",
  "senha": "sua-senha"
}
```

Use o token retornado nas demais chamadas protegidas:

```http
Authorization: Bearer <token>
```

Exemplo do corpo para matrícula, conforme o controller de cursos:

```http
POST /curso/matricula
Content-Type: application/json
Authorization: Bearer <token>

{
  "usuario_id": 1,
  "estudante_id": 2,
  "curso_id": 3
}
```

Os endpoints de escrita/consulta protegidos exigem autenticação JWT, exceto as rotas liberadas pela configuração de segurança (incluindo login e documentação OpenAPI). As operações de exclusão ou alteração de status devem ser conferidas na documentação Swagger para cada recurso; vários fluxos usam desativação lógica em vez de apagar fisicamente o registro.

Para autenticar, é necessário que já exista um usuário cadastrado. A aplicação não identifica uma rotina de carga inicial de usuários nem um endpoint público de criação; o cadastro de usuários está protegido pela autenticação atual. Configure os dados iniciais de acordo com o fluxo administrativo disponível no seu ambiente.

## 🗂️ Arquitetura e estrutura de pastas

```text
CodeCamp/
├── frontend/
│   ├── css/                  # Estilos das páginas
│   ├── html/                 # Telas de gerenciamento
│   ├── js/                   # Integração da interface com a API
│   └── index.html            # Entrada do frontend
├── src/
│   ├── main/
│   │   ├── java/com/example/codecamp/
│   │   │   ├── config/       # Segurança, CORS e OpenAPI
│   │   │   ├── controllers/  # Endpoints REST
│   │   │   ├── DTO/          # Objetos de requisição e resposta
│   │   │   ├── entities/     # Entidades persistidas
│   │   │   ├── repository/   # Acesso a dados com Spring Data JPA
│   │   │   ├── service/      # Regras de negócio e integrações
│   │   │   └── utils/        # Utilitários compartilhados
│   │   └── resources/
│   │       └── application.properties
│   └── test/                 # Testes automatizados
├── mvnw                      # Maven Wrapper para Unix
├── mvnw.cmd                  # Maven Wrapper para Windows
└── pom.xml                   # Dependências e configuração Maven
```

A API segue uma organização em camadas: controllers recebem as requisições HTTP, DTOs definem os dados de entrada e saída, services concentram regras de negócio quando aplicável, repositories acessam o banco e entities representam os dados persistidos. O frontend estático consome a API por JavaScript.

## 🤝 Contribuição

Contribuições são bem-vindas. Para propor uma alteração:

1. Abra uma issue descrevendo o problema ou a melhoria.
2. Faça um fork e crie uma branch com um nome claro, por exemplo `feat/relatorio-cursos` ou `fix/validacao-login`.
3. Mantenha a arquitetura e os padrões já usados no projeto; evite adicionar dependências sem necessidade.
4. Execute `./mvnw test` e `./mvnw clean package` e descreva os resultados na pull request.
5. Não inclua arquivos `.env`, credenciais, chaves JWT ou artefatos gerados (`target/`) nos commits.

## 📄 Licença

O repositório não contém um arquivo de licença no momento. A licença ainda precisa ser definida pelos mantenedores. Até que uma licença seja adicionada, não presuma permissão para redistribuir ou reutilizar o código fora das condições aplicáveis.
