drop table if exists investimento;
drop table if exists pessoa;
drop table if exists produto;
drop table if exists simulacao_investimento;
drop table if exists telemetria_metrica;

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