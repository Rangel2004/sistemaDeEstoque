-- Execute este script apenas se o seu banco JÁ existia antes dessa implementação
-- (ou seja, o volume do Docker do MySQL já tinha sido criado e o init.sql não vai rodar de novo).
-- Se você for subir o container do zero (volume novo), NÃO precisa rodar este script,
-- pois o init.sql atualizado já cria a coluna.

use estoque_db;

ALTER TABLE produtos
    ADD COLUMN local_armazenamento VARCHAR(100) AFTER status;
