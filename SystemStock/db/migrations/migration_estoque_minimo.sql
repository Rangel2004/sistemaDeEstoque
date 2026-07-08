-- Execute este script apenas se o seu banco JÁ existia antes dessa implementação.
-- Se for subir o container do zero (volume novo), não precisa: o init.sql já cria a coluna.

use estoque_db;

ALTER TABLE produtos
    ADD COLUMN estoque_minimo BIGINT AFTER local_armazenamento;
