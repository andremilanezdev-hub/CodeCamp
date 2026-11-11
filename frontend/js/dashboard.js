import { apiRequest } from "./api.js";

const currentPage = window.location.pathname.split("/").pop();
const navigationLinks = document.querySelectorAll(".sidebar__link");
const summaryStatus = document.querySelector("#summary-status");
const courseOverview = document.querySelector("#course-overview");
const finishedCoursesKey = "codecamp.cursosFinalizados";

navigationLinks.forEach((link) => {
  if (link.getAttribute("href").split("/").pop() === currentPage) {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

function obterLista(resposta) {
  if (Array.isArray(resposta)) return resposta;
  if (resposta && typeof resposta === "object") return [resposta];
  return [];
}

function cursosFinalizadosRegistrados() {
  try {
    const registros = JSON.parse(localStorage.getItem(finishedCoursesKey) || "[]");
    return Array.isArray(registros) ? new Set(registros.map(String)) : new Set();
  } catch {
    return new Set();
  }
}

function definirIndicador(id, valor) {
  document.querySelector(id).textContent = new Intl.NumberFormat("pt-BR").format(valor);
}

function exibirCursos(cursos, finalizados) {
  courseOverview.replaceChildren();
  if (cursos.length === 0) {
    const empty = document.createElement("p");
    empty.className = "course-overview__empty";
    empty.textContent = "Nenhum curso cadastrado.";
    courseOverview.append(empty);
    return;
  }

  cursos
    .slice()
    .sort((a, b) => String(a.nome || "").localeCompare(String(b.nome || ""), "pt-BR"))
    .forEach((curso) => {
      const row = document.createElement("article");
      row.className = "course-row";

      const details = document.createElement("div");
      const name = document.createElement("h3");
      name.className = "course-row__name";
      name.textContent = curso.nome || `Curso #${curso.id}`;
      const meta = document.createElement("p");
      meta.className = "course-row__meta";
      const moduleCount = Array.isArray(curso.modulos) ? curso.modulos.length : 0;
      meta.textContent = `${curso.cargahoraria ?? 0}h de carga horária · ${moduleCount} módulo(s)`;
      details.append(name, meta);

      const students = Array.isArray(curso.alunos) ? curso.alunos.length : 0;
      const enrollmentCount = document.createElement("span");
      enrollmentCount.className = "course-row__stat";
      enrollmentCount.textContent = `${students} matrícula(s)`;

      const status = document.createElement("span");
      const active = String(curso.status || "A").toUpperCase() === "A";
      status.className = `course-row__status${active ? "" : " course-row__status--inactive"}`;
      status.textContent = active ? "Ativo" : "Inativo";
      row.append(details, enrollmentCount, status);
      courseOverview.append(row);
    });
}

async function carregarResumo() {
  summaryStatus.textContent = "Carregando indicadores...";
  try {
    const [respostaCursos, respostaEstudantes] = await Promise.all([
      apiRequest("/curso"),
      apiRequest("/estudante"),
    ]);
    const cursos = obterLista(respostaCursos);
    const estudantes = obterLista(respostaEstudantes);
    const matriculas = cursos.reduce((total, curso) =>
      total + (Array.isArray(curso.alunos) ? curso.alunos.length : 0), 0);
    const finalizados = cursosFinalizadosRegistrados();
    const totalFinalizados = cursos.reduce((total, curso) =>
      total + (finalizados.has(String(curso.id)) ? 1 : 0), 0);

    definirIndicador("#total-courses", cursos.length);
    definirIndicador("#total-students", estudantes.length);
    definirIndicador("#total-enrollments", matriculas);
    definirIndicador("#total-finished", totalFinalizados);
    exibirCursos(cursos, finalizados);
    summaryStatus.textContent = "Indicadores atualizados.";
  } catch (error) {
    summaryStatus.textContent = error.message || "Não foi possível carregar os indicadores.";
    summaryStatus.classList.add("summary-status--error");
    courseOverview.replaceChildren();
    const message = document.createElement("p");
    message.className = "course-overview__empty";
    message.textContent = "Não foi possível carregar os cursos. Confira sua conexão e sessão.";
    courseOverview.append(message);
  }
}

carregarResumo();
