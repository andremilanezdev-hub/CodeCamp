import { apiRequest } from "./api.js";

const form = document.querySelector("#module-form");
const courseSelect = document.querySelector("#module-course");
const nameInput = document.querySelector("#module-name");
const hoursInput = document.querySelector("#module-hours");
const instructorInput = document.querySelector("#module-instructor-id");
const moduleIdInput = document.querySelector("#module-id");
const saveButton = document.querySelector("#module-save-button");
const message = document.querySelector("#module-form-message");
const pageTitle = document.querySelector("#module-page-title");
const requestedModuleId = new URLSearchParams(window.location.search).get("id");

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

async function carregarCursosDoInstrutor() {
  const cpf = getTokenSubject();
  if (!cpf) {
    setMessage("Sessão não encontrada. Entre novamente.", "error");
    return;
  }

  try {
    const [respostaCursos, respostaUsuarios] = await Promise.all([
      apiRequest("/curso"),
      apiRequest("/usuarios"),
    ]);
    const cursos = Array.isArray(respostaCursos) ? respostaCursos : [];
    const usuarios = Array.isArray(respostaUsuarios) ? respostaUsuarios : [];
    const cpfNormalizado = String(cpf).replace(/\D/g, "");
    const instrutor = usuarios.find((usuario) => String(usuario.cpf ?? "").replace(/\D/g, "") === cpfNormalizado);
    if (!instrutor?.id) throw new Error("Não foi possível identificar o instrutor autenticado.");

    instructorInput.value = instrutor.id;
    const cursosDoInstrutor = cursos.filter((curso) => String(curso.usuarioCadastro?.id) === String(instrutor.id));
    courseSelect.replaceChildren(new Option(
      cursosDoInstrutor.length ? "Selecione um curso" : "Você ainda não possui cursos",
      "",
    ));
    cursosDoInstrutor.forEach((curso) => {
      courseSelect.add(new Option(`${curso.nome} (#${curso.id})`, String(curso.id)));
    });
    courseSelect.disabled = cursosDoInstrutor.length === 0;
    saveButton.disabled = cursosDoInstrutor.length === 0;
    setMessage(cursosDoInstrutor.length
      ? "Apenas cursos cadastrados por você podem receber módulos."
      : "Cadastre um curso antes de criar um módulo.");

    if (requestedModuleId) await carregarModuloParaEdicao();
  } catch (error) {
    courseSelect.replaceChildren(new Option("Falha ao carregar cursos", ""));
    setMessage(error.message || "Não foi possível carregar seus cursos.", "error");
  }
}

async function carregarModuloParaEdicao() {
  try {
    const modulo = await apiRequest(`/modulo/${encodeURIComponent(requestedModuleId)}`);
    const optionDoCurso = [...courseSelect.options].find((option) => option.value === String(modulo.curso_id));
    if (!optionDoCurso) throw new Error("Este módulo não pertence a um curso seu.");
    moduleIdInput.value = modulo.id;
    courseSelect.value = String(modulo.curso_id);
    courseSelect.disabled = true; // A rota PUT atualiza nome e carga; o curso permanece o mesmo.
    nameInput.value = modulo.nome ?? "";
    hoursInput.value = modulo.cargahoraria ?? "";
    pageTitle.textContent = `Editar módulo #${modulo.id}`;
    document.querySelector("#module-form-title").textContent = "Atualizar dados do módulo";
    saveButton.textContent = "Salvar alterações";
    saveButton.disabled = false;
    setMessage(`Curso vinculado: ${modulo.cursoNome || `#${modulo.curso_id}`}.`);
  } catch (error) {
    setMessage(error.message || "Não foi possível carregar o módulo.", "error");
    saveButton.disabled = true;
  }
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!instructorInput.value || !courseSelect.value || !nameInput.value.trim() || Number(hoursInput.value) <= 0) {
    setMessage("Preencha os campos e selecione um curso.", "error");
    return;
  }

  saveButton.disabled = true;
  saveButton.textContent = "Salvando...";
  try {
    const id = moduleIdInput.value;
    const resposta = await apiRequest(id
      ? `/modulo/${encodeURIComponent(id)}`
      : `/curso/${encodeURIComponent(courseSelect.value)}/modulos`, {
      method: id ? "PUT" : "POST",
      body: {
        nome: nameInput.value.trim(),
        cargahoraria: Number(hoursInput.value),
        usuario_id: Number(instructorInput.value),
        curso_id: Number(courseSelect.value),
      },
    });
    setMessage(resposta?.mensagem || (id ? "Módulo atualizado com sucesso." : "Módulo cadastrado com sucesso."), "success");
    if (!id) {
      const instructorId = instructorInput.value;
      const cursoAtual = courseSelect.value;
      form.reset();
      instructorInput.value = instructorId;
      courseSelect.value = cursoAtual;
    }
  } catch (error) {
    setMessage(error.message || "Não foi possível cadastrar o módulo.", "error");
  } finally {
    saveButton.textContent = moduleIdInput.value ? "Salvar alterações" : "Salvar módulo";
    saveButton.disabled = !instructorInput.value || (!moduleIdInput.value && courseSelect.disabled);
  }
});

document.querySelectorAll(".sidebar__link").forEach((link) => {
  if (link.getAttribute("href") === "modulos.html") link.classList.add("active");
});

carregarCursosDoInstrutor();
