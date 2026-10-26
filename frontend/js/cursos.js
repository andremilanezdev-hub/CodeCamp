import { apiRequest } from "./api.js";

const currentPage = window.location.pathname.split("/").pop();
const navigationLinks = document.querySelectorAll(".sidebar__link");
const coursesTableBody = document.querySelector("#courses-table-body");
const coursesFeedback = document.querySelector("#courses-feedback");
const newCourseButton = document.querySelector("#new-course-button");
let instructorId = null;

newCourseButton?.addEventListener("click", () => {
  window.location.assign("curso-cadastro.html");
});

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
  } else if (normalizado === "inativo" || normalizado === "i" || normalizado === "d") {
    badge.classList.add("course-status--inactive");
    badge.textContent = normalizado === "d" ? "Desativado" : valor;
  }

  if (normalizado !== "d") badge.textContent = valor;
  cell.append(badge);
  return cell;
}

function exibirMensagemTabela(mensagem) {
  const row = document.createElement("tr");
  const cell = document.createElement("td");
  cell.className = "courses-table__placeholder";
  cell.colSpan = 6;
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

    const actionsCell = document.createElement("td");
    const status = String(curso.status ?? "").trim().toUpperCase();
    if (instructorId && String(usuarioCadastro?.id) === String(instructorId)) {
      const actions = document.createElement("div");
      actions.className = "table-actions";
      const editButton = document.createElement("button");
      editButton.type = "button";
      editButton.className = "action-button";
      editButton.textContent = "Editar";
      editButton.addEventListener("click", () => {
        window.location.assign(`curso-cadastro.html?id=${encodeURIComponent(curso.id)}`);
      });
      actions.append(editButton);

      if (status === "D" || status === "I") {
        const activateButton = document.createElement("button");
        activateButton.type = "button";
        activateButton.className = "action-button action-button--secondary";
        activateButton.textContent = "Ativar";
        activateButton.addEventListener("click", () => alterarStatusCurso(curso, "A"));
        actions.append(activateButton);
      } else if (status !== "I") {
        const deactivateButton = document.createElement("button");
        deactivateButton.type = "button";
        deactivateButton.className = "action-button action-button--danger";
        deactivateButton.textContent = "Desativar";
        deactivateButton.addEventListener("click", () => desativarCurso(curso));
        actions.append(deactivateButton);
      }
      actionsCell.append(actions);
    } else {
      actionsCell.textContent = "—";
    }
    row.append(actionsCell);
    coursesTableBody.append(row);
  });

  coursesFeedback.textContent = `${cursos.length} curso(s)`;
}

async function desativarCurso(curso) {
  if (!window.confirm(`Deseja desativar o curso “${curso.nome}”?`)) return;
  try {
    await apiRequest(`/curso/${encodeURIComponent(curso.id)}`, { method: "DELETE" });
    await carregarCursos();
  } catch (error) {
    coursesFeedback.textContent = error.message || "Não foi possível desativar o curso.";
    coursesFeedback.classList.add("courses-feedback--error");
  }
}

async function alterarStatusCurso(curso, status) {
  try {
    await apiRequest(`/curso/${encodeURIComponent(curso.id)}/status`, {
      method: "PATCH",
      body: { status },
    });
    await carregarCursos();
  } catch (error) {
    coursesFeedback.textContent = error.message || "Não foi possível atualizar o status do curso.";
    coursesFeedback.classList.add("courses-feedback--error");
  }
}

async function carregarCursos() {
  coursesFeedback.textContent = "Carregando cursos...";
  coursesFeedback.classList.remove("courses-feedback--error");

  try {
    // Lê o subject para mostrar ações somente nos cursos do instrutor autenticado.
    const token = localStorage.getItem("token");
    const payloadPart = token?.split(".")[1];
    let subject = null;
    if (payloadPart) {
      const normalized = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
      subject = JSON.parse(atob(normalized.padEnd(Math.ceil(normalized.length / 4) * 4, "="))).sub;
    }
    const [resposta, respostaUsuarios] = await Promise.all([
      apiRequest("/curso"),
      apiRequest("/usuarios"),
    ]);
    const usuarios = Array.isArray(respostaUsuarios) ? respostaUsuarios : [];
    const cpf = String(subject ?? "").replace(/\D/g, "");
    instructorId = usuarios.find((usuario) => String(usuario.cpf ?? "").replace(/\D/g, "") === cpf)?.id ?? null;
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
