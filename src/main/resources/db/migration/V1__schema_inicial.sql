-- =========================
-- USUÁRIO
-- =========================
CREATE TABLE usuario (
                         id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         nome        VARCHAR(120) NOT NULL,
                         cpf         VARCHAR(11)  NOT NULL UNIQUE,
                         email       VARCHAR(160) NOT NULL UNIQUE,
                         senha_hash  VARCHAR(100) NOT NULL,
                         papel       VARCHAR(20)  NOT NULL DEFAULT 'CLIENTE',
                         criado_em   TIMESTAMPTZ  NOT NULL DEFAULT now(),
                         CONSTRAINT ck_usuario_papel CHECK (papel IN ('CLIENTE', 'ADMIN'))
);

-- =========================
-- CONTA
-- =========================
CREATE TABLE conta (
                       id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       usuario_id  UUID          NOT NULL REFERENCES usuario(id),
                       agencia     VARCHAR(4)    NOT NULL DEFAULT '0001',
                       numero      VARCHAR(10)   NOT NULL UNIQUE,
                       saldo       NUMERIC(19,2) NOT NULL DEFAULT 0,
                       status      VARCHAR(20)   NOT NULL DEFAULT 'ATIVA',
                       version     BIGINT        NOT NULL DEFAULT 0,
                       criado_em   TIMESTAMPTZ   NOT NULL DEFAULT now(),
                       CONSTRAINT ck_conta_saldo  CHECK (saldo >= 0),
                       CONSTRAINT ck_conta_status CHECK (status IN ('ATIVA', 'BLOQUEADA', 'ENCERRADA'))
);
CREATE INDEX idx_conta_usuario ON conta(usuario_id);

-- =========================
-- TRANSAÇÃO
-- =========================
CREATE TABLE transacao (
                           id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           tipo             VARCHAR(20)   NOT NULL,
                           valor            NUMERIC(19,2) NOT NULL,
                           conta_origem_id  UUID REFERENCES conta(id),
                           conta_destino_id UUID REFERENCES conta(id),
                           status           VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE',
                           idempotency_key  VARCHAR(80)   UNIQUE,
                           criado_em        TIMESTAMPTZ   NOT NULL DEFAULT now(),
                           CONSTRAINT ck_transacao_valor  CHECK (valor > 0),
                           CONSTRAINT ck_transacao_tipo   CHECK (tipo IN ('DEPOSITO', 'TRANSFERENCIA', 'PIX')),
                           CONSTRAINT ck_transacao_status CHECK (status IN ('PENDENTE', 'CONCLUIDA', 'FALHOU', 'ESTORNADA')),
                           CONSTRAINT ck_transacao_contas CHECK (conta_origem_id IS DISTINCT FROM conta_destino_id)
    );

-- =========================
-- LANÇAMENTO (o extrato sai daqui)
-- =========================
CREATE TABLE lancamento (
                            id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            transacao_id  UUID          NOT NULL REFERENCES transacao(id),
                            conta_id      UUID          NOT NULL REFERENCES conta(id),
                            tipo          VARCHAR(10)   NOT NULL,
                            valor         NUMERIC(19,2) NOT NULL,
                            saldo_apos    NUMERIC(19,2) NOT NULL,
                            criado_em     TIMESTAMPTZ   NOT NULL DEFAULT now(),
                            CONSTRAINT ck_lancamento_tipo  CHECK (tipo IN ('DEBITO', 'CREDITO')),
                            CONSTRAINT ck_lancamento_valor CHECK (valor > 0)
);
CREATE INDEX idx_lancamento_conta_data ON lancamento(conta_id, criado_em DESC);