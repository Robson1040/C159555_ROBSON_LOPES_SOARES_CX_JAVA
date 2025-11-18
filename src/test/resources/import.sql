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

-- Inserindo produtos
INSERT INTO produto (id, fgc, liquidez, minimo_dias_investimento, rentabilidade, indice, nome, periodo_rentabilidade, tipo, tipo_rentabilidade)
VALUES
-- CDB
(1, TRUE, 0, 30, 0.0125, 'CDI', 'CDB Banco Alfa', 'AO_MES', 'CDB', 'POS'),
(2, TRUE, 0, 60, 0.0130, 'SELIC', 'CDB Banco Beta', 'AO_MES', 'CDB', 'POS'),

-- LCI
(3, TRUE, -1, 90, 0.0100, 'CDI', 'LCI Imobiliária Alfa', 'AO_MES', 'LCI', 'POS'),
(4, TRUE, -1, 180, 0.0110, 'CDI', 'LCI Imobiliária Beta', 'AO_MES', 'LCI', 'POS'),

-- LCA
(5, TRUE, -1, 90, 0.0095, 'CDI', 'LCA Agronegócio Alfa', 'AO_MES', 'LCA', 'POS'),
(6, TRUE, -1, 180, 0.0105, 'CDI', 'LCA Agronegócio Beta', 'AO_MES', 'LCA', 'POS'),

-- TESOURO_DIRETO
(7, FALSE, 0, 30, 0.0500, 'IPCA', 'Tesouro IPCA+ 2035', 'AO_ANO', 'TESOURO_DIRETO', 'POS'),
(8, FALSE, 0, 30, 0.0450, 'SELIC', 'Tesouro Selic 2029', 'AO_ANO', 'TESOURO_DIRETO', 'POS'),

-- POUPANCA
(9, TRUE, 0, 0, 0.0060, 'NENHUM', 'Poupança Tradicional', 'AO_MES', 'POUPANCA', 'POS'),
(10, TRUE, 0, 0, 0.0060, 'NENHUM', 'Poupança Digital', 'AO_MES', 'POUPANCA', 'POS'),

-- DEBENTURE
(11, FALSE, -1, 365, 0.0800, 'IGP_M', 'Debênture Alfa', 'AO_ANO', 'DEBENTURE', 'PRE'),
(12, FALSE, -1, 365, 0.0850, 'IGP_M', 'Debênture Beta', 'AO_ANO', 'DEBENTURE', 'PRE'),

-- CRI
(13, FALSE, -1, 720, 0.0900, 'IPCA', 'CRI Imobiliário Alfa', 'AO_ANO', 'CRI', 'POS'),
(14, FALSE, -1, 720, 0.0950, 'IPCA', 'CRI Imobiliário Beta', 'AO_ANO', 'CRI', 'POS'),

-- FUNDO
(15, FALSE, 0, 0, 0.0700, 'IBOVESPA', 'Fundo Multimercado Alfa', 'PERIODO_TOTAL', 'FUNDO', 'POS'),
(16, FALSE, 0, 0, 0.0650, 'IBOVESPA', 'Fundo Multimercado Beta', 'PERIODO_TOTAL', 'FUNDO', 'POS'),

-- FII
(17, FALSE, 0, 0, 0.0800, 'NENHUM', 'FII Imobiliário Alfa', 'AO_MES', 'FII', 'POS'),
(18, FALSE, 0, 0, 0.0750, 'NENHUM', 'FII Imobiliário Beta', 'AO_MES', 'FII', 'POS'),

-- AÇÃO
(19, FALSE, 0, 0, 0.1200, 'IBOVESPA', 'Ação Empresa Alfa', 'PERIODO_TOTAL', 'ACAO', 'PRE'),
(20, FALSE, 0, 0, 0.1500, 'IBOVESPA', 'Ação Empresa Beta', 'PERIODO_TOTAL', 'ACAO', 'PRE'),

-- ETF
(21, FALSE, 0, 0, 0.1000, 'IBOVESPA', 'ETF BOVA11', 'PERIODO_TOTAL', 'ETF', 'POS'),
(22, FALSE, 0, 0, 0.0950, 'IBOVESPA', 'ETF SMALL11', 'PERIODO_TOTAL', 'ETF', 'POS');
