import { apiRequest } from "./api.js";
import { formatarCnpj, instalarMascaras, somenteDigitos } from "./mascaras.js";

const form = document.querySelector("#company-registration-form");
const legalNameInput = document.querySelector("#company-legal-name");
const tradeNameInput = document.querySelector("#company-trade-name");
const cnpjInput = document.querySelector("#company-cnpj");
const stateRegistrationInput = document.querySelector("#company-state-registration");
const message = document.querySelector("#company-form-message");
const saveButton = document.querySelector("#save-company-button");
const companyIdInput = document.querySelector("#company-id");
const pageTitle = document.querySelector("#company-page-title");
const requestedCompanyId = new URLSearchParams(window.location.search).get("id");
instalarMascaras();

// Destaca Empresas no menu lateral nesta tela de cadastro.
document.querySelectorAll(".sidebar__link").forEach((link) => {
  if (link.getAttribute("href") === "empresas.html") {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

function mostrarMensagem(texto, tipo = "") {
  message.textContent = texto;
  message.classList.remove("company-form-message--error", "company-form-message--success");
  if (tipo) message.classList.add(`company-form-message--${tipo}`);
}

function validarFormulario() {
  const camposObrigatorios = [
    [legalNameInput, "Informe a razão social."],
    [tradeNameInput, "Informe o nome fantasia."],
    [cnpjInput, "Informe o CNPJ."],
    [stateRegistrationInput, "Informe a inscrição estadual."],
  ];

  const campoVazio = camposObrigatorios.find(([campo]) => !campo.value.trim());
  if (campoVazio) {
    mostrarMensagem(campoVazio[1], "error");
    campoVazio[0].focus();
    return false;
  }

  if (somenteDigitos(cnpjInput.value).length !== 14) {
    mostrarMensagem("Informe um CNPJ com 14 dígitos.", "error");
    cnpjInput.focus();
    return false;
  }

  return true;
}

async function carregarEmpresaParaEdicao() {
  try {
    const empresa = await apiRequest(`/empresas/${encodeURIComponent(requestedCompanyId)}`);
    companyIdInput.value = empresa.id;
    legalNameInput.value = empresa.razaoSocial ?? "";
    tradeNameInput.value = empresa.nomeFantasia ?? "";
    cnpjInput.value = formatarCnpj(empresa.cnpj);
    stateRegistrationInput.value = empresa.inscricaoEstaual ?? empresa.inscricaoEstadual ?? "";
    pageTitle.textContent = `Editar empresa #${empresa.id}`;
    document.querySelector("#company-form-title").textContent = "Atualizar dados da empresa";
    document.querySelector(".company-form-card__heading p").textContent = "Edite os dados cadastrais da empresa.";
    saveButton.textContent = "Salvar alterações";
  } catch (error) {
    mostrarMensagem(error.message || "Não foi possível carregar a empresa.", "error");
    saveButton.disabled = true;
  }
}

async function cadastrarEmpresa(event) {
  event.preventDefault();
  mostrarMensagem();
  if (!validarFormulario()) return;

  saveButton.disabled = true;
  saveButton.textContent = "Salvando...";

  const dadosEmpresa = {
    razaoSocial: legalNameInput.value.trim(),
    nomeFantasia: tradeNameInput.value.trim(),
    cnpj: somenteDigitos(cnpjInput.value),
    // A API documenta esta chave com esse nome ("Estaual").
    inscricaoEstaual: stateRegistrationInput.value.trim(),
  };

  try {
    const id = companyIdInput.value;
    const resposta = await apiRequest(id ? `/empresas/${encodeURIComponent(id)}` : "/empresas/criar", {
      method: id ? "PUT" : "POST",
      body: dadosEmpresa,
    });

    mostrarMensagem(resposta?.mensagem || (id ? "Empresa atualizada com sucesso!" : "Empresa cadastrada com sucesso!"), "success");
    if (!id) form.reset();
  } catch (error) {
    mostrarMensagem(error.message || "Não foi possível cadastrar a empresa.", "error");
  } finally {
    saveButton.disabled = false;
    saveButton.textContent = companyIdInput.value ? "Salvar alterações" : "Salvar empresa";
  }
}

form.addEventListener("submit", cadastrarEmpresa);
if (requestedCompanyId) carregarEmpresaParaEdicao();
