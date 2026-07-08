document.getElementById("valor").addEventListener("input", calcular);
document.getElementById("quantidade").addEventListener("input", calcular);

function calcular() {
  let valor = parseFloat(document.getElementById("valor").value) || 0;
  let quantidade = parseInt(document.getElementById("quantidade").value) || 0;

  document.getElementById("total").value = (valor * quantidade).toFixed(2);
}

const hoje = new Date().toISOString().split("T")[0];
document.getElementById("dataVencimento").setAttribute("min", hoje);
document.getElementById("dataFabricacao").setAttribute("max", hoje);

const parametros = new URLSearchParams(window.location.search);
if (parametros.get("erro") === "vencido") {
  alert(
    "Não é possível cadastrar um produto com a data de vencimento no passado.",
  );
}
if (parametros.get("erro") === "fabricacaoFutura") {
  alert(
    "Não é possível cadastrar um produto com a data de fabricação no futuro.",
  );
}

function validarDataVencimento() {
  const campoData = document.getElementById("dataVencimento");
  const mensagemErro = document.getElementById("erroDataVencimento");

  // 1. Pegar a data atual e formatar para "YYYY-MM-DD" (igual ao input do HTML)
  const dataAtual = new Date();
  const ano = dataAtual.getFullYear();
  const mes = String(dataAtual.getMonth() + 1).padStart(2, "0"); // Garante 2 dígitos
  const dia = String(dataAtual.getDate()).padStart(2, "0"); // Garante 2 dígitos
  const hojeFormatado = `${ano}-${mes}-${dia}`;

  // 2. Agora comparamos duas Strings perfeitamente iguais no formato
  if (campoData.value && campoData.value < hojeFormatado) {
    campoData.classList.add("campo-invalido");
    mensagemErro.textContent =
      "A data de vencimento não pode ser anterior a hoje.";
    mensagemErro.classList.remove("oculto");
    return false;
  }

  // Se passou na validação, remove os erros
  campoData.classList.remove("campo-invalido");
  mensagemErro.classList.add("oculto");
  return true;
}

function validarDataFabricacao() {
  const campoData = document.getElementById("dataFabricacao");
  const mensagemErro = document.getElementById("erroDataFabricacao");

  if (campoData.value && campoData.value > hoje) {
    campoData.classList.add("campo-invalido");
    mensagemErro.textContent =
      "A data de fabricação não pode ser posterior a hoje.";
    mensagemErro.classList.remove("oculto");
    return false;
  }

  campoData.classList.remove("campo-invalido");
  mensagemErro.classList.add("oculto");
  return true;
}

document
  .getElementById("dataVencimento")
  .addEventListener("input", validarDataVencimento);
document
  .getElementById("dataVencimento")
  .addEventListener("change", validarDataVencimento);
document
  .getElementById("dataFabricacao")
  .addEventListener("input", validarDataFabricacao);
document
  .getElementById("dataFabricacao")
  .addEventListener("change", validarDataFabricacao);

document
  .getElementById("formCadastro")
  .addEventListener("submit", function (evento) {
    const vencimentoValido = validarDataVencimento();
    const fabricacaoValida = validarDataFabricacao();

    if (!vencimentoValido || !fabricacaoValida) {
      evento.preventDefault();
    }
  });
