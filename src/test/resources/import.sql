drop table if exists investimento;
drop table if exists pessoa;
drop table if exists produto;
drop table if exists simulacao_investimento;
drop table if exists telemetria_metrica;
drop table if exists acesso_log;

create table investimento (
	id integer,
	data date not null,
	fgc boolean not null,
	liquidez integer not null check (liquidez>=-1),
	minimo_dias_investimento integer not null check (minimo_dias_investimento>=0),
	prazo_anos integer,
	prazo_dias integer,
	prazo_meses integer,
	rentabilidade numeric(10,4) not null,
	valor numeric(19,2) not null,
	cliente_id bigint not null,
	produto_id bigint not null,
	indice varchar(255) check (indice in ('SELIC','CDI','IBOVESPA','IPCA','IGP_M','NENHUM')),
	periodo_rentabilidade varchar(255) not null check (periodo_rentabilidade in ('AO_DIA','AO_MES','AO_ANO','PERIODO_TOTAL')),
	tipo varchar(255) not null check (tipo in ('CDB','LCI','LCA','TESOURO_DIRETO','POUPANCA','DEBENTURE','CRI','FUNDO','FII','ACAO','ETF')),
	tipo_rentabilidade varchar(255) not null check (tipo_rentabilidade in ('PRE','POS')),
	primary key (id)
);

create table pessoa (
	id integer,
	role varchar(10) not null,
	cpf varchar(11) not null unique,
	username varchar(50) not null unique,
	nome varchar(100) not null,
	password varchar(255) not null,
	primary key (id)
);


create table produto (
	id integer,
	fgc boolean not null,
	liquidez integer not null check (liquidez>=-1),
	minimo_dias_investimento integer not null check (minimo_dias_investimento>=0),
	rentabilidade numeric(10,4) not null,
	indice varchar(255) check (indice in ('SELIC','CDI','IBOVESPA','IPCA','IGP_M','NENHUM')),
	nome varchar(255) not null,
	periodo_rentabilidade varchar(255) not null check (periodo_rentabilidade in ('AO_DIA','AO_MES','AO_ANO','PERIODO_TOTAL')),
	tipo varchar(255) not null check (tipo in ('CDB','LCI','LCA','TESOURO_DIRETO','POUPANCA','DEBENTURE','CRI','FUNDO','FII','ACAO','ETF')),
	tipo_rentabilidade varchar(255) not null check (tipo_rentabilidade in ('PRE','POS')),
	primary key (id)
);

create table simulacao_investimento (
	id integer,
	prazo_anos integer,
	prazo_dias integer,
	prazo_meses integer,
	rendimento numeric(15,2),
	rentabilidade_efetiva numeric(10,4),
	valor_final numeric(15,2) not null,
	valor_investido numeric(15,2) not null,
	valor_simulado boolean,
	cliente_id bigint not null,
	data_simulacao timestamp not null,
	produto_id bigint not null,
	cenario_simulacao varchar(500),
	produto varchar(255) not null,
	primary key (id)
);

create table telemetria_metrica (
	id integer,
	tempo_medio_resposta float not null,
	tempo_total_execucao float not null,
	contador_execucoes bigint not null,
	data_criacao timestamp not null,
	ultima_atualizacao timestamp not null,
	endpoint varchar(200) not null,
	primary key (id)
);
	
create table acesso_log (
    id integer,
    usuario_id INTEGER,
    endpoint VARCHAR(255) NOT NULL,
    metodo_http VARCHAR(10) NOT NULL,
    uri_completa VARCHAR(500) NOT NULL,
    ip_origem VARCHAR(50),
    corpo_requisicao LONGTEXT,
    status_code INTEGER NOT NULL,
    corpo_resposta LONGTEXT,
    tempo_execucao_ms BIGINT NOT NULL,
    data_acesso DATETIME NOT NULL,
    user_agent VARCHAR(500),
    erro_message VARCHAR(500),
    erro_stacktrace LONGTEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    primary key(id)
);


-- =========================
-- PRODUTOS DE BAIXO RISCO
-- =========================
INSERT INTO produto VALUES (1, TRUE, 30, 60, 6.17, 'NENHUM', 'Poupança Tradicional Caixa', 'AO_ANO', 'POUPANCA', 'POS');
INSERT INTO produto VALUES (2, TRUE, 7, 90, 12.02, 'CDI', 'LCI Banco Caixa', 'AO_ANO', 'LCI', 'POS');
INSERT INTO produto VALUES (3, TRUE, 1, 30, 12.25, 'CDI', 'LCA Banco xpto', 'AO_ANO', 'LCA', 'POS');
INSERT INTO produto VALUES (4, TRUE, 0, 60, 12.91, 'SELIC', 'Tesouro Selic 2031', 'AO_ANO', 'TESOURO_DIRETO', 'POS');
INSERT INTO produto VALUES (5, TRUE, 7, 90, 12.48, 'CDI', 'LCA Banco Santander', 'AO_ANO', 'LCA', 'POS');
INSERT INTO produto VALUES (6, TRUE, 7, 0, 6.12, 'NENHUM', 'Poupança Caixa Tem', 'AO_ANO', 'POUPANCA', 'POS');
INSERT INTO produto VALUES (7, TRUE, 1, 0, 12.92, 'SELIC', 'Tesouro Selic 2033', 'AO_ANO', 'TESOURO_DIRETO', 'POS');
INSERT INTO produto VALUES (8, TRUE, 0, 90, 13.79, 'SELIC', 'Tesouro Prefixado 2029', 'AO_ANO', 'TESOURO_DIRETO', 'POS');
INSERT INTO produto VALUES (9, TRUE, 1, 0, 13.63, 'IPCA', 'Tesouro Selic 2042', 'AO_ANO', 'TESOURO_DIRETO', 'POS');
INSERT INTO produto VALUES (10, TRUE, 1, 90, 6.07, 'NENHUM', 'Poupança Caixa Fácil', 'AO_ANO', 'POUPANCA', 'POS');
INSERT INTO produto VALUES (11, TRUE, 1, 90, 13.59, 'CDI', 'CDB Liquidez Diária', 'AO_ANO', 'CDB', 'POS');
INSERT INTO produto VALUES (12, TRUE, 7, 30, 6.32, 'NENHUM', 'Poupança 2.0 Caixa', 'AO_ANO', 'POUPANCA', 'POS');
INSERT INTO produto VALUES (13, TRUE, 7, 30, 6.17, 'NENHUM', 'Poupança Tradicional ABC', 'AO_ANO', 'POUPANCA', 'POS');
INSERT INTO produto VALUES (14, TRUE, 0, 60, 6.2, 'NENHUM', 'Poupança Tradicional XYZ', 'AO_ANO', 'POUPANCA', 'POS');
INSERT INTO produto VALUES (15, TRUE, 30, 60, 12.89, 'CDI', 'CDB Liquidez Diária', 'AO_ANO', 'CDB', 'POS');
INSERT INTO produto VALUES (16, TRUE, 1, 60, 12.77, 'CDI', 'LCI Banco XC', 'AO_ANO', 'LCI', 'POS');
INSERT INTO produto VALUES (17, TRUE, 30, 30, 12.85, 'CDI', 'LCI Banco XC', 'AO_ANO', 'LCI', 'POS');
INSERT INTO produto VALUES (18, TRUE, 7, 30, 12.73, 'CDI', 'LCI Banco Brasileiro', 'AO_ANO', 'LCI', 'POS');
INSERT INTO produto VALUES (19, TRUE, 30, 0, 13.43, 'CDI', 'CDB Liquidez Diária', 'AO_ANO', 'CDB', 'POS');
INSERT INTO produto VALUES (20, TRUE, 1, 30, 13.52, 'CDI', 'CDB IGP-M', 'AO_ANO', 'CDB', 'POS');

-- =========================
-- PRODUTOS DE MÉDIO RISCO
-- =========================
INSERT INTO produto VALUES (21, TRUE, 120, 30, 10.25, 'IPCA', 'CDB IGP-M', 'AO_ANO', 'CDB', 'POS');
INSERT INTO produto VALUES (22, TRUE, 60, 30, 10.45, 'IPCA', 'Tesouro Prefixado 2042', 'AO_ANO', 'TESOURO_DIRETO', 'POS');
INSERT INTO produto VALUES (23, TRUE, 120, 180, 9.05, 'CDI', 'LCI Banco CAIXA SINGULAR', 'AO_ANO', 'LCI', 'POS');
INSERT INTO produto VALUES (24, TRUE, 120, 30, 11.0, 'CDI', 'LCI Banco CX', 'AO_ANO', 'LCI', 'POS');
INSERT INTO produto VALUES (25, TRUE, 60, 30, 9.37, 'CDI', 'LCI Banco CX', 'AO_ANO', 'LCI', 'POS');
INSERT INTO produto VALUES (26, TRUE, 120, 180, 9.31, 'CDI', 'LCA Banco xpto', 'AO_ANO', 'LCA', 'POS');
INSERT INTO produto VALUES (27, TRUE, 60, 30, 11.48, 'CDI', 'LCA Banco Caixa', 'AO_ANO', 'LCA', 'POS');
INSERT INTO produto VALUES (28, TRUE, 90, 180, 11.13, 'IPCA', 'Tesouro Prefixado 2034', 'AO_ANO', 'TESOURO_DIRETO', 'POS');
INSERT INTO produto VALUES (29, TRUE, 60, 30, 9.98, 'IPCA', 'Tesouro Prefixado 2038', 'AO_ANO', 'TESOURO_DIRETO', 'POS');
INSERT INTO produto VALUES (30, TRUE, 30, 30, 10.29, 'CDI', 'LCI Banco XC', 'AO_ANO', 'LCI', 'POS');
INSERT INTO produto VALUES (31, TRUE, 60, 30, 11.23, 'IPCA', 'CDB IGP-M', 'AO_ANO', 'CDB', 'POS');
INSERT INTO produto VALUES (32, TRUE, 120, 30, 9.41, 'CDI', 'LCI Banco Brasileiro', 'AO_ANO', 'LCI', 'POS');
INSERT INTO produto VALUES (33, TRUE, 60, 30, 10.77, 'SELIC', 'Tesouro Selic 2032', 'AO_ANO', 'TESOURO_DIRETO', 'POS');
INSERT INTO produto VALUES (34, TRUE, 60, 90, 9.88, 'IPCA', 'CDB Liquidez Diária', 'AO_ANO', 'CDB', 'POS');
INSERT INTO produto VALUES (35, TRUE, 120, 180, 11.21, 'CDI', 'LCI Banco BR', 'AO_ANO', 'LCI', 'POS');
INSERT INTO produto VALUES (36, TRUE, 120, 180, 11.3, 'IPCA', 'Tesouro Selic 2031', 'AO_ANO', 'TESOURO_DIRETO', 'POS');
INSERT INTO produto VALUES (37, TRUE, 90, 180, 9.8, 'IPCA', 'CDB IPCA+', 'AO_ANO', 'CDB', 'POS');
INSERT INTO produto VALUES (38, TRUE, 30, 30, 11.18, 'CDI', 'LCA Banco Caixa', 'AO_ANO', 'LCA', 'POS');
INSERT INTO produto VALUES (39, TRUE, 120, 180, 11.34, 'CDI', 'LCA Banco xpto', 'AO_ANO', 'LCA', 'POS');
INSERT INTO produto VALUES (40, TRUE, 30, 180, 9.49, 'IPCA', 'CDB IGP-M', 'AO_ANO', 'CDB', 'POS');

-- =========================
-- PRODUTOS DE ALTO RISCO
-- =========================
INSERT INTO produto VALUES (41, FALSE, 180, 180, 17.88, 'IBOVESPA', 'FII Alpha', 'PERIODO_TOTAL', 'FII', 'PRE');
INSERT INTO produto VALUES (42, FALSE, 180, 720, 13.82, 'IBOVESPA', 'FII Beta', 'PERIODO_TOTAL', 'FII', 'PRE');
INSERT INTO produto VALUES (43, FALSE, 180, 365, 17.96, 'IBOVESPA', 'FII Alpha', 'PERIODO_TOTAL', 'FII', 'PRE');
INSERT INTO produto VALUES (44, FALSE, 365, 365, 15.79, 'IBOVESPA', 'DEBENTURE Vale', 'PERIODO_TOTAL', 'DEBENTURE', 'PRE');
INSERT INTO produto VALUES (45, FALSE, 365, 720, 16.97, 'IBOVESPA', 'ACAO Alpha', 'PERIODO_TOTAL', 'ACAO', 'PRE');
INSERT INTO produto VALUES (46, FALSE, 180, 180, 12.99, 'IBOVESPA', 'FUNDO Beta', 'PERIODO_TOTAL', 'FUNDO', 'PRE');
INSERT INTO produto VALUES (47, FALSE, 365, 180, 14.01, 'IBOVESPA', 'DEBENTURE Alpha', 'PERIODO_TOTAL', 'DEBENTURE', 'PRE');
INSERT INTO produto VALUES (48, FALSE, 180, 365, 15.04, 'IBOVESPA', 'ACAO Petrobras', 'PERIODO_TOTAL', 'ACAO', 'PRE');
INSERT INTO produto VALUES (49, FALSE, 180, 365, 14.17, 'IBOVESPA', 'ACAO Beta', 'PERIODO_TOTAL', 'ACAO', 'PRE');
INSERT INTO produto VALUES (50, FALSE, 365, 180, 15.19, 'IBOVESPA', 'ACAO Alpha', 'PERIODO_TOTAL', 'ACAO', 'PRE');
INSERT INTO produto VALUES (51, FALSE, 180, 720, 15.23, 'IBOVESPA', 'CRI Petrobras', 'PERIODO_TOTAL', 'CRI', 'PRE');
INSERT INTO produto VALUES (52, FALSE, 180, 720, 15.09, 'IBOVESPA', 'FII Petrobras', 'PERIODO_TOTAL', 'FII', 'PRE');
INSERT INTO produto VALUES (53, FALSE, 180, 180, 16.63, 'IBOVESPA', 'FUNDO Petrobras', 'PERIODO_TOTAL', 'FUNDO', 'PRE');
INSERT INTO produto VALUES (54, FALSE, 180, 365, 15.26, 'IBOVESPA', 'ACAO Alpha', 'PERIODO_TOTAL', 'ACAO', 'PRE');
INSERT INTO produto VALUES (55, FALSE, 180, 720, 14.28, 'IBOVESPA', 'ACAO Alpha', 'PERIODO_TOTAL', 'ACAO', 'PRE');
INSERT INTO produto VALUES (56, FALSE, 180, 180, 12.81, 'IBOVESPA', 'DEBENTURE Beta', 'PERIODO_TOTAL', 'DEBENTURE', 'PRE');
INSERT INTO produto VALUES (57, FALSE, 365, 180, 14.51, 'IBOVESPA', 'ACAO Alpha', 'PERIODO_TOTAL', 'ACAO', 'PRE');
INSERT INTO produto VALUES (58, FALSE, 180, 365, 12.36, 'IBOVESPA', 'FUNDO Alpha', 'PERIODO_TOTAL', 'FUNDO', 'PRE');
INSERT INTO produto VALUES (59, FALSE, 180, 180, 15.69, 'IBOVESPA', 'CRI Beta', 'PERIODO_TOTAL', 'CRI', 'PRE');
INSERT INTO produto VALUES (60, FALSE, 365, 180, 14.92, 'IBOVESPA', 'FUNDO Petrobras', 'PERIODO_TOTAL', 'FUNDO', 'PRE');