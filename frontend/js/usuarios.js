import { apiRequest } from "./api.js";

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

  if (!empresa || typeof empresa !== "object") {
    return "Não informado";
  }

  return empresa.razaoSocial || empresa.nomeFantasia || "Não informado";
}

function obterClasseStatus(status) {
  const valor = status?.trim().toLowerCase();

  if (valor === "ativo") {
    return "status-badge status-badge--active";
  }

  if (valor === "inativo") {
    return "status-badge status-badge--inactive";
  }

  return "status-badge";
}

function exibirUsuarios(usuarios) {
  usersTableBody.replaceChildren();

  if (usuarios.length === 0) {
    const row = document.createElement("tr");
    const cell = criarCelula("Nenhum usuário cadastrado.");
    cell.colSpan = 4;
    cell.classList.add("users-table__placeholder");
    row.append(cell);
    usersTableBody.append(row);
    usersFeedback.textContent = "0 usuários";
    return;
  }

  usuarios.forEach((usuario) => {
    const row = document.createElement("tr");
    const statusCell = document.createElement("td");
    const statusBadge = document.createElement("span");
    const status = usuario.status || "Não informado";

    row.append(
      criarCelula(usuario.nome),
      criarCelula(usuario.cpf),
    );

    statusBadge.className = obterClasseStatus(usuario.status);
    statusBadge.textContent = status;
    statusCell.append(statusBadge);
    row.append(statusCell, criarCelula(obterNomeEmpresa(usuario)));
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
    cell.colSpan = 4;
    cell.classList.add("users-table__placeholder");
    row.append(cell);
    usersTableBody.append(row);
    usersFeedback.textContent = error.message || "Erro ao carregar os dados.";
    usersFeedback.classList.add("users-feedback--error");
  }
}

carregarUsuarios();
