import { apiRequest } from "./api.js";

const currentPage = window.location.pathname.split("/").pop();
const navigationLinks = document.querySelectorAll(".sidebar__link");
const modulesTableBody = document.querySelector("#modules-table-body");
const modulesFeedback = document.querySelector("#modules-feedback");
const newModuleButton = document.querySelector("#new-module-button");
let instructorId = null;
let coursesById = new Map();

newModuleButton?.addEventListener("click", () => {
  window.location.assign("modulo-cadastro.html");
});

// Marca Módulos como item ativo no menu lateral.
navigationLinks.forEach((link) => {
  if (link.getAttribute("href").split("/").pop() === currentPage) {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

// Exibe um traço para valores ausentes sem perder valores numéricos iguais a zero.
function formatarValor(valor) {
  return valor === null || valor === undefined || valor === "" ? "-" : String(valor);
}

function criarCelula(valor) {
  const cell = document.createElement("td");
  cell.textContent = formatarValor(valor);
  return cell;
}

function formatarStatus(status) {
  if (status === null || status === undefined || status === "") {
    return { texto: "-", classe: "" };
  }

  const valor = String(status).trim();
  const normalizado = valor.toUpperCase();
  if (normalizado === "A") return { texto: "Ativo", classe: "module-status--active" };
  if (normalizado === "I" || normalizado === "D") return { texto: normalizado === "D" ? "Desativado" : "Inativo", classe: "module-status--inactive" };
  return { texto: valor, classe: "" };
}

function criarCelulaStatus(status) {
  const cell = document.createElement("td");
  const badge = document.createElement("span");
  const resultado = formatarStatus(status);

  badge.className = `module-status${resultado.classe ? ` ${resultado.classe}` : ""}`;
  badge.textContent = resultado.texto;
  cell.append(badge);
  return cell;
}

function obterNomeCurso(modulo) {
  if (typeof modulo.cursoNome === "string" && modulo.cursoNome.trim()) return modulo.cursoNome;
  return modulo.curso_id ? `Curso #${modulo.curso_id}` : "-";
}

function exibirMensagemTabela(texto) {
  const row = document.createElement("tr");
  const cell = document.createElement("td");
  cell.className = "modules-table__placeholder";
  cell.colSpan = 6;
  cell.textContent = texto;
  row.append(cell);
  modulesTableBody.replaceChildren(row);
}

function exibirModulos(modulos) {
  modulesTableBody.replaceChildren();

  if (modulos.length === 0) {
    exibirMensagemTabela("Nenhum módulo cadastrado.");
    modulesFeedback.textContent = "0 módulos";
    return;
  }

  modulos.forEach((item) => {
    const modulo = item && typeof item === "object" ? item : {};
    const row = document.createElement("tr");
    row.append(
      criarCelula(modulo.id),
      criarCelula(modulo.nome),
      criarCelula(modulo.cargahoraria),
      criarCelulaStatus(modulo.status),
      criarCelula(obterNomeCurso(modulo)),
    );
    const actionsCell = document.createElement("td");
    const curso = coursesById.get(String(modulo.curso_id));
    const pertenceAoInstrutor = instructorId && String(curso?.usuarioCadastro?.id) === String(instructorId);
    if (pertenceAoInstrutor) {
      const actions = document.createElement("div");
      actions.className = "table-actions";
      const editButton = document.createElement("button");
      editButton.type = "button";
      editButton.className = "action-button";
      editButton.textContent = "Editar";
      editButton.addEventListener("click", () => {
        window.location.assign(`modulo-cadastro.html?id=${encodeURIComponent(modulo.id)}`);
      });
      actions.append(editButton);

      const status = String(modulo.status ?? "").toUpperCase();
      const estaDesativado = status === "D" || status === "I";
      const statusButton = document.createElement("button");
      statusButton.type = "button";
      statusButton.className = estaDesativado ? "action-button action-button--secondary" : "action-button action-button--danger";
      statusButton.textContent = estaDesativado ? "Ativar" : "Desativar";
      statusButton.addEventListener("click", () => alterarStatusModulo(modulo, statusButton, estaDesativado));
      actions.append(statusButton);
      actionsCell.append(actions);
    } else {
      actionsCell.textContent = "—";
    }
    row.append(actionsCell);
    modulesTableBody.append(row);
  });

  modulesFeedback.textContent = `${modulos.length} módulo(s)`;
}

async function identificarInstrutor() {
  const token = localStorage.getItem("token");
  const payloadPart = token?.split(".")[1];
  if (!payloadPart) throw new Error("Sessão não encontrada. Entre novamente.");
  const normalized = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
  const subject = JSON.parse(atob(normalized.padEnd(Math.ceil(normalized.length / 4) * 4, "="))).sub;
  const usuarios = await apiRequest("/usuarios");
  const cpf = String(subject ?? "").replace(/\D/g, "");
  instructorId = usuarios.find((usuario) => String(usuario.cpf ?? "").replace(/\D/g, "") === cpf)?.id ?? null;
  if (!instructorId) throw new Error("Não foi possível identificar o instrutor autenticado.");
}

async function alterarStatusModulo(modulo, button, ativar) {
  const textoAcao = ativar ? "ativar" : "desativar";
  if (!ativar && !window.confirm(`Deseja desativar o módulo “${modulo.nome}”?`)) return;
  button.disabled = true;
  try {
    if (ativar) {
      await apiRequest(`/modulo/${encodeURIComponent(modulo.id)}/status`, {
        method: "PATCH",
        body: { usuario_id: Number(instructorId), status: "A" },
      });
    } else {
      await apiRequest(`/modulo/${encodeURIComponent(modulo.id)}?usuario_id=${encodeURIComponent(instructorId)}`, {
        method: "DELETE",
      });
    }
    await carregarModulos();
  } catch (error) {
    modulesFeedback.textContent = error.message || `Não foi possível ${textoAcao} o módulo.`;
    modulesFeedback.classList.add("modules-feedback--error");
    button.disabled = false;
  }
}

async function carregarModulos() {
  modulesFeedback.textContent = "Carregando módulos...";
  modulesFeedback.classList.remove("modules-feedback--error");

  try {
    await identificarInstrutor();
    const cursos = await apiRequest("/curso");
    coursesById = new Map((Array.isArray(cursos) ? cursos : []).map((curso) => [String(curso.id), curso]));
    // Busca a coleção completa em GET http://localhost:8080/modulo.
    const resposta = await apiRequest("/modulo");
    const modulos = Array.isArray(resposta)
      ? resposta
      : resposta && typeof resposta === "object"
        ? [resposta]
        : null;

    if (!Array.isArray(modulos)) {
      throw new Error("A resposta da API não está no formato esperado.");
    }

    exibirModulos(modulos);
  } catch (error) {
    exibirMensagemTabela("Não foi possível carregar os módulos.");
    modulesFeedback.textContent = error.message || "Verifique sua conexão e tente novamente.";
    modulesFeedback.classList.add("modules-feedback--error");
  }
}

carregarModulos();
