// Endpoint base correspondente ao @RequestMapping("/estudante") do controller.
const API_ESTUDANTES = "http://localhost:8080/estudante";

// Referências dos elementos usados para exibir registros, mensagens e formulários.
const studentsTableBody = document.querySelector("#students-table-body");
const studentsFeedback = document.querySelector("#students-feedback");
const newStudentButton = document.querySelector("#new-student-button");
const studentFormDialog = document.querySelector("#student-form-dialog");
const studentForm = document.querySelector("#student-form");
const studentFormTitle = document.querySelector("#student-form-title");
const studentIdInput = document.querySelector("#student-id");
const studentNameInput = document.querySelector("#student-name");
const studentEmailInput = document.querySelector("#student-email");
const studentCpfInput = document.querySelector("#student-cpf");
const studentFormFeedback = document.querySelector("#student-form-feedback");
const studentSaveButton = document.querySelector("#student-save-button");
const studentDetailsDialog = document.querySelector("#student-details-dialog");

// Destaca a página atual no menu, inclusive no caminho /estudantes/ sem extensão.
const normalizePagePath = (path) => path.replace(/\/+$/, "").split("/").pop();
const currentPage = normalizePagePath(window.location.pathname);
document.querySelectorAll(".sidebar__link").forEach((link) => {
  if (normalizePagePath(new URL(link.href).pathname) === currentPage) {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

// Converte campos vazios ou ausentes em um marcador legível.
function formatarValor(valor) {
  return valor === null || valor === undefined || valor === "" ? "—" : String(valor);
}

// Exibe datas ISO da API no formato local e protege contra datas inválidas.
function formatarData(valor) {
  if (valor === null || valor === undefined || valor === "") return "—";
  const data = new Date(valor);
  if (Number.isNaN(data.getTime())) return String(valor);
  return data.toLocaleString("pt-BR", { dateStyle: "short", timeStyle: "short" });
}

// Converte códigos armazenados no banco para rótulos usados na tela.
function normalizarStatus(status) {
  const valor = typeof status === "string" ? status.trim().toLowerCase() : "";
  if (valor === "a" || valor === "ativo") return { texto: "Ativo", ativo: true };
  if (["d", "i", "inativo", "desativado"].includes(valor)) {
    return { texto: valor === "i" || valor === "inativo" ? "Inativo" : "Desativado", ativo: false };
  }
  return { texto: formatarValor(status), ativo: false };
}

// Faz chamadas JSON e acrescenta o JWT usado pelo filtro de autenticação do backend.
async function requisitarEstudantes(path = "", { method = "GET", body } = {}) {
  const token = localStorage.getItem("token");
  if (!token) {
    throw new Error("Sua sessão não foi encontrada. Entre novamente no sistema.");
  }

  const headers = {
    Accept: "*/*",
    Authorization: `Bearer ${token}`,
  };
  if (body !== undefined) headers["Content-Type"] = "application/json";

  const response = await fetch(`${API_ESTUDANTES}${path}`, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  const responseText = await response.text();
  let responseBody = null;
  if (responseText) {
    try {
      responseBody = JSON.parse(responseText);
    } catch {
      responseBody = responseText;
    }
  }

  if (!response.ok) {
    const message = typeof responseBody === "object" && responseBody !== null
      ? responseBody.mensagem || responseBody.message || responseBody.error
      : responseBody;
    throw new Error(message || `A API respondeu com HTTP ${response.status}.`);
  }

  return responseBody;
}

// Cria botões de ação sem inserir dados da API como HTML executável.
function criarBotaoAcao(rotulo, acao, id, classe = "") {
  const button = document.createElement("button");
  button.type = "button";
  button.className = `student-action ${classe}`.trim();
  button.dataset.action = acao;
  button.dataset.id = String(id);
  button.textContent = rotulo;
  return button;
}

// Renderiza a tabela; valores ausentes e objetos nulos não quebram a página.
function exibirEstudantes(estudantes) {
  studentsTableBody.replaceChildren();

  if (estudantes.length === 0) {
    mostrarMensagemTabela("Nenhum estudante cadastrado.");
    studentsFeedback.textContent = "0 estudantes";
    return;
  }

  estudantes.forEach((estudante) => {
    const student = estudante && typeof estudante === "object" ? estudante : {};
    const row = document.createElement("tr");
    const statusCell = document.createElement("td");
    const status = normalizarStatus(student.status);
    const statusBadge = document.createElement("span");
    statusBadge.className = `student-status ${status.ativo ? "student-status--active" : "student-status--inactive"}`;
    statusBadge.textContent = status.texto;
    statusCell.append(statusBadge);

    [student.id, student.nome, student.email, student.cpf].forEach((value) => {
      const cell = document.createElement("td");
      cell.textContent = formatarValor(value);
      row.append(cell);
    });
    row.append(statusCell);

    const createdCell = document.createElement("td");
    createdCell.textContent = formatarData(student.dataCadastro);
    row.append(createdCell);

    const actionsCell = document.createElement("td");
    const actions = document.createElement("div");
    actions.className = "students-table__actions";
    if (student.id !== null && student.id !== undefined && student.id !== "") {
      actions.append(
        criarBotaoAcao("Ver", "details", student.id),
        criarBotaoAcao("Editar", "edit", student.id),
        criarBotaoAcao(status.ativo ? "Desativar" : "Ativar", "toggle-status", student.id),
        criarBotaoAcao("Excluir", "delete", student.id, "student-action--danger"),
      );
    } else {
      actions.textContent = "Ações indisponíveis";
    }
    actionsCell.append(actions);
    row.append(actionsCell);
    studentsTableBody.append(row);
  });

  studentsFeedback.textContent = `${estudantes.length} estudante(s)`;
}

// Mostra uma mensagem dentro da tabela sem deixar linhas de dados antigos.
function mostrarMensagemTabela(mensagem) {
  const row = document.createElement("tr");
  const cell = document.createElement("td");
  cell.className = "students-table__placeholder";
  cell.colSpan = 7;
  cell.textContent = mensagem;
  row.append(cell);
  studentsTableBody.replaceChildren(row);
}

// GET /estudante busca a lista completa no banco por meio do EstudanteRepository.
async function carregarEstudantes() {
  studentsFeedback.textContent = "Carregando estudantes...";
  studentsFeedback.classList.remove("students-feedback--error");
  mostrarMensagemTabela("Carregando estudantes...");

  try {
    const estudantes = await requisitarEstudantes();
    if (!Array.isArray(estudantes)) throw new Error("A API não retornou uma lista de estudantes.");
    exibirEstudantes(estudantes);
  } catch (error) {
    mostrarMensagemTabela("Não foi possível carregar os estudantes.");
    studentsFeedback.textContent = error.message || "Verifique a conexão com a API.";
    studentsFeedback.classList.add("students-feedback--error");
  }
}

// Limpa e abre o formulário usado pelo POST /estudante.
function abrirNovoEstudante() {
  studentForm.reset();
  studentIdInput.value = "";
  studentFormTitle.textContent = "Novo estudante";
  studentSaveButton.textContent = "Salvar estudante";
  studentFormFeedback.textContent = "";
  studentFormFeedback.classList.remove("students-feedback--error");
  studentFormDialog.showModal();
  studentNameInput.focus();
}

// GET /estudante/{id} busca os dados atuais antes de preencher a edição.
async function editarEstudante(id) {
  try {
    const student = await requisitarEstudantes(`/${encodeURIComponent(id)}`);
    studentIdInput.value = formatarValor(student.id);
    studentNameInput.value = student.nome || "";
    studentEmailInput.value = student.email || "";
    studentCpfInput.value = student.cpf || "";
    studentFormTitle.textContent = `Editar estudante #${formatarValor(student.id)}`;
    studentSaveButton.textContent = "Salvar alterações";
    studentFormFeedback.textContent = "";
    studentFormFeedback.classList.remove("students-feedback--error");
    studentFormDialog.showModal();
  } catch (error) {
    studentsFeedback.textContent = error.message || "Não foi possível consultar o estudante.";
    studentsFeedback.classList.add("students-feedback--error");
  }
}

// GET /estudante/{id} exibe todos os campos retornados pelo DTO de consulta.
async function consultarEstudante(id) {
  try {
    const student = await requisitarEstudantes(`/${encodeURIComponent(id)}`);
    const campos = {
      "student-details-id": student.id,
      "student-details-name": student.nome,
      "student-details-email": student.email,
      "student-details-cpf": student.cpf,
      "student-details-status": normalizarStatus(student.status).texto,
      "student-details-created": formatarData(student.dataCadastro),
      "student-details-updated": formatarData(student.dataAtualizacao),
    };
    Object.entries(campos).forEach(([elementId, value]) => {
      document.getElementById(elementId).textContent = formatarValor(value);
    });
    studentDetailsDialog.showModal();
  } catch (error) {
    studentsFeedback.textContent = error.message || "Não foi possível consultar o estudante.";
    studentsFeedback.classList.add("students-feedback--error");
  }
}

// PATCH /estudante/{id}/status altera o status para A ou D sem excluir o registro.
async function alternarStatus(id, button) {
  const desativando = button.textContent.trim().toLowerCase() === "desativar";
  const novoStatus = desativando ? "D" : "A";
  button.disabled = true;

  try {
    await requisitarEstudantes(`/${encodeURIComponent(id)}/status`, {
      method: "PATCH",
      body: { status: novoStatus },
    });
    await carregarEstudantes();
  } catch (error) {
    studentsFeedback.textContent = error.message || "Não foi possível alterar o status.";
    studentsFeedback.classList.add("students-feedback--error");
    button.disabled = false;
  }
}

// DELETE /estudante/{id} segue o controller, que marca o registro como D.
async function excluirEstudante(id, button) {
  if (!window.confirm(`Deseja desativar o estudante #${id}?`)) return;
  button.disabled = true;

  try {
    await requisitarEstudantes(`/${encodeURIComponent(id)}`, { method: "DELETE" });
    await carregarEstudantes();
  } catch (error) {
    studentsFeedback.textContent = error.message || "Não foi possível excluir o estudante.";
    studentsFeedback.classList.add("students-feedback--error");
    button.disabled = false;
  }
}

// POST cria estudante; PUT /estudante/{id} atualiza o estudante selecionado.
async function salvarEstudante(event) {
  event.preventDefault();
  studentFormFeedback.textContent = "Salvando...";
  studentFormFeedback.classList.remove("students-feedback--error");
  studentSaveButton.disabled = true;

  const dados = {
    nome: studentNameInput.value.trim(),
    email: studentEmailInput.value.trim(),
    cpf: studentCpfInput.value.trim(),
  };
  const id = studentIdInput.value;

  try {
    await requisitarEstudantes(id ? `/${encodeURIComponent(id)}` : "", {
      method: id ? "PUT" : "POST",
      body: dados,
    });
    studentFormDialog.close();
    await carregarEstudantes();
  } catch (error) {
    studentFormFeedback.textContent = error.message || "Não foi possível salvar o estudante.";
    studentFormFeedback.classList.add("students-feedback--error");
  } finally {
    studentSaveButton.disabled = false;
  }
}

// O botão abre o formulário de inclusão.
newStudentButton.addEventListener("click", abrirNovoEstudante);

// Processa as ações da tabela através de um único listener delegado.
studentsTableBody.addEventListener("click", (event) => {
  const button = event.target.closest("button[data-action]");
  if (!button) return;

  const { action, id } = button.dataset;
  if (action === "details") consultarEstudante(id);
  if (action === "edit") editarEstudante(id);
  if (action === "toggle-status") alternarStatus(id, button);
  if (action === "delete") excluirEstudante(id, button);
});

// Envia cadastro/edição e fecha diálogos pelos botões com data-close-dialog.
studentForm.addEventListener("submit", salvarEstudante);
document.querySelectorAll("[data-close-dialog]").forEach((button) => {
  button.addEventListener("click", () => {
    document.getElementById(button.dataset.closeDialog).close();
  });
});

// Fecha o diálogo ao clicar fora de seu painel.
document.querySelectorAll("dialog").forEach((dialog) => {
  dialog.addEventListener("click", (event) => {
    if (event.target === dialog) dialog.close();
  });
});

// Inicia a listagem assim que a página sem extensão é carregada.
carregarEstudantes();
