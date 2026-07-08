package controller;

import dao.CadastroProdutoDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.CadastroProdutoModel;

@WebServlet("/api/produto")
public class GerenciamentoController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String acao = request.getParameter("acao");
        CadastroProdutoDAO dao = new CadastroProdutoDAO();

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        if ("excluir".equals(acao)) {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean sucesso = dao.excluir(id);
            response.getWriter().write(sucesso ? "ok" : "erro");
            return;
        }

        if ("atualizar".equals(acao)) {
            CadastroProdutoModel produto = new CadastroProdutoModel();

            produto.setId(Integer.parseInt(request.getParameter("id")));
            produto.setCodigoBarras(request.getParameter("codigoBarras"));
            produto.setNomeProduto(request.getParameter("nomeProduto"));
            produto.setFabricante(request.getParameter("fabricante"));
            produto.setMarca(request.getParameter("marca"));
            produto.setDataFabricacao(request.getParameter("dataFabricacao"));
            produto.setDataVencimento(request.getParameter("dataVencimento"));
            produto.setQuantidade(Long.parseLong(request.getParameter("quantidade")));
            produto.setValor(request.getParameter("valor"));
            produto.setTotal(request.getParameter("total"));
            produto.setStatus(request.getParameter("status"));
            produto.setLocalArmazenamento(request.getParameter("localArmazenamento"));

            String estoqueMinimoParam = request.getParameter("estoqueMinimo");
            if (estoqueMinimoParam != null && !estoqueMinimoParam.isBlank()) {
                produto.setEstoqueMinimo(Long.parseLong(estoqueMinimoParam));
            }

            boolean sucesso = dao.atualizar(produto);
            response.getWriter().write(sucesso ? "ok" : "erro");
            return;
        }

        if ("excluirProduto".equals(acao)) {
            String codigoBarras = request.getParameter("codigoBarras");
            boolean sucesso = dao.excluirPorCodigoBarras(codigoBarras);
            response.getWriter().write(sucesso ? "ok" : "erro");
            return;
        }

        if ("atualizarProduto".equals(acao)) {
            String codigoBarras = request.getParameter("codigoBarras");
            String nomeProduto = request.getParameter("nomeProduto");
            String fabricante = request.getParameter("fabricante");
            String marca = request.getParameter("marca");
            String localArmazenamento = request.getParameter("localArmazenamento");

            Long estoqueMinimo = null;
            String estoqueMinimoParam = request.getParameter("estoqueMinimo");
            if (estoqueMinimoParam != null && !estoqueMinimoParam.isBlank()) {
                estoqueMinimo = Long.parseLong(estoqueMinimoParam);
            }

            boolean sucesso = dao.atualizarPorCodigoBarras(codigoBarras, nomeProduto, fabricante,
                    marca, localArmazenamento, estoqueMinimo);
            response.getWriter().write(sucesso ? "ok" : "erro");
            return;
        }

        response.getWriter().write("acao invalida");
    }
}
