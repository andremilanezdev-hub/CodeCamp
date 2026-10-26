import { apiRequest } from "./api.js";
import { formatarCpf, instalarMascaras, somenteDigitos } from "./mascaras.js";

const form = document.querySelector("#user-registration-form");
const nameInput = document.querySelector("#user-name");
const cpfInput = document.querySelector("#user-cpf");
const birthDateInput = document.querySelector("#user-birth-date");
const passwordInput = document.querySelector("#user-password");
const userIdInput = document.querySelector("#user-id");
const companySelect = document.querySelector("#user-company");
const companyHelp = document.querySelector("#company-help");
const message = document.querySelector("#user-form-message");
const saveButton = document.querySelector("#save-user-button");
const pageTitle = document.querySelector("#user-page-title");
const passwordHelp = document.querySelector("#password-help");
const requestedUserId = new URLSearchParams(window.location.search).get("id");
instalarMascaras();

// Destaca Usuários no menu mesmo nesta tela secundária de cadastro.
document.querySelectorAll(".sidebar__link").forEach((link) => {
  if (link.getAttribute("href") === "usuarios.html") {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

function mostrarMensagem(texto, tipo = "") {
  message.textContent = texto;
  message.classList.remove("user-form-message--error", "user-form-message--success");
  if (tipo) message.classList.add(`user-form-message--${tipo}`);
}

function nomeDaEmpresa(empresa) {
  if (!empresa || typeof empresa !== "object") return "";
  return empresa.nomeFantasia || empresa.razaoSocial || `Empresa #${empresa.id}`;
}

async function carregarEmpresas() {
  companySelect.disabled = true;
  companySelect.replaceChildren(new Option("Carregando empresas...", ""));

  try {
    const resposta = await apiRequest("/empresas");
    const empresas = Array.isArray(resposta)
      ? resposta
      : resposta && typeof resposta === "object"
        ? [resposta]
        : null;

    if (!Array.isArray(empresas)) {
      throw new Error("A API retornou uma lista de empresas inválida.");
    }

    companySelect.replaceChildren(new Option("Selecione uma empresa", ""));
    empresas.forEach((empresa) => {
      if (!empresa || empresa.id === null || empresa.id === undefined) return;
      const nome = nomeDaEmpresa(empresa) || `Empresa #${empresa.id}`;
      companySelect.add(new Option(nome, String(empresa.id)));
    });

    if (companySelect.options.length === 1) {
      companySelect.replaceChildren(new Option("Nenhuma empresa disponível", ""));
      companyHelp.textContent = "Cadastre uma empresa antes de adicionar um usuário.";
      saveButton.disabled = true;
      return;
    }

    companySelect.disabled = false;
    saveButton.disabled = false;
    companyHelp.textContent = "";
    if (requestedUserId) await carregarUsuarioParaEdicao();
  } catch (error) {
    companySelect.replaceChildren(new Option("Erro ao carregar empresas", ""));
    companyHelp.textContent = "Não foi possível carregar as empresas. Atualize a página para tentar novamente.";
    mostrarMensagem(error.message || "Falha ao carregar empresas.", "error");
  }
}

async function carregarUsuarioParaEdicao() {
  try {
    // A listagem usa UsuarioConsultaResponse, que fornece os campos editáveis sem devolver a senha.
    const resposta = await apiRequest("/usuarios");
    const usuarios = Array.isArray(resposta) ? resposta : [];
    const usuario = usuarios.find((item) => String(item.id) === String(requestedUserId));
    if (!usuario) throw new Error("Usuário não encontrado.");

    userIdInput.value = usuario.id;
    nameInput.value = usuario.nome ?? "";
    cpfInput.value = formatarCpf(usuario.cpf);
    birthDateInput.value = String(usuario.dataNascimento ?? "").slice(0, 10);
    companySelect.value = String(usuario.empresa_id ?? "");
    pageTitle.textContent = `Editar usuário #${usuario.id}`;
    document.querySelector("#user-form-title").textContent = "Atualizar dados do usuário";
    passwordInput.required = false;
    passwordHelp.textContent = "Deixe vazia para manter a senha atual ou informe uma nova senha.";
    saveButton.textContent = "Salvar alterações";
  } catch (error) {
    mostrarMensagem(error.message || "Não foi possível carregar o usuário.", "error");
    saveButton.disabled = true;
  }
}

function validarFormulario() {
  const campos = [
    [nameInput, "Informe o nome."],
    [cpfInput, "Informe o CPF."],
    [birthDateInput, "Informe a data de nascimento."],
    [companySelect, "Selecione uma empresa."],
  ];

  const campoVazio = campos.find(([campo]) => !campo.value.trim());
  if (campoVazio) {
    mostrarMensagem(campoVazio[1], "error");
    campoVazio[0].focus();
    return false;
  }

  if (!Number.isFinite(Number(companySelect.value))) {
    mostrarMensagem("Selecione uma empresa válida.", "error");
    companySelect.focus();
    return false;
  }

  if (!userIdInput.value && !passwordInput.value.trim()) {
    mostrarMensagem("Informe a senha para o novo usuário.", "error");
    passwordInput.focus();
    return false;
  }

  if (somenteDigitos(cpfInput.value).length !== 11) {
    mostrarMensagem("Informe um CPF com 11 dígitos.", "error");
    cpfInput.focus();
    return false;
  }

  return true;
}

async function cadastrarUsuario(event) {
  event.preventDefault();
  mostrarMensagem();
  if (!validarFormulario()) return;

  saveButton.disabled = true;
  saveButton.textContent = "Salvando...";
  const id = userIdInput.value;
  const dadosUsuario = {
    nome: nameInput.value.trim(),
    cpf: somenteDigitos(cpfInput.value),
    dataNascimento: birthDateInput.value,
    senha: passwordInput.value,
    empresa_id: Number(companySelect.value),
  };

  try {
    const resposta = await apiRequest(id ? `/usuarios/${encodeURIComponent(id)}` : "/usuarios", {
      method: id ? "PUT" : "POST",
      body: dadosUsuario,
    });
    mostrarMensagem(resposta?.mensagem || (id ? "Usuário atualizado com sucesso!" : "Usuário cadastrado com sucesso!"), "success");
    if (!id) form.reset();
  } catch (error) {
    mostrarMensagem(error.message || "Não foi possível cadastrar o usuário.", "error");
  } finally {
    saveButton.textContent = userIdInput.value ? "Salvar alterações" : "Salvar usuário";
    saveButton.disabled = companySelect.disabled || companySelect.options.length <= 1;
  }
}

form.addEventListener("submit", cadastrarUsuario);
carregarEmpresas();
