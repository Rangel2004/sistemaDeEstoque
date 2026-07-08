async function carregarGerenciamento() {
    try {
        const response = await fetch("http://localhost:8080/api/saldo");
        const dados = await response.json();

        const tabela = document.getElementById("corpoTabelaSaldo");
        tabela.innerHTML = "";

        dados.forEach(item => {
            const classeSituacao = item.situacao === "OK" ? "situacao-ok"
                : item.situacao === "REPOR ESTOQUE" ? "situacao-repor"
                : "situacao-critico";

            const linha = `
                    <tr>
                        <td>${item.nomeProduto}</td>
                        <td>${item.codigoBarras}</td>
                        <td>${item.localArmazenamento ?? ""}</td>
                        <td>${item.saldo}</td>
                        <td>${item.estoqueMinimo ?? "-"}</td>
                        <td><span class="badge-situacao ${classeSituacao}">${item.situacao}</span></td>
                        <td>
                            <button type="button" class="btn-editar" data-codigo="${item.codigoBarras}">Editar</button>
                            <button type="button" class="btn-ajustar" data-codigo="${item.codigoBarras}">Ajustar Estoque</button>
                            <button type="button" class="btn-excluir" data-codigo="${item.codigoBarras}">Excluir</button>
                        </td>
                    </tr>
                    `;
            tabela.innerHTML += linha;
        });

        registrarEventosDaTabela(dados);
    } catch (erro) {
        console.log("Erro ao carregar o gerenciamento", erro);
    }
}

function registrarEventosDaTabela(dados) {
    document.querySelectorAll(".btn-editar").forEach(botao => {
        botao.addEventListener("click", () => {
            const produto = dados.find(p => p.codigoBarras === botao.dataset.codigo);
            abrirModalEdicao(produto);
        });
    });

    document.querySelectorAll(".btn-ajustar").forEach(botao => {
        botao.addEventListener("click", () => {
            const produto = dados.find(p => p.codigoBarras === botao.dataset.codigo);
            abrirModalAjuste(produto);
        });
    });

    document.querySelectorAll(".btn-excluir").forEach(botao => {
        botao.addEventListener("click", () => {
            excluirProduto(botao.dataset.codigo);
        });
    });
}

function abrirModalEdicao(produto) {
    document.getElementById("editCodigoBarras").value = produto.codigoBarras;
    document.getElementById("editNomeProduto").value = produto.nomeProduto ?? "";
    document.getElementById("editFabricante").value = produto.fabricante ?? "";
    document.getElementById("editMarca").value = produto.marca ?? "";
    document.getElementById("editLocalArmazenamento").value = produto.localArmazenamento ?? "";
    document.getElementById("editEstoqueMinimo").value = produto.estoqueMinimo ?? "";

    document.getElementById("overlayEdicao").classList.remove("oculto");
}

function fecharModalEdicao() {
    document.getElementById("overlayEdicao").classList.add("oculto");
}

async function excluirProduto(codigoBarras) {
    const confirmar = confirm(
        "Tem certeza que deseja excluir este produto? Isso vai apagar TODO o histórico de entradas e saídas dele. Essa ação não pode ser desfeita."
    );
    if (!confirmar) {
        return;
    }

    try {
        const params = new URLSearchParams();
        params.append("acao", "excluirProduto");
        params.append("codigoBarras", codigoBarras);

        const response = await fetch("http://localhost:8080/api/produto", {
            method: "POST",
            body: params
        });

        const resultado = await response.text();

        if (resultado === "ok") {
            carregarGerenciamento();
        } else {
            alert("Não foi possível excluir o produto.");
        }
    } catch (erro) {
        console.log("Erro ao excluir produto", erro);
    }
}

document.getElementById("formEdicao").addEventListener("submit", async function (evento) {
    evento.preventDefault();

    const formulario = document.getElementById("formEdicao");
    const params = new URLSearchParams(new FormData(formulario));
    params.append("acao", "atualizarProduto");

    try {
        const response = await fetch("http://localhost:8080/api/produto", {
            method: "POST",
            body: params
        });

        const resultado = await response.text();

        if (resultado === "ok") {
            fecharModalEdicao();
            carregarGerenciamento();
        } else {
            alert("Não foi possível salvar as alterações.");
        }
    } catch (erro) {
        console.log("Erro ao atualizar produto", erro);
    }
});

document.getElementById("btnCancelarEdicao").addEventListener("click", fecharModalEdicao);

let produtoEmAjuste = null;

function abrirModalAjuste(produto) {
    produtoEmAjuste = produto;

    document.getElementById("produtoAjusteNome").textContent =
        `${produto.nomeProduto} (código ${produto.codigoBarras}) — saldo atual: ${produto.saldo}`;
    document.getElementById("ajusteTipo").value = "entrada";
    document.getElementById("ajusteQuantidade").value = "";

    document.getElementById("overlayAjuste").classList.remove("oculto");
}

function fecharModalAjuste() {
    document.getElementById("overlayAjuste").classList.add("oculto");
    produtoEmAjuste = null;
}

document.getElementById("formAjuste").addEventListener("submit", async function (evento) {
    evento.preventDefault();

    const tipo = document.getElementById("ajusteTipo").value;
    const quantidade = document.getElementById("ajusteQuantidade").value;
    const valorUnitario = produtoEmAjuste.valor ?? "0";
    const total = (parseFloat(valorUnitario) * parseFloat(quantidade)).toFixed(2);

    const params = new URLSearchParams();
    params.append("codigoBarras", produtoEmAjuste.codigoBarras ?? "");
    params.append("nomeProduto", produtoEmAjuste.nomeProduto ?? "");
    params.append("fabricante", produtoEmAjuste.fabricante ?? "");
    params.append("marca", produtoEmAjuste.marca ?? "");
    params.append("dataFabricacao", produtoEmAjuste.dataFabricacao ?? "");
    params.append("dataVencimento", produtoEmAjuste.dataVencimento ?? "");
    params.append("quantidade", quantidade);
    params.append("valor", valorUnitario);
    params.append("total", total);
    params.append("status", tipo);
    params.append("localArmazenamento", produtoEmAjuste.localArmazenamento ?? "");
    params.append("estoqueMinimo", produtoEmAjuste.estoqueMinimo ?? "");

    try {
        await fetch("http://localhost:8080/cadastroProdutos", {
            method: "POST",
            body: params
        });

        fecharModalAjuste();
        carregarGerenciamento();
    } catch (erro) {
        console.log("Erro ao ajustar estoque", erro);
        alert("Não foi possível registrar o ajuste de estoque.");
    }
});

document.getElementById("btnCancelarAjuste").addEventListener("click", fecharModalAjuste);

window.onload = () => {
    carregarGerenciamento();
};
