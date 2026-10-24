import { apiRequest } from "./api.js";

const form = document.querySelector("#company-registration-form");
const legalNameInput = document.querySelector("#company-legal-name");
const tradeNameInput = document.querySelector("#company-trade-name");
const cnpjInput = document.querySelector("#company-cnpj");
const stateRegistrationInput = document.querySelector("#company-state-registration");
const message = document.querySelector("#company-form-message");
const saveButton = document.querySelector("#save-company-button");

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

  return true;
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
    cnpj: cnpjInput.value.trim(),
    // A API documenta esta chave com esse nome ("Estaual").
    inscricaoEstaual: stateRegistrationInput.value.trim(),
  };

  try {
    const resposta = await apiRequest("/empresas/criar", {
      method: "POST",
      body: dadosEmpresa,
    });

    mostrarMensagem(resposta?.mensagem || "Empresa cadastrada com sucesso!", "success");
    form.reset();
  } catch (error) {
    mostrarMensagem(error.message || "Não foi possível cadastrar a empresa.", "error");
  } finally {
    saveButton.disabled = false;
    saveButton.textContent = "Salvar empresa";
  }
}

form.addEventListener("submit", cadastrarEmpresa);
