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

INSERT INTO produto VALUES (1, true, 30, 180, 0.1442, 'CDI', 'Caixa CDB 1', 'AO_MES', 'CDB', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (2, false, -1, 30, 0.1227, 'IGP_M', 'Caixa POUPANCA 2', 'PERIODO_TOTAL', 'POUPANCA', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (3, false, 360, 180, 0.1019, 'SELIC', 'Caixa FII 3', 'AO_DIA', 'FII', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (4, false, 0, 0, 0.0819, 'IBOVESPA', 'Caixa POUPANCA 4', 'PERIODO_TOTAL', 'POUPANCA', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (5, false, 0, 0, 0.0049, 'SELIC', 'Caixa ACAO 5', 'AO_DIA', 'ACAO', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (6, true, 90, 360, 0.0804, 'NENHUM', 'Caixa LCI 6', 'AO_ANO', 'LCI', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (7, false, 360, 90, 0.1106, 'IBOVESPA', 'Caixa DEBENTURE 7', 'AO_ANO', 'DEBENTURE', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (8, false, -1, 30, 0.1076, 'CDI', 'Caixa POUPANCA 8', 'PERIODO_TOTAL', 'POUPANCA', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (9, false, 180, 360, 0.0944, 'IPCA', 'Caixa CRI 9', 'PERIODO_TOTAL', 'CRI', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (10, false, 90, 360, 0.1048, 'SELIC', 'Caixa FUNDO 10', 'AO_DIA', 'FUNDO', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (11, false, 0, 0, 0.1191, 'SELIC', 'Caixa DEBENTURE 11', 'PERIODO_TOTAL', 'DEBENTURE', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (12, false, 90, 30, 0.0051, 'IPCA', 'Caixa DEBENTURE 12', 'AO_DIA', 'DEBENTURE', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (13, false, 60, 30, 0.114, 'NENHUM', 'Caixa ETF 13', 'AO_MES', 'ETF', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (14, false, 360, 360, 0.0104, 'IBOVESPA', 'Caixa DEBENTURE 14', 'AO_MES', 'DEBENTURE', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (15, false, -1, 60, 0.1099, 'NENHUM', 'Caixa CRI 15', 'AO_MES', 'CRI', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (16, false, 360, 180, 0.0042, 'CDI', 'Caixa TESOURO_DIRETO 16', 'AO_DIA', 'TESOURO_DIRETO', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (17, true, 180, 360, 0.093, 'CDI', 'Caixa CDB 17', 'AO_ANO', 'CDB', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (18, true, 90, 30, 0.1043, 'IPCA', 'Caixa LCI 18', 'PERIODO_TOTAL', 'LCI', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (19, false, 30, 60, 0.1148, 'IBOVESPA', 'Caixa POUPANCA 19', 'AO_ANO', 'POUPANCA', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (20, false, -1, 60, 0.0884, 'IGP_M', 'Caixa POUPANCA 20', 'AO_ANO', 'POUPANCA', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (21, false, 0, 0, 0.0992, 'CDI', 'Caixa CRI 21', 'AO_ANO', 'CRI', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (22, true, 180, 180, 0.0939, 'IPCA', 'Caixa LCI 22', 'AO_ANO', 'LCI', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (23, false, 360, 90, 0.0909, 'IBOVESPA', 'Caixa ACAO 23', 'AO_DIA', 'ACAO', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (24, false, 90, 60, 0.1015, 'IBOVESPA', 'Caixa DEBENTURE 24', 'PERIODO_TOTAL', 'DEBENTURE', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (25, false, 90, 90, 0.1098, 'NENHUM', 'Caixa POUPANCA 25', 'AO_ANO', 'POUPANCA', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (26, false, -1, 30, 0.1147, 'IPCA', 'Caixa FUNDO 26', 'AO_ANO', 'FUNDO', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (27, false, 360, 360, 0.1435, 'NENHUM', 'Caixa FUNDO 27', 'AO_ANO', 'FUNDO', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (28, false, 0, 0, 0.1265, 'SELIC', 'Caixa ETF 28', 'AO_MES', 'ETF', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (29, true, 30, 360, 0.1173, 'IGP_M', 'Caixa LCI 29', 'PERIODO_TOTAL', 'LCI', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (30, true, 60, 180, 0.0853, 'IBOVESPA', 'Caixa LCI 30', 'AO_DIA', 'LCI', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (31, false, 90, 60, 0.0874, 'NENHUM', 'Caixa TESOURO_DIRETO 31', 'AO_MES', 'TESOURO_DIRETO', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (32, true, 0, 0, 0.1152, 'IBOVESPA', 'Caixa LCA 32', 'AO_ANO', 'LCA', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (33, false, 90, 30, 0.0037, 'NENHUM', 'Caixa ETF 33', 'AO_DIA', 'ETF', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (34, false, 360, 180, 0.098, 'NENHUM', 'Caixa TESOURO_DIRETO 34', 'PERIODO_TOTAL', 'TESOURO_DIRETO', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (35, false, 0, 0, 0.0956, 'IPCA', 'Caixa ETF 35', 'AO_DIA', 'ETF', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (36, false, 180, 360, 0.1085, 'NENHUM', 'Caixa ETF 36', 'AO_MES', 'ETF', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (37, false, 30, 90, 0.0192, 'IPCA', 'Caixa FII 37', 'AO_MES', 'FII', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (38, false, -1, 90, 0.0015, 'IGP_M', 'Caixa ETF 38', 'AO_DIA', 'ETF', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (39, true, 360, 180, 0.0967, 'SELIC', 'Caixa LCI 39', 'PERIODO_TOTAL', 'LCI', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (40, false, 0, 0, 0.0832, 'IBOVESPA', 'Caixa FII 40', 'PERIODO_TOTAL', 'FII', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (41, false, 90, 180, 0.0101, 'IPCA', 'Caixa POUPANCA 41', 'AO_MES', 'POUPANCA', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (42, false, 90, 60, 0.0828, 'IPCA', 'Caixa DEBENTURE 42', 'PERIODO_TOTAL', 'DEBENTURE', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (43, false, 0, 0, 0.0865, 'IBOVESPA', 'Caixa POUPANCA 43', 'PERIODO_TOTAL', 'POUPANCA', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (44, false, 30, 360, 0.017, 'SELIC', 'Caixa FII 44', 'AO_MES', 'FII', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (45, false, 60, 60, 0.0896, 'CDI', 'Caixa FII 45', 'PERIODO_TOTAL', 'FII', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (46, true, 0, 0, 0.0176, 'IGP_M', 'Caixa LCA 46', 'AO_MES', 'LCA', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (47, true, -1, 360, 0.0095, 'IBOVESPA', 'Caixa CDB 47', 'AO_DIA', 'CDB', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (48, true, 30, 30, 0.0809, 'SELIC', 'Caixa LCA 48', 'AO_ANO', 'LCA', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (49, false, 60, 180, 0.111, 'IPCA', 'Caixa ETF 49', 'AO_ANO', 'ETF', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (50, false, -1, 30, 0.0065, 'IBOVESPA', 'Caixa FUNDO 50', 'AO_MES', 'FUNDO', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (51, false, -1, 360, 0.0054, 'IPCA', 'Caixa TESOURO_DIRETO 51', 'AO_DIA', 'TESOURO_DIRETO', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (52, false, 0, 0, 0.1379, 'IBOVESPA', 'Caixa ETF 52', 'AO_ANO', 'ETF', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (53, false, 90, 30, 0.1359, 'NENHUM', 'Caixa DEBENTURE 53', 'PERIODO_TOTAL', 'DEBENTURE', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (54, false, 60, 30, 0.1411, 'IBOVESPA', 'Caixa TESOURO_DIRETO 54', 'PERIODO_TOTAL', 'TESOURO_DIRETO', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (55, true, -1, 180, 0.0041, 'NENHUM', 'Caixa CDB 55', 'AO_DIA', 'CDB', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (56, false, 180, 90, 0.0918, 'IBOVESPA', 'Caixa FUNDO 56', 'PERIODO_TOTAL', 'FUNDO', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (57, false, 60, 90, 0.0166, 'NENHUM', 'Caixa FII 57', 'AO_MES', 'FII', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (58, false, 60, 360, 0.086, 'NENHUM', 'Caixa TESOURO_DIRETO 58', 'AO_MES', 'TESOURO_DIRETO', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (59, false, 360, 60, 0.0034, 'NENHUM', 'Caixa FII 59', 'AO_DIA', 'FII', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (60, false, 30, 30, 0.0131, 'IPCA', 'Caixa ACAO 60', 'AO_MES', 'ACAO', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (61, false, 0, 0, 0.0096, 'NENHUM', 'Caixa FII 61', 'AO_DIA', 'FII', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (62, false, 0, 0, 0.112, 'IPCA', 'Caixa DEBENTURE 62', 'AO_ANO', 'DEBENTURE', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (63, false, -1, 30, 0.1378, 'IGP_M', 'Caixa ETF 63', 'PERIODO_TOTAL', 'ETF', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (64, false, 180, 360, 0.1104, 'CDI', 'Caixa FII 64', 'PERIODO_TOTAL', 'FII', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (65, true, 90, 180, 0.0158, 'NENHUM', 'Caixa CDB 65', 'AO_MES', 'CDB', 'POS'); -- RISCO: MEDIO
INSERT INTO produto VALUES (66, false, 0, 0, 0.1043, 'IPCA', 'Caixa FII 66', 'AO_MES', 'FII', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (67, true, 0, 0, 0.1156, 'NENHUM', 'Caixa LCA 67', 'AO_MES', 'LCA', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (68, false, 60, 90, 0.0974, 'IBOVESPA', 'Caixa DEBENTURE 68', 'AO_ANO', 'DEBENTURE', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (69, true, 30, 60, 0.1092, 'IBOVESPA', 'Caixa CDB 69', 'PERIODO_TOTAL', 'CDB', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (70, false, 60, 90, 0.1276, 'IBOVESPA', 'Caixa ETF 70', 'AO_ANO', 'ETF', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (71, true, 60, 360, 0.1315, 'CDI', 'Caixa CDB 71', 'AO_ANO', 'CDB', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (72, false, 180, 360, 0.0007, 'IBOVESPA', 'Caixa TESOURO_DIRETO 72', 'AO_DIA', 'TESOURO_DIRETO', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (73, false, 30, 60, 0.1157, 'IBOVESPA', 'Caixa ETF 73', 'AO_MES', 'ETF', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (74, false, 60, 60, 0.1468, 'IBOVESPA', 'Caixa DEBENTURE 74', 'AO_DIA', 'DEBENTURE', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (75, false, 360, 60, 0.0827, 'IGP_M', 'Caixa ACAO 75', 'AO_ANO', 'ACAO', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (76, true, -1, 180, 0.1215, 'IPCA', 'Caixa LCI 76', 'PERIODO_TOTAL', 'LCI', 'PRE'); -- RISCO: MEDIO
INSERT INTO produto VALUES (77, false, -1, 90, 0.0867, 'SELIC', 'Caixa ETF 77', 'AO_ANO', 'ETF', 'PRE'); -- RISCO: ALTO
INSERT INTO produto VALUES (78, false, 60, 60, 0.0046, 'IBOVESPA', 'Caixa TESOURO_DIRETO 78', 'AO_DIA', 'TESOURO_DIRETO', 'POS'); -- RISCO: ALTO
INSERT INTO produto VALUES (79, true, 30, 30, 0.1062, 'CDI', 'Caixa CDB 79', 'PERIODO_TOTAL', 'CDB', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (80, true, 0, 0, 0.0035, 'NENHUM', 'Caixa LCA 80', 'AO_DIA', 'LCA', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (81, true, 180, 180, 0.1053, 'IGP_M', 'Caixa LCI 81', 'AO_ANO', 'LCI', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (82, true, 90, 90, 0.0909, 'IBOVESPA', 'Caixa CDB 82', 'PERIODO_TOTAL', 'CDB', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (83, true, 0, 0, 0.0082, 'NENHUM', 'Caixa CDB 83', 'AO_DIA', 'CDB', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (84, true, 180, 60, 0.0885, 'IBOVESPA', 'Caixa LCA 84', 'AO_ANO', 'LCA', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (85, true, 0, 0, 0.1245, 'IBOVESPA', 'Caixa LCA 85', 'PERIODO_TOTAL', 'LCA', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (86, true, 90, 360, 0.0809, 'IPCA', 'Caixa CDB 86', 'PERIODO_TOTAL', 'CDB', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (87, true, 30, 90, 0.0072, 'IPCA', 'Caixa LCI 87', 'AO_MES', 'LCI', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (88, true, 30, 30, 0.0089, 'SELIC', 'Caixa LCA 88', 'AO_DIA', 'LCA', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (89, true, 0, 0, 0.1264, 'IPCA', 'Caixa LCI 89', 'AO_ANO', 'LCI', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (90, true, 60, 30, 0.1469, 'SELIC', 'Caixa LCA 90', 'AO_MES', 'LCA', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (91, true, 180, 90, 0.1036, 'IGP_M', 'Caixa CDB 91', 'PERIODO_TOTAL', 'CDB', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (92, true, 0, 0, 0.1382, 'IBOVESPA', 'Caixa CDB 92', 'AO_MES', 'CDB', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (93, true, 0, 0, 0.0142, 'IGP_M', 'Caixa LCI 93', 'AO_MES', 'LCI', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (94, true, 0, 0, 0.0033, 'SELIC', 'Caixa CDB 94', 'AO_DIA', 'CDB', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (95, true, 0, 0, 0.0046, 'SELIC', 'Caixa LCI 95', 'AO_DIA', 'LCI', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (96, true, 0, 0, 0.1197, 'NENHUM', 'Caixa LCA 96', 'PERIODO_TOTAL', 'LCA', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (97, true, 0, 0, 0.0045, 'SELIC', 'Caixa LCA 97', 'AO_DIA', 'LCA', 'POS'); -- RISCO: BAIXO
INSERT INTO produto VALUES (98, true, 60, 360, 0.1074, 'IPCA', 'Caixa CDB 98', 'PERIODO_TOTAL', 'CDB', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (99, true, 60, 60, 0.1143, 'NENHUM', 'Caixa LCI 99', 'PERIODO_TOTAL', 'LCI', 'PRE'); -- RISCO: BAIXO
INSERT INTO produto VALUES (100, false, 180, 30, 0.1045, 'SELIC', 'Caixa ACAO 100', 'AO_ANO', 'ACAO', 'POS'); -- RISCO: ALTO