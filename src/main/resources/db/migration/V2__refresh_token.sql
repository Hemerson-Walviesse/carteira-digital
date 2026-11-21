CREATE TABLE refresh_token (
                               id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               usuario_id       UUID        NOT NULL REFERENCES usuario(id),
                               token_hash       VARCHAR(64) NOT NULL UNIQUE,
                               expira_em        TIMESTAMPTZ NOT NULL,
                               revogado_em      TIMESTAMPTZ,
                               substituido_por  UUID REFERENCES refresh_token(id),
                               criado_em        TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_refresh_token_usuario ON refresh_token(usuario_id);