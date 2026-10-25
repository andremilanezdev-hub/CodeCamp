import { apiRequest } from "./api.js";

const form = document.querySelector("#address-form");
const cepInput = document.querySelector("#address-cep");
const searchButton = document.querySelector("#address-search-button");
const feedback = document.querySelector("#address-feedback");
const result = document.querySelector("#address-result");

document.querySelectorAll(".sidebar__link").forEach((link) => {
  if (link.getAttribute("href") === "enderecos.html") link.classList.add("active");
});

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const cep = cepInput.value.replace(/\D/g, "");
  result.hidden = true;
  feedback.classList.remove("error");

  if (cep.length !== 8) {
    feedback.textContent = "Informe um CEP com 8 dígitos.";
    feedback.classList.add("error");
    cepInput.focus();
    return;
  }

  searchButton.disabled = true;
  searchButton.textContent = "Consultando...";
  feedback.textContent = "Buscando endereço...";
  try {
    const address = await apiRequest(`/api/enderecos/${encodeURIComponent(cep)}`);
    if (!address || !address.cep) throw new Error("CEP não encontrado.");
    for (const field of result.querySelectorAll("[data-address]")) {
      field.textContent = address[field.dataset.address] || "—";
    }
    result.hidden = false;
    feedback.textContent = `Endereço localizado para o CEP ${address.cep}.`;
  } catch (error) {
    feedback.textContent = error.message || "Não foi possível consultar este CEP.";
    feedback.classList.add("error");
  } finally {
    searchButton.disabled = false;
    searchButton.textContent = "Consultar";
  }
});
