package dao;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CadastroProdutoModel;
import model.SaldoProdutoModel;

public class CadastroProdutoDAO {

    public boolean salvar(CadastroProdutoModel produto) {

        String sql = "INSERT INTO produtos "
                + "(codigo_barras, nome_produto, fabricante, marca, data_fabricacao, data_vencimento, quantidade, valor, total, status, local_armazenamento, estoque_minimo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getCodigoBarras());
            stmt.setString(2, produto.getNomeProduto());
            stmt.setString(3, produto.getFabricante());
            stmt.setString(4, produto.getMarca());
            stmt.setDate(5, java.sql.Date.valueOf(produto.getDataFabricacao()));
            stmt.setDate(6, java.sql.Date.valueOf(produto.getDataVencimento()));
            stmt.setLong(7, produto.getQuantidade());
            stmt.setString(8, produto.getValor());
            stmt.setString(9, produto.getTotal());
            stmt.setString(10, produto.getStatus());
            stmt.setString(11, produto.getLocalArmazenamento());
            stmt.setObject(12, produto.getEstoqueMinimo());

            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CadastroProdutoModel> listarComFiltro(String nome, String tipo, String data) {
        List<CadastroProdutoModel> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM produtos WHERE 1=1");

        if (nome != null && !nome.isEmpty()) {
            sql.append(" AND LOWER (nome_produto) LIKE ?");
        }
        if (tipo != null && !tipo.isEmpty()) {
            sql.append(" AND status = ?");
        }
        if (data != null && !data.isEmpty()) {
            sql.append(" AND data_fabricacao = ?");
        }

        try (Connection conn = ConnectionFactory.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement(sql.toString()))
                {

            int index = 1;

            if (nome != null && !nome.isEmpty()) {
                stmt.setString(index++, "%" + nome.toLowerCase());
            }
            
            if (tipo != null && !tipo.isEmpty()) {
                stmt.setString(index++, tipo);
            }
            
            if (data != null && !data.isEmpty()) {
                stmt.setString(index++, data);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CadastroProdutoModel p = new CadastroProdutoModel();

                p.setId(rs.getInt("id"));
                p.setCodigoBarras(rs.getString("codigo_barras"));
                p.setNomeProduto(rs.getString("nome_produto"));
                p.setFabricante(rs.getString("fabricante"));
                p.setMarca(rs.getString("marca"));
                p.setDataFabricacao(rs.getDate("data_fabricacao").toLocalDate().toString());
                p.setDataVencimento(rs.getDate("data_vencimento").toLocalDate().toString());
                p.setQuantidade(rs.getLong("quantidade"));
                p.setValor(rs.getString("valor"));
                p.setTotal(rs.getString("total"));
                p.setStatus(rs.getString("status"));
                p.setLocalArmazenamento(rs.getString("local_armazenamento"));
                p.setEstoqueMinimo((Long) rs.getObject("estoque_minimo"));

                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean atualizar(CadastroProdutoModel produto) {

        String sql = "UPDATE produtos SET "
                + "codigo_barras = ?, nome_produto = ?, fabricante = ?, marca = ?, "
                + "data_fabricacao = ?, data_vencimento = ?, quantidade = ?, valor = ?, "
                + "total = ?, status = ?, local_armazenamento = ?, estoque_minimo = ? "
                + "WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getCodigoBarras());
            stmt.setString(2, produto.getNomeProduto());
            stmt.setString(3, produto.getFabricante());
            stmt.setString(4, produto.getMarca());
            stmt.setString(5, produto.getDataFabricacao());
            stmt.setString(6, produto.getDataVencimento());
            stmt.setLong(7, produto.getQuantidade());
            stmt.setString(8, produto.getValor());
            stmt.setString(9, produto.getTotal());
            stmt.setString(10, produto.getStatus());
            stmt.setString(11, produto.getLocalArmazenamento());
            stmt.setObject(12, produto.getEstoqueMinimo());
            stmt.setInt(13, produto.getId());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {

        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarPorCodigoBarras(String codigoBarras, String nomeProduto, String fabricante,
            String marca, String localArmazenamento, Long estoqueMinimo) {

        String sql = "UPDATE produtos SET nome_produto = ?, fabricante = ?, marca = ?, "
                + "local_armazenamento = ?, estoque_minimo = ? WHERE codigo_barras = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeProduto);
            stmt.setString(2, fabricante);
            stmt.setString(3, marca);
            stmt.setString(4, localArmazenamento);
            stmt.setObject(5, estoqueMinimo);
            stmt.setString(6, codigoBarras);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluirPorCodigoBarras(String codigoBarras) {

        String sql = "DELETE FROM produtos WHERE codigo_barras = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoBarras);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SaldoProdutoModel> listarSaldoPorProduto() {

        List<SaldoProdutoModel> lista = new ArrayList<>();

        String sql = """
                     SELECT
                        nome_produto,
                        codigo_barras,
                        local_armazenamento,
                        SUM(CASE WHEN status = 'entrada' THEN quantidade ELSE 0 END) AS entrada,
                        SUM(CASE WHEN status = 'saida' THEN quantidade ELSE 0 END) AS saida,
                        MAX(estoque_minimo) AS estoque_minimo,
                        MAX(fabricante) AS fabricante,
                        MAX(marca) AS marca,
                        MAX(valor) AS valor,
                        MAX(data_fabricacao) AS data_fabricacao,
                        MAX(data_vencimento) AS data_vencimento
                     FROM produtos
                     GROUP BY nome_produto, codigo_barras, local_armazenamento
                     ORDER BY nome_produto
                     """;

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SaldoProdutoModel s = new SaldoProdutoModel();

                s.setNomeProduto(rs.getString("nome_produto"));
                s.setCodigoBarras(rs.getString("codigo_barras"));
                s.setLocalArmazenamento(rs.getString("local_armazenamento"));
                s.setEntrada(rs.getLong("entrada"));
                s.setSaida(rs.getLong("saida"));

                long saldo = rs.getLong("entrada") - rs.getLong("saida");
                s.setSaldo(saldo);

                Long estoqueMinimo = (Long) rs.getObject("estoque_minimo");
                s.setEstoqueMinimo(estoqueMinimo);

                s.setFabricante(rs.getString("fabricante"));
                s.setMarca(rs.getString("marca"));
                s.setValor(rs.getString("valor"));
                s.setDataFabricacao(rs.getString("data_fabricacao"));
                s.setDataVencimento(rs.getString("data_vencimento"));

                if (saldo <= 0) {
                    s.setSituacao("EMITIR SOLICITAÇÃO DE COMPRA");
                } else if (estoqueMinimo != null && saldo <= estoqueMinimo) {
                    s.setSituacao("REPOR ESTOQUE");
                } else {
                    s.setSituacao("OK");
                }

                lista.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}
