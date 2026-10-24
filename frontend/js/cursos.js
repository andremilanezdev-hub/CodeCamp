import { apiRequest } from "./api.js";

const currentPage = window.location.pathname.split("/").pop();
const navigationLinks = document.querySelectorAll(".sidebar__link");
const coursesTableBody = document.querySelector("#courses-table-body");
const coursesFeedback = document.querySelector("#courses-feedback");

// Marca Cursos como a página atual no menu lateral.
navigationLinks.forEach((link) => {
  if (link.getAttribute("href").split("/").pop() === currentPage) {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

// Valores ausentes são apresentados como traço; números como zero são preservados.
function formatarValor(valor) {
  return valor === null || valor === undefined || valor === "" ? "-" : String(valor);
}

function criarCelula(valor) {
  const cell = document.createElement("td");
  cell.textContent = formatarValor(valor);
  return cell;
}

function obterAlunos(curso) {
  return Array.isArray(curso.alunos) ? curso.alunos : [];
}

function criarCelulaStatus(status) {
  const cell = document.createElement("td");
  const badge = document.createElement("span");
  const valor = formatarValor(status);
  const normalizado = typeof status === "string" ? status.trim().toLowerCase() : "";

  badge.className = "course-status";
  if (normalizado === "ativo" || normalizado === "a") {
    badge.classList.add("course-status--active");
  } else if (normalizado === "inativo" || normalizado === "i") {
    badge.classList.add("course-status--inactive");
  }

  badge.textContent = valor;
  cell.append(badge);
  return cell;
}

function exibirMensagemTabela(mensagem) {
  const row = document.createElement("tr");
  const cell = document.createElement("td");
  cell.className = "courses-table__placeholder";
  cell.colSpan = 5;
  cell.textContent = mensagem;
  row.append(cell);
  coursesTableBody.replaceChildren(row);
}

function exibirCursos(cursos) {
  coursesTableBody.replaceChildren();

  if (cursos.length === 0) {
    exibirMensagemTabela("Nenhum curso cadastrado.");
    coursesFeedback.textContent = "0 cursos";
    return;
  }

  cursos.forEach((curso) => {
    const row = document.createElement("tr");
    const usuarioCadastro = curso.usuarioCadastro;
    const nomeUsuario = usuarioCadastro && typeof usuarioCadastro === "object"
      ? usuarioCadastro.nome
      : null;
    const alunos = obterAlunos(curso);
    const quantidadeAlunos = alunos.length;
    const nomesAlunos = alunos
      .map((aluno) => aluno && typeof aluno.nome === "string" ? aluno.nome.trim() : "")
      .filter(Boolean);
    const resumoAlunos = nomesAlunos.length > 0
      ? `${quantidadeAlunos} (${nomesAlunos.join(", ")})`
      : quantidadeAlunos;

    row.append(
      criarCelula(curso.nome),
      criarCelula(curso.cargahoraria),
      criarCelulaStatus(curso.status),
      criarCelula(nomeUsuario),
      criarCelula(resumoAlunos),
    );
    coursesTableBody.append(row);
  });

  coursesFeedback.textContent = `${cursos.length} curso(s)`;
}

async function carregarCursos() {
  coursesFeedback.textContent = "Carregando cursos...";
  coursesFeedback.classList.remove("courses-feedback--error");

  try {
    // O endpoint geral informado é GET http://localhost:8080/curso.
    const resposta = await apiRequest("/curso");
    // Aceita tanto a lista de cursos quanto um único objeto retornado pela API.
    const cursos = Array.isArray(resposta)
      ? resposta
      : resposta && typeof resposta === "object"
        ? [resposta]
        : null;

    if (!Array.isArray(cursos)) {
      throw new Error("A resposta da API não está no formato esperado.");
    }

    exibirCursos(cursos);
  } catch (error) {
    exibirMensagemTabela("Não foi possível carregar os cursos.");
    coursesFeedback.textContent = error.message || "Verifique sua conexão e tente novamente.";
    coursesFeedback.classList.add("courses-feedback--error");
  }
}

carregarCursos();
