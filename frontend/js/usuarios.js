import { apiRequest } from "./api.js";
import { formatarCpf } from "./mascaras.js";

const currentPage = window.location.pathname.split("/").pop();
const navigationLinks = document.querySelectorAll(".sidebar__link");
const usersTableBody = document.querySelector("#users-table-body");
const usersFeedback = document.querySelector("#users-feedback");
const addUserButton = document.querySelector("#add-user-button");

// Abre o formulário dedicado de cadastro.
addUserButton?.addEventListener("click", () => {
  window.location.assign("usuario-cadastro.html");
});

// Destaca Usuários no menu lateral enquanto esta página estiver aberta.
navigationLinks.forEach((link) => {
  if (link.getAttribute("href").split("/").pop() === currentPage) {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

function criarCelula(texto) {
  const cell = document.createElement("td");
  cell.textContent = texto || "Não informado";
  return cell;
}

function obterNomeEmpresa(usuario) {
  // A API pode retornar empresa como null ou como um objeto relacionado.
  const empresa = usuario.empresa;

  if (typeof empresa === "string") {
    return empresa;
  }

  if (usuario.razaosocialEmprea) return usuario.razaosocialEmprea;

  if (!empresa || typeof empresa !== "object") {
    return "Não informado";
  }

  return empresa.razaoSocial || empresa.nomeFantasia || "Não informado";
}

function obterStatus(status) {
  const valor = String(status ?? "").trim().toUpperCase();
  if (valor === "A") return { texto: "Ativo", ativo: true };
  if (valor === "D" || valor === "I") return { texto: valor === "D" ? "Desativado" : "Inativo", ativo: false };
  return { texto: "Não informado", ativo: false };
}

function exibirUsuarios(usuarios) {
  usersTableBody.replaceChildren();

  if (usuarios.length === 0) {
    const row = document.createElement("tr");
    const cell = criarCelula("Nenhum usuário cadastrado.");
    cell.colSpan = 6;
    cell.classList.add("users-table__placeholder");
    row.append(cell);
    usersTableBody.append(row);
    usersFeedback.textContent = "0 usuários";
    return;
  }

  usuarios.forEach((usuario) => {
    const row = document.createElement("tr");
    const status = obterStatus(usuario.status);
    const statusCell = document.createElement("td");
    const statusBadge = document.createElement("span");
    statusBadge.className = `status-badge ${status.ativo ? "status-badge--active" : "status-badge--inactive"}`;
    statusBadge.textContent = status.texto;
    statusCell.append(statusBadge);

    row.append(
      criarCelula(usuario.nome),
      criarCelula(formatarCpf(usuario.cpf)),
      criarCelula(usuario.dataNascimento),
      criarCelula(obterNomeEmpresa(usuario)),
      statusCell,
    );

    const actionsCell = document.createElement("td");
    const actions = document.createElement("div");
    actions.className = "table-actions";
    const editButton = document.createElement("button");
    editButton.type = "button";
    editButton.className = "action-button";
    editButton.textContent = "Editar";
    editButton.addEventListener("click", () => {
      window.location.assign(`usuario-cadastro.html?id=${encodeURIComponent(usuario.id)}`);
    });
    actions.append(editButton);

    const statusButton = document.createElement("button");
    statusButton.type = "button";
    statusButton.className = `action-button ${status.ativo ? "action-button--danger" : "action-button--secondary"}`;
    statusButton.textContent = status.ativo ? "Desativar" : "Ativar";
    statusButton.addEventListener("click", () => alterarStatusUsuario(usuario, statusButton, status.ativo));
    actions.append(statusButton);
    actionsCell.append(actions);
    row.append(actionsCell);
    usersTableBody.append(row);
  });

  usersFeedback.textContent = `${usuarios.length} usuário(s)`;
}

async function carregarUsuarios() {
  try {
    // Lê os campos nome, cpf, status e empresa conforme o JSON da API.
    const resposta = await apiRequest("/usuarios");
    // O endpoint pode responder com um usuário ou com uma lista de usuários.
    const usuarios = Array.isArray(resposta)
      ? resposta
      : resposta && typeof resposta === "object"
        ? [resposta]
        : null;

    if (!Array.isArray(usuarios)) {
      throw new Error("A resposta da API não está no formato esperado.");
    }

    exibirUsuarios(usuarios);
  } catch (error) {
    usersTableBody.replaceChildren();
    const row = document.createElement("tr");
    const cell = criarCelula("Não foi possível carregar os usuários.");
    cell.colSpan = 6;
    cell.classList.add("users-table__placeholder");
    row.append(cell);
    usersTableBody.append(row);
    usersFeedback.textContent = error.message || "Erro ao carregar os dados.";
    usersFeedback.classList.add("users-feedback--error");
  }
}

async function alterarStatusUsuario(usuario, button, estaAtivo) {
  const acao = estaAtivo ? "desativar" : "ativar";
  if (estaAtivo && !window.confirm(`Deseja desativar o usuário “${usuario.nome}”?`)) return;
  button.disabled = true;
  try {
    if (estaAtivo) {
      // DELETE do controller é uma desativação lógica, sem remover o registro.
      await apiRequest(`/usuarios/${encodeURIComponent(usuario.id)}`, { method: "DELETE" });
    } else {
      await apiRequest(`/usuarios/${encodeURIComponent(usuario.id)}/status`, {
        method: "PATCH",
        body: { status: "A" },
      });
    }
    await carregarUsuarios();
  } catch (error) {
    usersFeedback.textContent = error.message || `Não foi possível ${acao} o usuário.`;
    usersFeedback.classList.add("users-feedback--error");
    button.disabled = false;
  }
}

carregarUsuarios();
