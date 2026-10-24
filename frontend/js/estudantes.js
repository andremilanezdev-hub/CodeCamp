import { apiRequest } from "./api.js";

const studentsTableBody = document.querySelector("#students-table-body");
const studentsFeedback = document.querySelector("#students-feedback");
const studentsSearchInput = document.querySelector("#students-search-input");
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

let estudantes = [];

// Marca a página atual no menu lateral.
document.querySelectorAll(".sidebar__link").forEach((link) => {
  if (new URL(link.href).pathname === window.location.pathname) {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

function formatarValor(valor) {
  return valor === null || valor === undefined || valor === "" ? "—" : String(valor);
}

function formatarData(valor) {
  if (valor === null || valor === undefined || valor === "") return "—";

  let data;
  if (Array.isArray(valor)) {
    const [ano, mes, dia, hora = 0, minuto = 0, segundo = 0, nanos = 0] = valor;
    data = new Date(ano, mes - 1, dia, hora, minuto, segundo, Math.floor(nanos / 1_000_000));
  } else {
    data = new Date(valor);
  }

  if (Number.isNaN(data.getTime())) return String(valor);
  return data.toLocaleString("pt-BR", { dateStyle: "short", timeStyle: "short" });
}

function normalizarStatus(status) {
  const codigo = typeof status === "string" ? status.trim().toLowerCase() : "";
  if (codigo === "a" || codigo === "ativo") return { texto: "Ativo", ativo: true };
  if (codigo === "d" || codigo === "desativado") return { texto: "Desativado", ativo: false };
  if (codigo === "i" || codigo === "inativo") return { texto: "Inativo", ativo: false };
  return { texto: formatarValor(status), ativo: false };
}

function exibirFeedback(mensagem, tipo = "") {
  studentsFeedback.textContent = mensagem;
  studentsFeedback.classList.toggle("students-feedback--error", tipo === "erro");
}

function exibirFeedbackFormulario(mensagem, tipo = "") {
  studentFormFeedback.textContent = mensagem;
  studentFormFeedback.classList.toggle("students-feedback--error", tipo === "erro");
}

function mensagemDeErro(error) {
  if (error.status === 401) return "Sua sessão expirou. Entre novamente para continuar.";
  if (error.status === 403) return "Você não tem permissão para realizar esta operação.";
  if (error instanceof TypeError) return "Não foi possível conectar à API. Verifique se o servidor está ativo.";
  return error.message || "Ocorreu um erro. Tente novamente.";
}

function criarCelula(valor) {
  const cell = document.createElement("td");
  cell.textContent = formatarValor(valor);
  return cell;
}

function criarBotaoAcao(rotulo, acao, id, classe = "") {
  const button = document.createElement("button");
  button.type = "button";
  button.className = `student-action ${classe}`.trim();
  button.dataset.action = acao;
  button.dataset.id = String(id);
  button.textContent = rotulo;
  return button;
}

function mostrarMensagemTabela(mensagem) {
  const row = document.createElement("tr");
  const cell = document.createElement("td");
  cell.className = "students-table__placeholder";
  cell.colSpan = 7;
  cell.textContent = mensagem;
  row.append(cell);
  studentsTableBody.replaceChildren(row);
}

function exibirEstudantes() {
  const termo = studentsSearchInput.value.trim().toLocaleLowerCase("pt-BR");
  const filtrados = estudantes.filter((estudante) => {
    const texto = [estudante.id, estudante.nome, estudante.email, estudante.cpf]
      .map((valor) => String(valor ?? "").toLocaleLowerCase("pt-BR"))
      .join(" ");
    return texto.includes(termo);
  });

  if (filtrados.length === 0) {
    mostrarMensagemTabela(estudantes.length === 0
      ? "Nenhum estudante cadastrado."
      : "Nenhum estudante corresponde à busca.");
    studentsFeedback.textContent = estudantes.length === 0
      ? "0 estudantes"
      : `0 resultados de ${estudantes.length} estudantes`;
    return;
  }

  studentsTableBody.replaceChildren();
  filtrados.forEach((estudante) => {
    const row = document.createElement("tr");
    const status = normalizarStatus(estudante.status);
    const statusCell = document.createElement("td");
    const statusBadge = document.createElement("span");
    statusBadge.className = `student-status ${status.ativo ? "student-status--active" : "student-status--inactive"}`;
    statusBadge.textContent = status.texto;
    statusCell.append(statusBadge);

    row.append(
      criarCelula(estudante.id),
      criarCelula(estudante.nome),
      criarCelula(estudante.email),
      criarCelula(estudante.cpf),
      statusCell,
      criarCelula(formatarData(estudante.dataCadastro)),
    );

    const actionsCell = document.createElement("td");
    const actions = document.createElement("div");
    actions.className = "students-table__actions";
    actions.append(
      criarBotaoAcao("Ver", "details", estudante.id),
      criarBotaoAcao("Editar", "edit", estudante.id),
      status.ativo
        ? criarBotaoAcao("Desativar", "deactivate", estudante.id, "student-action--danger")
        : criarBotaoAcao("Ativar", "activate", estudante.id, "student-action--secondary"),
    );
    actionsCell.append(actions);
    row.append(actionsCell);
    studentsTableBody.append(row);
  });

  studentsFeedback.textContent = termo
    ? `${filtrados.length} resultado(s) de ${estudantes.length} estudante(s)`
    : `${estudantes.length} estudante(s)`;
}

async function carregarEstudantes() {
  exibirFeedback("Carregando estudantes...");
  mostrarMensagemTabela("Carregando estudantes...");

  try {
    const resposta = await apiRequest("/estudante");
    if (!Array.isArray(resposta)) {
      throw new Error("A API não retornou uma lista de estudantes.");
    }
    estudantes = resposta;
    exibirEstudantes();
    return true;
  } catch (error) {
    estudantes = [];
    mostrarMensagemTabela("Não foi possível carregar os estudantes.");
    exibirFeedback(mensagemDeErro(error), "erro");
    return false;
  }
}

function abrirNovoEstudante() {
  studentForm.reset();
  studentIdInput.value = "";
  studentFormTitle.textContent = "Novo estudante";
  studentSaveButton.textContent = "Salvar estudante";
  exibirFeedbackFormulario("");
  studentFormDialog.showModal();
  studentNameInput.focus();
}

async function consultarEstudante(id) {
  try {
    const estudante = await apiRequest(`/estudante/${encodeURIComponent(id)}`);
    const campos = {
      "student-details-id": estudante.id,
      "student-details-name": estudante.nome,
      "student-details-email": estudante.email,
      "student-details-cpf": estudante.cpf,
      "student-details-status": normalizarStatus(estudante.status).texto,
      "student-details-created": formatarData(estudante.dataCadastro),
      "student-details-updated": formatarData(estudante.dataAtualizacao),
    };
    Object.entries(campos).forEach(([elementId, value]) => {
      document.getElementById(elementId).textContent = formatarValor(value);
    });
    studentDetailsDialog.showModal();
  } catch (error) {
    exibirFeedback(mensagemDeErro(error), "erro");
  }
}

async function editarEstudante(id) {
  try {
    const estudante = await apiRequest(`/estudante/${encodeURIComponent(id)}`);
    studentIdInput.value = estudante.id ?? "";
    studentNameInput.value = estudante.nome ?? "";
    studentEmailInput.value = estudante.email ?? "";
    studentCpfInput.value = estudante.cpf ?? "";
    studentFormTitle.textContent = `Editar estudante #${formatarValor(estudante.id)}`;
    studentSaveButton.textContent = "Salvar alterações";
    exibirFeedbackFormulario("");
    studentFormDialog.showModal();
    studentNameInput.focus();
  } catch (error) {
    exibirFeedback(mensagemDeErro(error), "erro");
  }
}

async function salvarEstudante(event) {
  event.preventDefault();
  studentSaveButton.disabled = true;
  exibirFeedbackFormulario("Salvando...");

  const id = studentIdInput.value;
  const dados = {
    nome: studentNameInput.value.trim(),
    email: studentEmailInput.value.trim(),
    cpf: studentCpfInput.value.trim(),
  };

  try {
    const resposta = await apiRequest(id ? `/estudante/${encodeURIComponent(id)}` : "/estudante", {
      method: id ? "PUT" : "POST",
      body: dados,
    });
    studentFormDialog.close();
    const carregouLista = await carregarEstudantes();
    if (carregouLista) exibirFeedback(resposta?.mensagem || "Estudante salvo com sucesso.");
  } catch (error) {
    exibirFeedbackFormulario(mensagemDeErro(error), "erro");
  } finally {
    studentSaveButton.disabled = false;
  }
}

async function alterarStatus(id, acao, button) {
  const estudante = estudantes.find((item) => String(item.id) === String(id));
  if (!estudante) return;

  if (acao === "deactivate" && !window.confirm(`Deseja desativar ${estudante.nome || `o estudante #${id}`}?`)) {
    return;
  }

  button.disabled = true;
  try {
    if (acao === "deactivate") {
      // DELETE /estudante/{id} desativa o cadastro sem removê-lo do banco.
      await apiRequest(`/estudante/${encodeURIComponent(id)}`, { method: "DELETE" });
      const carregouLista = await carregarEstudantes();
      if (carregouLista) exibirFeedback("Estudante desativado.");
    } else {
      // PATCH /estudante/{id}/status reativa um cadastro desativado.
      const resposta = await apiRequest(`/estudante/${encodeURIComponent(id)}/status`, {
        method: "PATCH",
        body: { status: "A" },
      });
      const carregouLista = await carregarEstudantes();
      if (carregouLista) exibirFeedback(resposta?.mensagem || "Estudante ativado.");
    }
  } catch (error) {
    exibirFeedback(mensagemDeErro(error), "erro");
    button.disabled = false;
  }
}

newStudentButton.addEventListener("click", abrirNovoEstudante);
studentsSearchInput.addEventListener("input", exibirEstudantes);
studentForm.addEventListener("submit", salvarEstudante);

studentsTableBody.addEventListener("click", (event) => {
  const button = event.target.closest("button[data-action]");
  if (!button) return;

  const { action, id } = button.dataset;
  if (action === "details") consultarEstudante(id);
  if (action === "edit") editarEstudante(id);
  if (action === "activate" || action === "deactivate") alterarStatus(id, action, button);
});

document.querySelectorAll("[data-close-dialog]").forEach((button) => {
  button.addEventListener("click", () => {
    document.getElementById(button.dataset.closeDialog).close();
  });
});

document.querySelectorAll("dialog").forEach((dialog) => {
  dialog.addEventListener("click", (event) => {
    if (event.target === dialog) dialog.close();
  });
});

carregarEstudantes();
