-- As instruções DML (insert, update e delete)
-- aqui serão executadas quando a aplicação iniciar
-- PODEM haver quantas forem necessárias.
-- apenas separe elas com ponto e vírgula.

-- MAAAAS, para que funcione, é necessária a seguinte configuração no application.properties:
-- spring.jpa.defer-datasource-initialization=true

INSERT INTO usuario (nome, email, telefone, senha, tipo) VALUES
('João Silva', 'joao.silva@email.com', '11987654321', 'senha123', 1),
('Maria Oliveira', 'maria.oliveira@outroemail.com', '21998765432', 'minhasenha', 0),
('Carlos Pereira', 'carlos.pereira@maisum.net', '31976543210', 'seguranca456', 2),
('Ana Souza', 'ana.souza@testmail.org', '41965432109', 'aninha123', 1),
('Pedro Rodrigues', 'pedro.rodrigues@final.com.br', '51954321098', 'pedrinho789', 0);

INSERT INTO pedido (id, dt_pedido, dt_entrega, status_pagamento, preco_total, is_retirada) VALUES
(0, '2020-10-02T18:13:00', '2020-10-15T15:10:00', 1, 300.00, TRUE),
(1, '2025-04-01T10:00:00', '2025-04-11T16:20:00', 2, 450.00, FALSE),
(2, '2025-03-16T12:00:00', '2025-03-21T10:15:00', 0, 350.00, FALSE);