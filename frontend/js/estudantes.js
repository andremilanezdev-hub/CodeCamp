import { apiRequest } from "./api.js";

const currentPage = window.location.pathname.split("/").pop();
const navigationLinks = document.querySelectorAll(".sidebar__link");
const studentsTableBody = document.querySelector("#students-table-body");
const studentsFeedback = document.querySelector("#students-feedback");

// Destaca Estudantes no menu lateral quando esta página está aberta.
navigationLinks.forEach((link) => {
  if (link.getAttribute("href").split("/").pop() === currentPage) {
    link.classList.add("active");
    link.setAttribute("aria-current", "page");
  }
});

function formatarValor(valor) {
  return valor === null || valor === undefined || valor === "" ? "-" : String(valor);
}

function formatarData(data) {
  if (data === null || data === undefined || data === "") return "-";
  const dataValida = new Date(data);
  if (Number.isNaN(dataValida.getTime())) return String(data);
  return dataValida.toLocaleString("pt-BR", { dateStyle: "short", timeStyle: "short" });
}

function criarCelula(valor) {
  const cell = document.createElement("td");
  cell.textContent = formatarValor(valor);
  return cell;
}

function criarCelulaStatus(status) {
  const cell = document.createElement("td");
  const badge = document.createElement("span");
  const statusTexto = formatarValor(status);
  const statusNormalizado = typeof status === "string" ? status.trim().toLowerCase() : "";

  badge.className = "student-status";
  if (statusNormalizado === "ativo" || statusNormalizado === "a") {
    badge.classList.add("student-status--active");
  } else if (statusNormalizado === "inativo" || statusNormalizado === "i") {
    badge.classList.add("student-status--inactive");
  }
  badge.textContent = statusTexto;
  cell.append(badge);
  return cell;
}

function exibirMensagemTabela(mensagem) {
  const row = document.createElement("tr");
  const cell = document.createElement("td");
  cell.className = "students-table__placeholder";
  cell.colSpan = 5;
  cell.textContent = mensagem;
  row.append(cell);
  studentsTableBody.replaceChildren(row);
}

function exibirEstudantes(estudantes) {
  studentsTableBody.replaceChildren();

  if (estudantes.length === 0) {
    exibirMensagemTabela("Nenhum estudante cadastrado.");
    studentsFeedback.textContent = "0 estudantes";
    return;
  }

  estudantes.forEach((estudante) => {
    const student = estudante && typeof estudante === "object" ? estudante : {};
    const row = document.createElement("tr");
    row.append(
      criarCelula(student.nome),
      criarCelula(student.email),
      criarCelula(student.cpf),
      criarCelulaStatus(student.status),
      criarCelula(formatarData(student.dataCadastro)),
    );
    studentsTableBody.append(row);
  });

  studentsFeedback.textContent = `${estudantes.length} estudante(s)`;
}

async function carregarEstudantes() {
  studentsFeedback.textContent = "Carregando estudantes...";
  studentsFeedback.classList.remove("students-feedback--error");

  try {
    // apiRequest consulta http://localhost:8080/estudante com Accept: */*.
    const estudantes = await apiRequest("/estudante");
    if (!Array.isArray(estudantes)) {
      throw new Error("A resposta da API não está no formato esperado.");
    }
    exibirEstudantes(estudantes);
  } catch (error) {
    exibirMensagemTabela("Não foi possível carregar os estudantes.");
    studentsFeedback.textContent = error.message || "Verifique sua conexão e tente novamente.";
    studentsFeedback.classList.add("students-feedback--error");
  }
}

carregarEstudantes();
