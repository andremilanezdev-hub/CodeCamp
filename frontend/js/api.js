// URL base indicada pelo Swagger. Os endpoints documentados não usam o prefixo /api.
export const BASE_URL = "http://localhost:8080";
const TOKEN_KEY = "token";

/** Salva o JWT retornado pela API para as próximas chamadas. */
export function salvarToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

/** Remove o JWT local e encaminha o usuário de volta ao login. */
export function logout() {
  localStorage.removeItem(TOKEN_KEY);
  window.location.assign("index.html");
}

/** Faz uma chamada JSON e devolve o corpo da resposta. */
export async function apiRequest(path, { method = "GET", body, token, auth = true } = {}) {
  const headers = {
    Accept: "*/*",
  };

  // O Content-Type só é necessário quando enviamos um corpo JSON.
  if (body !== undefined) {
    headers["Content-Type"] = "application/json";
  }

  // Usa o token informado na chamada ou, por padrão, o JWT armazenado no navegador.
  // auth:false permite chamadas públicas, como o endpoint de login.
  const jwt = token ?? (auth ? localStorage.getItem(TOKEN_KEY) : null);
  if (jwt) {
    headers.Authorization = `Bearer ${jwt}`;
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  // Respostas 204 não têm conteúdo; as demais devem conter JSON válido.
  const responseText = await response.text();
  let responseData = null;

  if (responseText) {
    try {
      responseData = JSON.parse(responseText);
    } catch {
      throw new Error("A API retornou uma resposta que não é JSON válido.");
    }
  }

  if (!response.ok) {
    const errorMessage = responseData?.mensagem || responseData?.message || responseData?.error;
    const error = new Error(errorMessage || `Erro HTTP ${response.status}`);
    error.status = response.status;
    throw error;
  }

  return responseData;
}
