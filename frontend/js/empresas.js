import { apiRequest } from "./api.js";

const currentPage = window.location.pathname.split("/").pop();
const navigationLinks = document.querySelectorAll(".sidebar__link");
const companiesTableBody = document.querySelector("#companies-table-body");
const companiesFeedback = document.querySelector("#companies-feedback");
const newCompanyButton = document.querySelector("#new-company-button");

// Abre o formulário dedicado para cadastrar uma empresa.
newCompanyButton?.addEventListener("click", () => {
  window.location.assign("empresa-cadastro.html");
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
  cell.colSpan = 6;
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
      criarCelula(empresa.cnpj),
      // Mantém o nome da propriedade conforme o contrato da API informado.
      criarCelula(empresa.inscricaoEstaual ?? empresa.inscricaoEstadual),
      criarCelula(empresa.quantidadeUsuarios),
    );
    const actionsCell = document.createElement("td");
    const button = document.createElement("button");
    button.type = "button";
    button.className = "company-action";
    button.textContent = "Ver usuários";
    button.addEventListener("click", () => buscarUsuariosDaEmpresa(empresa, button));
    actionsCell.append(button);
    row.append(actionsCell);
    companiesTableBody.append(row);
  });

  companiesFeedback.textContent = `${empresas.length} empresa(s)`;
}

async function buscarUsuariosDaEmpresa(empresa, button) {
  button.disabled = true;
  button.textContent = "Carregando...";
  try {
    const usuarios = await apiRequest(`/empresas/cnpj/${encodeURIComponent(empresa.cnpj)}/usuarios`);
    const nomes = Array.isArray(usuarios)
      ? usuarios.map((usuario) => usuario.nome).filter(Boolean)
      : [];
    window.alert(nomes.length
      ? `Usuários de ${empresa.nomeFantasia || empresa.razaoSocial}:\n${nomes.join("\n")}`
      : "Esta empresa não possui usuários cadastrados.");
  } catch (error) {
    companiesFeedback.textContent = error.message || "Não foi possível consultar os usuários desta empresa.";
    companiesFeedback.classList.add("companies-feedback--error");
  } finally {
    button.disabled = false;
    button.textContent = "Ver usuários";
  }
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
