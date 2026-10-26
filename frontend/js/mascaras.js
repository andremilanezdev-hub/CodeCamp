// Funções compartilhadas para exibir documentos e contatos formatados no navegador.
// Os métodos de envio continuam convertendo esses valores para somente dígitos.
export function somenteDigitos(valor) {
  return String(valor ?? "").replace(/\D/g, "");
}

export function formatarCpf(valor) {
  const digitos = somenteDigitos(valor).slice(0, 11);
  if (digitos.length <= 3) return digitos;
  if (digitos.length <= 6) return `${digitos.slice(0, 3)}.${digitos.slice(3)}`;
  if (digitos.length <= 9) return `${digitos.slice(0, 3)}.${digitos.slice(3, 6)}.${digitos.slice(6)}`;
  return `${digitos.slice(0, 3)}.${digitos.slice(3, 6)}.${digitos.slice(6, 9)}-${digitos.slice(9)}`;
}

export function formatarCnpj(valor) {
  const digitos = somenteDigitos(valor).slice(0, 14);
  if (digitos.length <= 2) return digitos;
  if (digitos.length <= 5) return `${digitos.slice(0, 2)}.${digitos.slice(2)}`;
  if (digitos.length <= 8) return `${digitos.slice(0, 2)}.${digitos.slice(2, 5)}.${digitos.slice(5)}`;
  if (digitos.length <= 12) return `${digitos.slice(0, 2)}.${digitos.slice(2, 5)}.${digitos.slice(5, 8)}/${digitos.slice(8)}`;
  return `${digitos.slice(0, 2)}.${digitos.slice(2, 5)}.${digitos.slice(5, 8)}/${digitos.slice(8, 12)}-${digitos.slice(12)}`;
}

export function formatarCep(valor) {
  const digitos = somenteDigitos(valor).slice(0, 8);
  return digitos.replace(/^(\d{5})(\d)/, "$1-$2");
}

export function formatarTelefone(valor) {
  const digitos = somenteDigitos(valor).slice(0, 11);
  if (digitos.length <= 2) return digitos;

  const ddd = digitos.slice(0, 2);
  const numero = digitos.slice(2);
  const tamanhoPrefixo = numero.length > 8 ? 5 : 4;
  const numeroFormatado = numero.length > tamanhoPrefixo
    ? `${numero.slice(0, tamanhoPrefixo)}-${numero.slice(tamanhoPrefixo)}`
    : numero;
  return `(${ddd}) ${numeroFormatado}`;
}

const formatos = {
  cpf: { formatar: formatarCpf, maxDigitos: 11 },
  cnpj: { formatar: formatarCnpj, maxDigitos: 14 },
  cep: { formatar: formatarCep, maxDigitos: 8 },
  telefone: { formatar: formatarTelefone, maxDigitos: 11 },
};

/** Liga a máscara indicada por data-mascara sem alterar o contrato JSON da API. */
export function instalarMascaras(raiz = document) {
  raiz.querySelectorAll("[data-mascara]").forEach((campo) => {
    const configuracao = formatos[campo.dataset.mascara];
    if (!configuracao) return;

    const formatarCampo = () => {
      const cursorAtual = campo.selectionStart ?? campo.value.length;
      const digitosAntesDoCursor = somenteDigitos(campo.value.slice(0, cursorAtual)).length;
      const digitos = somenteDigitos(campo.value).slice(0, configuracao.maxDigitos);
      campo.value = configuracao.formatar(digitos);

      // Mantém o cursor junto à posição digitada, inclusive quando a máscara insere pontuação.
      if (typeof campo.setSelectionRange === "function") {
        let posicao = 0;
        let contagem = 0;
        while (posicao < campo.value.length && contagem < digitosAntesDoCursor) {
          if (/\d/.test(campo.value[posicao])) contagem += 1;
          posicao += 1;
        }
        campo.setSelectionRange(posicao, posicao);
      }
    };

    campo.addEventListener("input", formatarCampo);
    campo.value = configuracao.formatar(campo.value);
  });
}
