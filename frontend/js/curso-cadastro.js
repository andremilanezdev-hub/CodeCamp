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
    return JSON.parse(atob(base64.padEnd(Math.ceil(base64.length / 4) * 4, "="))).sub ?? null;
  } catch {
    return null;
  }
}

async function identificarInstrutor() {
  const cpf = getTokenSubject();
  if (!cpf) {
    setMessage("Sessão não encontrada. Entre novamente.", "error");
    return;
  }
  try {
    const usuarios = await apiRequest("/usuarios");
    const lista = Array.isArray(usuarios) ? usuarios : [];
    const cpfNormalizado = String(cpf).replace(/\D/g, "");
    const instrutor = lista.find((usuario) => String(usuario.cpf ?? "").replace(/\D/g, "") === cpfNormalizado);
    if (!instrutor?.id) throw new Error("Não foi possível identificar o instrutor autenticado.");
    instructorInput.value = instrutor.id;
    if (requestedCourseId) await carregarCursoParaEdicao(instrutor.id);
    else saveButton.disabled = false;
    if (!requestedCourseId) setMessage(`Curso será cadastrado por ${instrutor.nome}.`);
  } catch (error) {
    setMessage(error.message || "Falha ao identificar o instrutor.", "error");
  }
}

async function carregarCursoParaEdicao(idInstrutor) {
  try {
    const curso = await apiRequest(`/curso/${encodeURIComponent(requestedCourseId)}`);
    if (String(curso.usuarioCadastro?.id) !== String(idInstrutor)) {
      throw new Error("Você só pode editar cursos cadastrados por você.");
    }
    courseIdInput.value = curso.id;
    nameInput.value = curso.nome ?? "";
    hoursInput.value = curso.cargahoraria ?? "";
    pageTitle.textContent = `Editar curso #${curso.id}`;
    document.querySelector("#course-form-title").textContent = "Atualizar dados do curso";
    saveButton.textContent = "Salvar alterações";
    saveButton.disabled = false;
    setMessage("Edite os dados do curso e salve as alterações.");
  } catch (error) {
    setMessage(error.message || "Não foi possível carregar o curso.", "error");
  }
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!instructorInput.value || !nameInput.value.trim() || Number(hoursInput.value) <= 0) {
    setMessage("Informe o nome, a carga horária e aguarde a identificação do instrutor.", "error");
    return;
  }

  saveButton.disabled = true;
  saveButton.textContent = "Salvando...";
  try {
    const id = courseIdInput.value;
    const resposta = await apiRequest(id ? `/curso/${encodeURIComponent(id)}` : "/curso", {
      method: id ? "PUT" : "POST",
      body: {
        nome: nameInput.value.trim(),
        cargahoraria: Number(hoursInput.value),
        usuario_id: Number(instructorInput.value),
      },
    });
    setMessage(resposta?.mensagem || (id ? "Curso atualizado com sucesso." : "Curso cadastrado com sucesso."), "success");
    if (!id) {
      const instructorId = instructorInput.value;
      form.reset();
      instructorInput.value = instructorId;
    }
  } catch (error) {
    setMessage(error.message || "Não foi possível cadastrar o curso.", "error");
  } finally {
    saveButton.textContent = courseIdInput.value ? "Salvar alterações" : "Salvar curso";
    saveButton.disabled = !instructorInput.value;
  }
});

document.querySelectorAll(".sidebar__link").forEach((link) => {
  if (link.getAttribute("href") === "cursos.html") link.classList.add("active");
});

identificarInstrutor();
