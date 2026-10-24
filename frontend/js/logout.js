import { logout } from "./api.js";

// Limpa a sessão local e retorna ao login ao acionar o botão Sair.
const logoutButton = document.querySelector(".sidebar__logout");
logoutButton?.addEventListener("click", logout);
