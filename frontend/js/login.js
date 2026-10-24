import { apiRequest, salvarToken } from "./api.js";

const loginForm = document.querySelector("#login-form");
const loginButton = document.querySelector("#login-button");
const loginMessage = document.querySelector("#login-message");

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
<<<<<<< HEAD
  const login = String(formData.get("login") ?? "").trim();
  const senha = String(formData.get("senha") ?? "");

  // Verifica os campos antes de enviar qualquer requisição ao backend.
  if (!login || senha.length === 0) {
=======
  const login = formData.get("login");
  const senha = formData.get("senha");

  // Verifica os campos antes de enviar qualquer requisição ao backend.
  if (!login.trim() || senha.length === 0) {
>>>>>>> origin/main
    mostrarMensagem("Preencha o login e a senha.", "warning");
    return;
  }

  loginButton.disabled = true;
  mostrarMensagem("Enviando dados...");

  try {
    // Contrato documentado: POST /login recebe { login, senha }.
    const resposta = await apiRequest("/login", {
      method: "POST",
      body: { login, senha },
      auth: false,
    });

    // A API pode devolver o token diretamente ou dentro da propriedade data.
    const token = resposta?.data?.token ?? resposta?.token;
    if (typeof token !== "string" || token.length === 0) {
      throw new Error("A API não retornou o token de autenticação.");
    }

    // Guarda o JWT antes de abrir páginas que fazem chamadas protegidas.
    salvarToken(token);
    window.location.assign("dashboard.html");
  } catch (error) {
    mostrarMensagem(error.message || "Não foi possível entrar.", "error");
  } finally {
    loginButton.disabled = false;
  }
}

<<<<<<< HEAD
// O formulário trata clique e Enter pelo mesmo evento, evitando enviar duas requisições.
=======
// O clique chama a função diretamente; Enter continua funcionando pelo onsubmit.
loginButton.onclick = fazerLogin;
>>>>>>> origin/main
loginForm.onsubmit = fazerLogin;
