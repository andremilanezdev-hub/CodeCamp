import { apiRequest } from "./api.js";

const enrollmentForm = document.querySelector("#enrollment-form");
const instructorIdInput = document.querySelector("#instructor-id");
const courseSelect = document.querySelector("#course-id");
const studentSelect = document.querySelector("#student-id");
const submitButton = document.querySelector("#enrollment-submit");
const feedback = document.querySelector("#enrollment-feedback");
const navigationLinks = document.querySelectorAll(".sidebar__link");
const currentPage = window.location.pathname.split("/").pop();

let instructorId = null;
let courses = [];
let students = [];

// Mantém o indicador visual da página atual consistente com as outras telas do sistema.
navigationLinks.forEach((link) => {
  if (link.getAttribute("href").split("/").pop() === currentPage) {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

function mostrarFeedback(message, type = "") {
  feedback.textContent = message;
  feedback.classList.remove("enrollment-feedback--success", "enrollment-feedback--error");
  if (type) feedback.classList.add(`enrollment-feedback--${type}`);
}

function normalizarCpf(cpf) {
  return String(cpf ?? "").replace(/\D/g, "");
}

/**
 * Lê o subject do JWT apenas para localizar o ID do instrutor na lista de usuários.
 * A validação de confiança e de propriedade continua sendo feita pelo backend.
 */
function obterCpfDoToken() {
  const token = localStorage.getItem("token");
  if (!token) return null;

  try {
    const partePayload = token.split(".")[1];
    if (!partePayload) return null;
    const base64 = partePayload.replace(/-/g, "+").replace(/_/g, "/");
    const preenchido = base64.padEnd(Math.ceil(base64.length / 4) * 4, "=");
    const payload = JSON.parse(atob(preenchido));
    return typeof payload.sub === "string" ? payload.sub : null;
  } catch {
    return null;
  }
}

function definirOpcaoInicial(select, label) {
  const option = document.createElement("option");
  option.value = "";
  option.textContent = label;
  select.replaceChildren(option);
}

function obterLista(resposta, nomeRecurso) {
  const lista = Array.isArray(resposta)
    ? resposta
    : resposta && typeof resposta === "object"
      ? [resposta]
      : null;
  if (!lista) throw new Error(`A API não retornou uma lista válida de ${nomeRecurso}.`);
  return lista;
}

function carregarOpcoesDeCursos() {
  const cursosDoInstrutor = courses.filter((course) =>
    String(course.usuarioCadastro?.id) === String(instructorId));

  definirOpcaoInicial(courseSelect, cursosDoInstrutor.length
    ? "Selecione um curso"
    : "Nenhum curso cadastrado por você");

  cursosDoInstrutor.forEach((course) => {
    const option = document.createElement("option");
    option.value = course.id;
    option.textContent = `${course.nome || `Curso #${course.id}`} — ${course.cargahoraria ?? "carga horária não informada"}h`;
    courseSelect.append(option);
  });

  courseSelect.disabled = cursosDoInstrutor.length === 0;
}

function carregarOpcoesDeEstudantes() {
  const course = courses.find((item) => String(item.id) === courseSelect.value);
  const alunosMatriculados = new Set((course?.alunos ?? []).map((aluno) => String(aluno.id)));
  const disponiveis = students.filter((student) => !alunosMatriculados.has(String(student.id)));

  definirOpcaoInicial(studentSelect, !course
    ? "Selecione primeiro um curso"
    : disponiveis.length
      ? "Selecione um estudante"
      : "Todos os estudantes já estão matriculados");

  disponiveis.forEach((student) => {
    const option = document.createElement("option");
    option.value = student.id;
    option.textContent = `${student.nome || `Estudante #${student.id}`} (ID ${student.id})`;
    studentSelect.append(option);
  });

  studentSelect.disabled = !course || disponiveis.length === 0;
  atualizarBotao();
}

function atualizarBotao() {
  submitButton.disabled = !instructorId || !courseSelect.value || !studentSelect.value;
}

async function carregarDados() {
  const cpfToken = obterCpfDoToken();
  if (!cpfToken) {
    mostrarFeedback("Sua sessão não foi encontrada. Entre novamente.", "error");
    return;
  }

  mostrarFeedback("Carregando cursos e estudantes...");
  try {
    // Aproveita os endpoints e o helper de autenticação já usados nas demais páginas.
    const [respostaCursos, respostaEstudantes, respostaUsuarios] = await Promise.all([
      apiRequest("/curso"),
      apiRequest("/estudante"),
      apiRequest("/usuarios"),
    ]);

    courses = obterLista(respostaCursos, "cursos");
    students = obterLista(respostaEstudantes, "estudantes");
    const usuarios = obterLista(respostaUsuarios, "usuários");
    const instrutor = usuarios.find((user) =>
      normalizarCpf(user.cpf) === normalizarCpf(cpfToken));

    if (!instrutor?.id) {
      throw new Error("Não foi possível identificar o instrutor da sessão.");
    }

    instructorId = instrutor.id;
    instructorIdInput.value = instructorId;
    carregarOpcoesDeCursos();
    carregarOpcoesDeEstudantes();
    mostrarFeedback(courses.some((course) => String(course.usuarioCadastro?.id) === String(instructorId))
      ? "Selecione um curso e um estudante para continuar."
      : "Você ainda não possui cursos cadastrados.");
  } catch (error) {
    courseSelect.disabled = true;
    studentSelect.disabled = true;
    mostrarFeedback(error.message || "Não foi possível carregar os dados necessários.", "error");
  }
}

async function realizarMatricula(event) {
  event.preventDefault();
  if (!instructorId || !courseSelect.value || !studentSelect.value) {
    mostrarFeedback("Selecione um curso e um estudante.", "error");
    return;
  }

  submitButton.disabled = true;
  submitButton.textContent = "Salvando matrícula...";
  mostrarFeedback("Enviando matrícula...");

  try {
    // Os nomes JSON seguem exatamente MatriculaRequest no backend.
    const resposta = await apiRequest("/curso/matricula", {
      method: "POST",
      body: {
        usuario_id: Number(instructorId),
        estudante_id: Number(studentSelect.value),
        curso_id: Number(courseSelect.value),
      },
    });

    const cursoMatriculado = courses.find((course) => String(course.id) === courseSelect.value);
    const estudanteMatriculado = students.find((student) => String(student.id) === studentSelect.value);
    if (cursoMatriculado && estudanteMatriculado) {
      cursoMatriculado.alunos = [...(cursoMatriculado.alunos ?? []), estudanteMatriculado];
    }

    mostrarFeedback(resposta?.mensagem || "Matrícula realizada com sucesso.", "success");
    carregarOpcoesDeEstudantes();
  } catch (error) {
    mostrarFeedback(error.message || "Não foi possível concluir a matrícula.", "error");
  } finally {
    submitButton.textContent = "Confirmar matrícula";
    atualizarBotao();
  }
}

courseSelect.addEventListener("change", carregarOpcoesDeEstudantes);
studentSelect.addEventListener("change", atualizarBotao);
enrollmentForm.addEventListener("submit", realizarMatricula);

carregarDados();
