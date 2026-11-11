import { apiRequest } from "./api.js";

const form = document.querySelector("#course-form");
const nameInput = document.querySelector("#course-name");
const hoursInput = document.querySelector("#course-hours");
const instructorInput = document.querySelector("#course-instructor-id");
const courseIdInput = document.querySelector("#course-id");
const saveButton = document.querySelector("#course-save-button");
const message = document.querySelector("#course-form-message");
const pageTitle = document.querySelector("#course-page-title");
const requestedCourseId = new URLSearchParams(window.location.search).get("id");

function setMessage(text, type = "") {
  message.textContent = text;
  message.classList.remove("catalog-form-message--error", "catalog-form-message--success");
  if (type) message.classList.add(`catalog-form-message--${type}`);
}

function getTokenSubject() {
  try {
    const payloadPart = localStorage.getItem("token")?.split(".")[1];
    if (!payloadPart) return null;

    const base64 = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
    const payload = JSON.parse(atob(base64.padEnd(Math.ceil(base64.length / 4) * 4, "=")));
    return payload.sub ?? null;
  } catch {
    return null;
  }
}

function normalizarCpf(cpf) {
  return String(cpf ?? "").replace(/\D/g, "");
}

async function identificarInstrutor() {
  const cpfAutenticado = getTokenSubject();
  if (!cpfAutenticado) {
    setMessage("Sessão não encontrada. Entre novamente.", "error");
    return;
  }

  try {
    // UsuarioController devolve uma lista com id e cpf; o ID é exigido pelo POST /curso.
    const usuarios = await apiRequest("/usuarios");
    const instrutor = (Array.isArray(usuarios) ? usuarios : []).find(
      (usuario) => normalizarCpf(usuario.cpf) === normalizarCpf(cpfAutenticado),
    );

    if (!instrutor?.id) {
      throw new Error("Não foi possível identificar o professor associado à sessão.");
    }

    instructorInput.value = String(instrutor.id);

    if (requestedCourseId) {
      await carregarCursoParaEdicao(instrutor.id);
    } else {
      saveButton.disabled = false;
      setMessage(`Curso será cadastrado por ${instrutor.nome}.`);
    }
  } catch (error) {
    setMessage(error.message || "Falha ao identificar o instrutor.", "error");
    saveButton.disabled = true;
  }
}

async function carregarCursoParaEdicao(idInstrutor) {
  try {
    const curso = await apiRequest(`/curso/${encodeURIComponent(requestedCourseId)}`);
    if (String(curso.usuarioCadastro?.id) !== String(idInstrutor)) {
      throw new Error("Você só pode editar cursos cadastrados por você.");
    }

    courseIdInput.value = String(curso.id);
    nameInput.value = curso.nome ?? "";
    hoursInput.value = curso.cargahoraria ?? "";
    pageTitle.textContent = `Editar curso #${curso.id}`;
    document.querySelector("#course-form-title").textContent = "Atualizar dados do curso";
    saveButton.textContent = "Salvar alterações";
    saveButton.disabled = false;
    setMessage("Edite os dados do curso e salve as alterações.");
  } catch (error) {
    setMessage(error.message || "Não foi possível carregar o curso.", "error");
    saveButton.disabled = true;
  }
}

function obterDadosCurso() {
  const nome = nameInput.value.trim();
  const cargaHoraria = Number(hoursInput.value);
  const usuarioId = Number(instructorInput.value);

  if (!nome) {
    setMessage("Informe o nome do curso.", "error");
    nameInput.focus();
    return null;
  }

  if (!Number.isInteger(cargaHoraria) || cargaHoraria <= 0) {
    setMessage("Informe uma carga horária inteira maior que zero.", "error");
    hoursInput.focus();
    return null;
  }

  if (!Number.isInteger(usuarioId) || usuarioId <= 0) {
    setMessage("Não foi possível identificar o instrutor autenticado. Entre novamente.", "error");
    return null;
  }

  // O cadastro usa estes campos de CursoRequest; o CPF autenticado chega ao backend pelo JWT.
  return {
    nome,
    cargahoraria: cargaHoraria,
    usuario_id: usuarioId,
  };
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const dadosCurso = obterDadosCurso();
  if (!dadosCurso) return;

  saveButton.disabled = true;
  saveButton.textContent = "Salvando...";

  const cursoId = courseIdInput.value;
  try {
    const resposta = await apiRequest(
      cursoId ? `/curso/${encodeURIComponent(cursoId)}` : "/curso",
      {
        method: cursoId ? "PUT" : "POST",
        body: dadosCurso,
      },
    );

    const mensagemSucesso = resposta?.mensagem
      || (cursoId ? "Curso atualizado com sucesso." : "Curso cadastrado com sucesso.");
    setMessage(mensagemSucesso, "success");

    if (!cursoId) {
      form.reset();
      instructorInput.value = String(dadosCurso.usuario_id);
    }
  } catch (error) {
    setMessage(error.message || "Não foi possível salvar o curso.", "error");
  } finally {
    saveButton.textContent = courseIdInput.value ? "Salvar alterações" : "Salvar curso";
    saveButton.disabled = !instructorInput.value;
  }
});

document.querySelectorAll(".sidebar__link").forEach((link) => {
  if (link.getAttribute("href") === "cursos.html") link.classList.add("active");
});

identificarInstrutor();
