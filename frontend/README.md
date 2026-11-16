# CodeCamp — Frontend

Interface web estática da plataforma CodeCamp. Professores cadastrados entram no sistema para gerenciar empresas, professores, cursos, módulos, estudantes e matrículas. A interface também oferece acompanhamento de conclusões de módulos, exibição de certificados emitidos pela API e consulta de endereço por CEP.

## Funcionalidades

- Login por CPF e senha.
- Dashboard com resumo de cursos cadastrados, estudantes, matrículas e cursos finalizados registrados neste navegador.
- Consulta, cadastro, edição e desativação lógica de empresas, professores, cursos, módulos e estudantes, conforme as operações existentes na API.
- Matrícula de estudantes em cursos.
- Registro de conclusão de módulos e exibição do código de certificado retornado pela API quando o curso for concluído.
- Consulta de endereço pelo CEP e preenchimento dos campos de endereço do estudante.
- Máscaras de CPF, CNPJ, CEP e data nos formulários.

O dashboard calcula cursos, estudantes e matrículas consultando a API. A contagem de cursos finalizados usa registros locais (`localStorage`) gravados quando a tela de acompanhamento recebe a confirmação de conclusão. Ela não recupera conclusões realizadas em outro navegador.

## Tecnologias e padrões

- HTML5 semântico, com `lang="pt-BR"`, codificação UTF-8 e metadados responsivos.
- CSS3 em folhas externas, com estilos compartilhados e estilos por página.
- JavaScript puro (Vanilla JS), organizado em módulos ES carregados com `type="module"`; não há framework frontend.
- `fetch` com `async/await`, centralizado em `js/api.js` por meio de `apiRequest`.
- JSON enviado com `JSON.stringify` e lido das respostas HTTP pelo módulo compartilhado.
- JWT armazenado em `localStorage` e enviado nas chamadas protegidas como `Authorization: Bearer <token>`.
- Manipulação dinâmica do DOM com APIs como `createElement`, `textContent`, `append` e `replaceChildren`. Template literals são usados em mensagens, URLs e textos dinâmicos; a renderização não depende de um sistema de templates ou framework.
- `Intl.DateTimeFormat` para exibir datas e horários de estudantes no padrão brasileiro.

O projeto não contém `package.json`, não instala dependências npm e não requer compilação do frontend. Os recursos do navegador usados são nativos.

## Estrutura de pastas

```text
frontend/
├── index.html                    # Página de login
├── dashboard.html                # Visão geral e indicadores
├── cursos.html                   # Listagem e ações de cursos
├── curso-cadastro.html           # Cadastro e edição de cursos
├── modulos.html                  # Listagem e ações de módulos
├── modulo-cadastro.html          # Cadastro e edição de módulos
├── usuarios.html                 # Listagem de professores
├── usuario-cadastro.html         # Cadastro e edição de professores
├── estudantes.html               # Listagem, cadastro, edição e detalhes de estudantes
├── estudantes/index.html         # Redirecionamento para estudantes.html
├── matricula.html                # Formulário de matrícula
├── acompanhamento.html           # Conclusões e certificados
├── empresas.html                 # Listagem e consulta de professores por empresa
├── empresa-cadastro.html         # Cadastro e edição de empresas
├── enderecos.html                # Consulta de endereço por CEP
├── Anotacoes.txt                 # Anotações auxiliares do projeto
├── prompt.txt                    # Prompt auxiliar mantido no repositório
├── css/
│   ├── global.css                # Variáveis, estilos globais e componentes compartilhados
│   ├── style.css                 # Estilos específicos da página de login
│   ├── dashboard.css             # Menu lateral e estrutura das páginas internas
│   ├── acompanhamento.css        # Acompanhamento e certificados
│   ├── curso-cadastro.css        # Formulário de curso
│   ├── cursos.css                # Listagem de cursos
│   ├── empresa-cadastro.css      # Formulário de empresa
│   ├── empresas.css              # Listagem de empresas
│   ├── enderecos.css             # Consulta de CEP
│   ├── estudantes.css            # Fluxos de estudantes
│   ├── matricula.css             # Formulário de matrícula
│   ├── modulo-cadastro.css       # Formulário de módulo
│   ├── modulos.css               # Listagem de módulos
│   ├── usuario-cadastro.css      # Formulário de professor
│   └── usuarios.css              # Listagem de professores
└── js/
    ├── api.js                    # URL base, token, chamadas HTTP e erros
    ├── login.js                  # Validação do login e redirecionamento
    ├── logout.js                 # Ação do botão de saída
    ├── mascaras.js               # Máscaras e formatação de documentos, CEP e data
    └── *.js                      # Lógica de página, formulários e renderização
```

### Responsabilidade dos módulos JavaScript

| Arquivo | Responsabilidade |
|---|---|
| `api.js` | Define `BASE_URL`, monta headers e corpo JSON, envia chamadas HTTP, interpreta respostas e encerra a sessão em erros de autenticação. |
| `login.js` | Valida CPF e senha, chama `/login`, guarda o JWT e abre o dashboard. |
| `logout.js` | Liga o botão “Sair” à limpeza do token e ao retorno para o login. |
| `mascaras.js` | Aplica e formata CPF, CNPJ, CEP, telefone e data nos campos marcados com `data-mascara`. |
| `dashboard.js` | Marca a navegação ativa, consulta cursos e estudantes, calcula os indicadores e monta a lista de cursos. |
| `cursos.js` / `curso-cadastro.js` | Lista cursos e gerencia cadastro, edição e status. O formulário identifica o professor da sessão pelo `sub` do JWT e pela lista de professores. |
| `modulos.js` / `modulo-cadastro.js` | Lista módulos e gerencia cadastro e edição, selecionando cursos do professor autenticado. |
| `usuarios.js` / `usuario-cadastro.js` | Lista professores e gerencia cadastro e edição, incluindo validação de CPF e associação com empresa. |
| `estudantes.js` | Gerencia listagem, busca, cadastro, edição, detalhes, status e consulta de CEP para estudantes. |
| `matricula.js` | Carrega cursos, professores e estudantes, valida a seleção e envia a matrícula. |
| `acompanhamento.js` | Carrega cursos, estudantes matriculados e módulos; registra conclusões e apresenta o código de certificado. |
| `empresas.js` / `empresa-cadastro.js` | Lista e desativa empresas, mostra professores associados e gerencia cadastro e edição. |
| `enderecos.js` | Consulta um CEP e exibe os dados retornados pela API. |

As páginas de gerenciamento compartilham `css/dashboard.css` para a navegação e estrutura principal, `css/global.css` para estilos comuns e uma folha CSS específica quando necessário. A página de login usa `css/style.css` junto com `css/global.css`.

## Fluxo de autenticação e comunicação com a API

1. Em `index.html`, o formulário envia o CPF somente com dígitos e a senha para `POST /login` usando `apiRequest` com `auth: false`.
2. Se as credenciais forem aceitas, `login.js` lê o campo `token` da resposta, salva o JWT em `localStorage` sob a chave `token` e redireciona para `dashboard.html`.
3. Por padrão, `apiRequest` inclui o token salvo no cabeçalho `Authorization: Bearer <token>`. Chamadas de escrita enviam JSON e definem `Content-Type: application/json`.
4. O módulo lê o corpo da resposta como texto, converte respostas JSON e trata corpos vazios. Para respostas HTTP de erro, cria uma exceção com a mensagem retornada pela API quando disponível.
5. Se uma chamada autenticada retorna `401` ou `403`, `apiRequest` remove o token e redireciona para `index.html`. O botão “Sair” executa o mesmo fluxo de encerramento da sessão.

O frontend não faz validação independente da assinatura JWT. A validação do token e a autorização das rotas são responsabilidade do backend.

## Endpoints consumidos

Os caminhos abaixo são usados pelos módulos do frontend; as operações disponíveis devem ser conferidas nos controllers e no Swagger do backend.

| Recurso | Caminhos usados |
|---|---|
| Login | `POST /login` |
| Professores | `/usuarios` e `/usuarios/{id}` |
| Empresas | `/empresas`, `/empresas/criar` e `/empresas/{id}` |
| Cursos | `/curso`, `/curso/{id}`, `/curso/{cursoId}/modulos`, `/curso/matricula` |
| Módulos | `/modulo`, `/modulo/{id}` e `/modulo/{id}/status` |
| Estudantes | `/estudante`, `/estudante/{id}` e `/estudante/modulos/conclusao` |
| Endereços | `/api/enderecos/{cep}` |

Somente o endpoint de endereços usa o prefixo `/api`. Não acrescente esse prefixo aos demais caminhos.

## Como executar localmente

### Pré-requisitos

- Navegador com suporte a módulos JavaScript ES.
- Backend CodeCamp em execução em `http://localhost:8080`, com PostgreSQL configurado e usuário disponível para autenticação.
- Servidor HTTP estático local. Não abra `index.html` diretamente como arquivo, pois os módulos JavaScript ES devem ser servidos por HTTP.

### Executar com Python

Na raiz do repositório, inicie o servidor estático:

```bash
python3 -m http.server 5502 --bind 127.0.0.1 --directory frontend
```

Abra `http://127.0.0.1:5502` no navegador e entre com o CPF e a senha de um professor já cadastrado.

### Executar com Live Server

Abra a pasta `frontend` no editor e inicie o Live Server na porta `5502`, servindo pela origem `http://127.0.0.1:5502`. A configuração CORS atual do backend permite essa origem. Se usar outra porta, host ou extensão, ajuste a origem permitida em `../src/main/java/com/example/codecamp/config/CorsConfig.java`.

### URL da API

A URL base está definida diretamente em `frontend/js/api.js`:

```js
export const BASE_URL = "http://localhost:8080";
```

Para apontar o frontend a outro servidor, altere essa constante e configure o CORS correspondente no backend. O frontend não carrega `.env` nem lê variáveis de ambiente do navegador.

## Observações

- O cadastro de professores é protegido pela autenticação atual do backend; para entrar é necessário já existir um professor cadastrado.
- Os indicadores e formulários dependem dos dados e das permissões retornados pelo backend.
- A documentação interativa da API é servida pelo backend em `http://localhost:8080/swagger-ui/index.html`.
