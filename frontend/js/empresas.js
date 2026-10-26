import { apiRequest } from "./api.js";
import { formatarCnpj, formatarCpf } from "./mascaras.js";

const currentPage = window.location.pathname.split("/").pop();
const navigationLinks = document.querySelectorAll(".sidebar__link");
const companiesTableBody = document.querySelector("#companies-table-body");
const companiesFeedback = document.querySelector("#companies-feedback");
const newCompanyButton = document.querySelector("#new-company-button");
const companyUsersSection = document.querySelector("#company-users-section");
const companyUsersTitle = document.querySelector("#company-users-title");
const companyUsersSummary = document.querySelector("#company-users-summary");
const companyUsersGrid = document.querySelector("#company-users-grid");
const closeCompanyUsersButton = document.querySelector("#close-company-users");

// Abre o formulário dedicado para cadastrar uma empresa.
newCompanyButton?.addEventListener("click", () => {
  window.location.assign("empresa-cadastro.html");
});

// A consulta de usuários fica recolhida até uma empresa ser selecionada, mantendo
// a listagem principal limpa e apresentando o resultado dentro do próprio fluxo.
closeCompanyUsersButton?.addEventListener("click", () => {
  companyUsersSection.hidden = true;
  companyUsersGrid.replaceChildren();
});

// Indica no menu lateral qual página está aberta.
navigationLinks.forEach((link) => {
  if (link.getAttribute("href").split("/").pop() === currentPage) {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

// Converte valores nulos ou vazios em um traço e preserva valores como o número zero.
function formatarValor(valor) {
  return valor === null || valor === undefined || valor === "" ? "-" : String(valor);
}

function criarCelula(valor) {
  const cell = document.createElement("td");
  cell.textContent = formatarValor(valor);
  return cell;
}

function exibirMensagemTabela(mensagem) {
  const row = document.createElement("tr");
  const cell = document.createElement("td");
  cell.className = "companies-table__placeholder";
  cell.colSpan = 7;
  cell.textContent = mensagem;
  row.append(cell);
  companiesTableBody.replaceChildren(row);
}

function exibirEmpresas(empresas) {
  companiesTableBody.replaceChildren();

  if (empresas.length === 0) {
    exibirMensagemTabela("Nenhuma empresa cadastrada.");
    companiesFeedback.textContent = "0 empresas";
    return;
  }

  empresas.forEach((empresa) => {
    const row = document.createElement("tr");
    row.append(
      criarCelula(empresa.razaoSocial),
      criarCelula(empresa.nomeFantasia),
      criarCelula(formatarCnpj(empresa.cnpj)),
      // Mantém o nome da propriedade conforme o contrato da API informado.
      criarCelula(empresa.inscricaoEstaual ?? empresa.inscricaoEstadual),
      criarCelula(empresa.quantidadeUsuarios),
    );
    const estaDesativada = String(empresa.status ?? "A").toUpperCase() === "D";
    const statusCell = document.createElement("td");
    const statusBadge = document.createElement("span");
    statusBadge.className = `company-status ${estaDesativada ? "company-status--inactive" : "company-status--active"}`;
    statusBadge.textContent = estaDesativada ? "Desativada" : "Ativa";
    statusCell.append(statusBadge);
    row.append(statusCell);

    const actionsCell = document.createElement("td");
    const actions = document.createElement("div");
    actions.className = "table-actions";
    if (!estaDesativada) {
      const editButton = document.createElement("button");
      editButton.type = "button";
      editButton.className = "action-button";
      editButton.textContent = "Editar";
      editButton.addEventListener("click", () => {
        window.location.assign(`empresa-cadastro.html?id=${encodeURIComponent(empresa.id)}`);
      });
      actions.append(editButton);

      const deactivateButton = document.createElement("button");
      deactivateButton.type = "button";
      deactivateButton.className = "action-button action-button--danger";
      deactivateButton.textContent = "Desativar";
      deactivateButton.addEventListener("click", () => desativarEmpresa(empresa, deactivateButton));
      actions.append(deactivateButton);
    }

    const usersButton = document.createElement("button");
    usersButton.type = "button";
    usersButton.className = "action-button";
    usersButton.textContent = "Ver usuários";
    usersButton.addEventListener("click", () => buscarUsuariosDaEmpresa(empresa, usersButton));
    actions.append(usersButton);
    actionsCell.append(actions);
    row.append(actionsCell);
    companiesTableBody.append(row);
  });

  companiesFeedback.textContent = `${empresas.length} empresa(s)`;
}

async function desativarEmpresa(empresa, button) {
  if (!window.confirm(`Deseja desativar a empresa “${empresa.nomeFantasia || empresa.razaoSocial}”?`)) return;
  button.disabled = true;
  try {
    await apiRequest(`/empresas/${encodeURIComponent(empresa.id)}`, { method: "DELETE" });
    await carregarEmpresas();
  } catch (error) {
    companiesFeedback.textContent = error.message || "Não foi possível desativar a empresa.";
    companiesFeedback.classList.add("companies-feedback--error");
    button.disabled = false;
  }
}

async function buscarUsuariosDaEmpresa(empresa, button) {
  button.disabled = true;
  button.textContent = "Carregando...";
  try {
    // Consulta a lista já existente e filtra pelo ID da empresa. O ID evita pôr a
    // barra do CNPJ no path e funciona mesmo em registros antigos com máscara.
    const resposta = await apiRequest("/usuarios");
    const usuarios = Array.isArray(resposta) ? resposta : [];
    const usuariosDaEmpresa = usuarios.filter(
      (usuario) => String(usuario.empresa_id) === String(empresa.id),
    );
    exibirUsuariosDaEmpresa(empresa, usuariosDaEmpresa);
  } catch (error) {
    companiesFeedback.textContent = error.message || "Não foi possível consultar os usuários desta empresa.";
    companiesFeedback.classList.add("companies-feedback--error");
  } finally {
    button.disabled = false;
    button.textContent = "Ver usuários";
  }
}

// Renderiza os usuários como cards: cada registro tem espaço para seus dados e
// pode ser lido em telas estreitas sem transformar a resposta em uma caixa modal.
function exibirUsuariosDaEmpresa(empresa, usuarios) {
  const nomeEmpresa = empresa.nomeFantasia || empresa.razaoSocial || "Empresa";
  companyUsersTitle.textContent = `Usuários de ${nomeEmpresa}`;
  companyUsersGrid.replaceChildren();

  if (usuarios.length === 0) {
    const estadoVazio = document.createElement("p");
    estadoVazio.className = "company-users-empty";
    estadoVazio.textContent = "Esta empresa não possui usuários cadastrados.";
    companyUsersGrid.append(estadoVazio);
    companyUsersSummary.textContent = "Nenhum usuário encontrado";
  } else {
    usuarios.forEach((usuario) => companyUsersGrid.append(criarCartaoUsuario(usuario)));
    companyUsersSummary.textContent = `${usuarios.length} usuário(s) cadastrado(s)`;
  }

  companyUsersSection.hidden = false;
  companyUsersSection.scrollIntoView({ behavior: "smooth", block: "start" });
}

// Cria os elementos usando textContent para que valores vindos da API sejam
// exibidos como texto e não interpretados como marcação HTML.
function criarCartaoUsuario(usuario) {
  const card = document.createElement("article");
  card.className = "company-user-card";

  const cabecalho = document.createElement("div");
  cabecalho.className = "company-user-card__header";
  const nome = document.createElement("h3");
  nome.textContent = usuario.nome || "Usuário sem nome";
  const statusTexto = formatarStatusUsuario(usuario.status);
  const status = document.createElement("span");
  status.className = `company-user-status ${statusTexto.ativo ? "company-user-status--active" : "company-user-status--inactive"}`;
  status.textContent = statusTexto.rotulo;
  cabecalho.append(nome, status);

  const documento = document.createElement("p");
  documento.className = "company-user-card__detail";
  const documentoRotulo = document.createElement("span");
  documentoRotulo.textContent = "CPF";
  const documentoValor = document.createElement("strong");
  documentoValor.textContent = usuario.cpf ? formatarCpf(usuario.cpf) : "Não informado";
  documento.append(documentoRotulo, documentoValor);

  card.append(cabecalho, documento);
  return card;
}

// O backend do projeto representa o status ativo como "A"; valores ausentes ou
// diferentes disso aparecem como inativos para não sugerir uma permissão indevida.
function formatarStatusUsuario(status) {
  const valor = String(status ?? "").toUpperCase();
  const ativo = valor === "A";
  return { ativo, rotulo: ativo ? "Ativo" : valor === "D" ? "Desativado" : "Inativo" };
}

async function carregarEmpresas() {
  companiesFeedback.textContent = "Carregando empresas...";
  companiesFeedback.classList.remove("companies-feedback--error");

  try {
    // apiRequest usa a base http://localhost:8080 e envia Accept: */*.
    const empresas = await apiRequest("/empresas");

    if (!Array.isArray(empresas)) {
      throw new Error("A resposta da API não está no formato esperado.");
    }

    exibirEmpresas(empresas);
  } catch (error) {
    exibirMensagemTabela("Não foi possível carregar as empresas.");
    companiesFeedback.textContent = error.message || "Verifique sua conexão e tente novamente.";
    companiesFeedback.classList.add("companies-feedback--error");
  }
}

carregarEmpresas();
