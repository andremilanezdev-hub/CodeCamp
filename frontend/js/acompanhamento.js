import { apiRequest } from "./api.js";

const courseSelect = document.querySelector("#progress-course");
const studentSelect = document.querySelector("#progress-student");
const feedback = document.querySelector("#progress-feedback");
const modulesDescription = document.querySelector("#modules-description");
const modulesList = document.querySelector("#modules-list");
const certificateCard = document.querySelector("#certificate-card");
const certificateCode = document.querySelector("#certificate-code");
const certificateMessage = document.querySelector("#certificate-message");
const navigationLinks = document.querySelectorAll(".sidebar__link");
const currentPage = window.location.pathname.split("/").pop();

let instructorId = null;
let courses = [];
let modules = [];
const completionByStudentCourse = new Map();
const pendingModules = new Set();
const finishedCoursesKey = "codecamp.cursosFinalizados";

function registrarCursoFinalizado(cursoId) {
  try {
    const registros = JSON.parse(localStorage.getItem(finishedCoursesKey) || "[]");
    const cursosFinalizados = new Set(Array.isArray(registros) ? registros.map(String) : []);
    cursosFinalizados.add(String(cursoId));
    localStorage.setItem(finishedCoursesKey, JSON.stringify([...cursosFinalizados]));
  } catch {
    // A conclusão permanece registrada no backend; o dashboard pode não refletir este navegador.
  }
}

navigationLinks.forEach((link) => {
  if (link.getAttribute("href").split("/").pop() === currentPage) {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

function mostrarFeedback(text, type = "") {
  feedback.textContent = text;
  feedback.classList.remove("progress-feedback--success", "progress-feedback--error");
  if (type) feedback.classList.add(`progress-feedback--${type}`);
}

function normalizarCpf(cpf) {
  return String(cpf ?? "").replace(/\D/g, "");
}

function obterCpfDoToken() {
  try {
    const partePayload = localStorage.getItem("token")?.split(".")[1];
    if (!partePayload) return null;

    const base64 = partePayload.replace(/-/g, "+").replace(/_/g, "/");
    const payload = JSON.parse(atob(base64.padEnd(Math.ceil(base64.length / 4) * 4, "=")));
    return typeof payload.sub === "string" ? payload.sub : null;
  } catch {
    return null;
  }
}

function obterLista(resposta, recurso) {
  if (Array.isArray(resposta)) return resposta;
  if (resposta && typeof resposta === "object") return [resposta];
  throw new Error(`A API não retornou uma lista válida de ${recurso}.`);
}

function definirOpcaoInicial(select, texto) {
  const option = document.createElement("option");
  option.value = "";
  option.textContent = texto;
  select.replaceChildren(option);
}

function cursosDoInstrutor() {
  return courses.filter((course) => String(course.usuarioCadastro?.id) === String(instructorId));
}

function carregarCursos() {
  const disponiveis = cursosDoInstrutor();
  definirOpcaoInicial(courseSelect, disponiveis.length ? "Selecione um curso" : "Nenhum curso disponível");

  disponiveis.forEach((course) => {
    const option = document.createElement("option");
    option.value = String(course.id);
    option.textContent = `${course.nome || `Curso #${course.id}`} — ${course.cargahoraria ?? "—"}h`;
    courseSelect.append(option);
  });

  courseSelect.disabled = disponiveis.length === 0;
}

function cursoSelecionado() {
  return courses.find((course) => String(course.id) === courseSelect.value) ?? null;
}

function estudantesDoCurso() {
  const curso = cursoSelecionado();
  return Array.isArray(curso?.alunos) ? curso.alunos : [];
}

function carregarEstudantes() {
  const estudantes = estudantesDoCurso();
  definirOpcaoInicial(studentSelect, !courseSelect.value
    ? "Selecione primeiro um curso"
    : estudantes.length
      ? "Selecione um estudante matriculado"
      : "Nenhum estudante matriculado neste curso");

  estudantes.forEach((student) => {
    const option = document.createElement("option");
    option.value = String(student.id);
    option.textContent = `${student.nome || `Estudante #${student.id}`} (ID ${student.id})`;
    studentSelect.append(option);
  });

  studentSelect.disabled = estudantes.length === 0;
}

function chaveDaMatricula() {
  return `${studentSelect.value}:${courseSelect.value}`;
}

function conclusoesDestaMatricula() {
  const chave = chaveDaMatricula();
  if (!completionByStudentCourse.has(chave)) {
    completionByStudentCourse.set(chave, new Set());
  }
  return completionByStudentCourse.get(chave);
}

function exibirCertificado(response) {
  const code = response.codigo_validacao ?? response.codigoValidacao ?? null;
  if (!code) {
    certificateCard.hidden = true;
    certificateCode.textContent = "";
    return;
  }

  certificateCode.textContent = code;
  certificateMessage.textContent = "Todos os módulos foram concluídos. Guarde este código para validar o certificado.";
  certificateCard.hidden = false;
}

function criarModuloCard(modulo, concluidos) {
  const card = document.createElement("article");
  card.className = "progress-module";

  const details = document.createElement("div");
  details.className = "progress-module__details";

  const title = document.createElement("h3");
  title.textContent = modulo.nome || `Módulo #${modulo.id}`;

  const metadata = document.createElement("p");
  metadata.textContent = `${modulo.cargahoraria ?? 0}h · ${concluidos.has(String(modulo.id)) ? "Conclusão registrada nesta sessão" : "Status anterior não consultado"}`;
  details.append(title, metadata);

  const button = document.createElement("button");
  button.type = "button";
  button.className = "progress-module__button";
  button.dataset.moduleId = String(modulo.id);
  button.textContent = concluidos.has(String(modulo.id)) ? "Conclusão registrada" : "Marcar como concluído";
  button.disabled = concluidos.has(String(modulo.id)) || pendingModules.has(String(modulo.id));
  if (concluidos.has(String(modulo.id))) button.classList.add("progress-module__button--done");

  card.append(details, button);
  return card;
}

function exibirModulos() {
  const curso = cursoSelecionado();
  const estudanteId = studentSelect.value;
  modulesList.replaceChildren();
  certificateCard.hidden = true;

  if (!curso || !estudanteId) {
    modulesDescription.textContent = "Selecione um curso e um estudante para carregar os módulos.";
    modulesList.append(criarMensagemVazia("Aguardando seleção."));
    return;
  }

  modulesDescription.textContent = `${curso.nome} · ${estudantesDoCurso().find((item) => String(item.id) === estudanteId)?.nome ?? "Estudante"}`;
  if (modules.length === 0) {
    modulesList.append(criarMensagemVazia("Este curso ainda não possui módulos."));
    return;
  }

  const concluidos = conclusoesDestaMatricula();
  modules.forEach((modulo) => modulesList.append(criarModuloCard(modulo, concluidos)));
}

function criarMensagemVazia(texto) {
  const message = document.createElement("p");
  message.className = "progress-empty";
  message.textContent = texto;
  return message;
}

async function carregarModulosDoCurso() {
  modules = [];
  exibirModulos();
  const cursoId = courseSelect.value;
  if (!cursoId) return;

  modulesList.replaceChildren(criarMensagemVazia("Carregando módulos..."));
  try {
    const resposta = await apiRequest(`/curso/${encodeURIComponent(cursoId)}/modulos`);
    modules = obterLista(resposta, "módulos");
    exibirModulos();
    mostrarFeedback(modules.length
      ? "Selecione um estudante e registre as conclusões dos módulos."
      : "Este curso ainda não possui módulos.");
  } catch (error) {
    modulesList.replaceChildren(criarMensagemVazia("Não foi possível carregar os módulos."));
    mostrarFeedback(error.message || "Falha ao carregar os módulos do curso.", "error");
  }
}

async function carregarDados() {
  const cpfToken = obterCpfDoToken();
  if (!cpfToken) {
    mostrarFeedback("Sua sessão não foi encontrada. Entre novamente.", "error");
    courseSelect.disabled = true;
    studentSelect.disabled = true;
    return;
  }

  mostrarFeedback("Carregando cursos...");
  try {
    const [respostaCursos, respostaUsuarios] = await Promise.all([
      apiRequest("/curso"),
      apiRequest("/usuarios"),
    ]);
    courses = obterLista(respostaCursos, "cursos");
    const usuarios = obterLista(respostaUsuarios, "professores");
    const instrutor = usuarios.find((user) => normalizarCpf(user.cpf) === normalizarCpf(cpfToken));

    if (!instrutor?.id) throw new Error("Não foi possível identificar o instrutor da sessão.");
    instructorId = instrutor.id;
    carregarCursos();
    mostrarFeedback(cursosDoInstrutor().length
      ? "Selecione um curso para consultar os estudantes matriculados."
      : "Você ainda não possui cursos cadastrados.");
  } catch (error) {
    courseSelect.disabled = true;
    studentSelect.disabled = true;
    mostrarFeedback(error.message || "Não foi possível carregar os dados necessários.", "error");
  }
}

async function registrarConclusao(moduloId, button) {
  const estudanteId = studentSelect.value;
  const cursoId = courseSelect.value;
  if (!estudanteId || !cursoId || !moduloId) {
    mostrarFeedback("Selecione um curso e um estudante antes de registrar a conclusão.", "error");
    return;
  }

  const moduleKey = String(moduloId);
  pendingModules.add(moduleKey);
  button.disabled = true;
  button.textContent = "Registrando...";
  mostrarFeedback("Registrando a conclusão do módulo...");

  try {
    const response = await apiRequest("/estudante/modulos/conclusao", {
      method: "POST",
      body: {
        estudante_id: Number(estudanteId),
        modulo_id: Number(moduloId),
      },
    });

    conclusoesDestaMatricula().add(moduleKey);
    exibirModulos();
    exibirCertificado(response);

    const cursoConcluido = response.curso_concluido ?? response.cursoConcluido ?? false;
    if (cursoConcluido) registrarCursoFinalizado(cursoId);
    mostrarFeedback(response.mensagem || (cursoConcluido
      ? "Curso concluído. O código do certificado foi emitido."
      : "Módulo marcado como concluído."), "success");
  } catch (error) {
    mostrarFeedback(error.message || "Não foi possível registrar a conclusão.", "error");
    button.disabled = false;
    button.textContent = "Marcar como concluído";
  } finally {
    pendingModules.delete(moduleKey);
  }
}

courseSelect.addEventListener("change", async () => {
  carregarEstudantes();
  studentSelect.value = "";
  await carregarModulosDoCurso();
  mostrarFeedback(courseSelect.value
    ? "Selecione um estudante matriculado neste curso."
    : "Selecione um curso para continuar.");
});

studentSelect.addEventListener("change", () => {
  exibirModulos();
  mostrarFeedback(studentSelect.value
    ? "Marque cada módulo quando o estudante concluir o conteúdo."
    : "Selecione um estudante para continuar.");
});

modulesList.addEventListener("click", (event) => {
  const button = event.target.closest("button[data-module-id]");
  if (button) registrarConclusao(button.dataset.moduleId, button);
});

carregarDados();
