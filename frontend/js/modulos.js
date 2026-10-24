import { apiRequest } from "./api.js";

const currentPage = window.location.pathname.split("/").pop();
const navigationLinks = document.querySelectorAll(".sidebar__link");
const modulesTableBody = document.querySelector("#modules-table-body");
const modulesFeedback = document.querySelector("#modules-feedback");

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
  if (normalizado === "I") return { texto: "Inativo", classe: "module-status--inactive" };
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

function obterNomeEmpresa(empresa) {
  if (typeof empresa === "string") return empresa || "-";
  if (!empresa || typeof empresa !== "object") return "-";
  return empresa.razaoSocial || empresa.nomeFantasia || empresa.nome || "-";
}

function exibirMensagemTabela(texto) {
  const row = document.createElement("tr");
  const cell = document.createElement("td");
  cell.className = "modules-table__placeholder";
  cell.colSpan = 5;
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
      criarCelula(obterNomeEmpresa(modulo.empresa)),
    );
    modulesTableBody.append(row);
  });

  modulesFeedback.textContent = `${modulos.length} módulo(s)`;
}

async function carregarModulos() {
  modulesFeedback.textContent = "Carregando módulos...";
  modulesFeedback.classList.remove("modules-feedback--error");

  try {
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
