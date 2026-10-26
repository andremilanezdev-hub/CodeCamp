import { apiRequest, salvarToken } from "./api.js";
import { instalarMascaras, somenteDigitos } from "./mascaras.js";

const loginForm = document.querySelector("#login-form");
const loginButton = document.querySelector("#login-button");
const loginMessage = document.querySelector("#login-message");
const textoBotaoOriginal = loginButton.textContent;
instalarMascaras();

function mostrarMensagem(texto, tipo = "") {
  loginMessage.textContent = texto;
  loginMessage.classList.remove("message-error", "message-warning");

  if (tipo) {
    loginMessage.classList.add(`message-${tipo}`);
  }
}

async function fazerLogin(event) {
  event.preventDefault();

  const formData = new FormData(loginForm);
  const login = somenteDigitos(formData.get("login"));
  const senha = String(formData.get("senha") ?? "");

  if (!login || !senha) {
    mostrarMensagem("Informe seu CPF e sua senha.", "warning");
    return;
  }

  if (login.length !== 11) {
    mostrarMensagem("Informe um CPF válido com 11 dígitos.", "warning");
    return;
  }

  loginButton.disabled = true;
  loginButton.textContent = "Entrando…";
  mostrarMensagem("Validando seus dados...");

  try {
    // LoginController recebe { login, senha } em POST /login.
    const resposta = await apiRequest("/login", {
      method: "POST",
      body: { login, senha },
      auth: false,
    });

    // LoginResponse retorna o token diretamente no corpo da resposta.
    const token = resposta?.token;
    if (typeof token !== "string" || token.trim().length === 0) {
      throw new Error("A API não retornou o token de autenticação.");
    }

    salvarToken(token);
    window.location.assign("dashboard.html");
  } catch (error) {
    if (error.status === 401) {
      mostrarMensagem("CPF ou senha inválidos.", "error");
    } else if (error instanceof TypeError) {
      mostrarMensagem("Não foi possível conectar à API. Verifique se o servidor está ativo.", "error");
    } else {
      mostrarMensagem(error.message || "Não foi possível entrar. Tente novamente.", "error");
    }
  } finally {
    loginButton.disabled = false;
    loginButton.textContent = textoBotaoOriginal;
  }
}

// Um único listener cobre o clique no botão e o envio pelo teclado.
loginForm.addEventListener("submit", fazerLogin);
