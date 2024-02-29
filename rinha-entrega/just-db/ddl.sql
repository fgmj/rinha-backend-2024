--DDL
--create UNLOGGED table cliente (id bigserial not null, limite bigint, saldo bigint, primary key (id));

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

CREATE UNLOGGED TABLE cliente (
	id INTEGER PRIMARY KEY,
	limite INTEGER NOT NULL,
	saldo INTEGER NOT NULL
);

--create UNLOGGED table transacao (
--tipo char(1) not null,
--valor numeric(10,0) not null,
--cliente_id bigint not null,
--data_lancamento timestamp(6),
--id bigserial not null,
--descricao varchar(10) not null, primary key (id));
CREATE UNLOGGED TABLE transacao (
    id SERIAL PRIMARY KEY,
    tipo char(1) not null,
    valor numeric(10,0) NOT NULL,
    descricao varchar(10) NOT NULL,
    data_lancamento timestamp NOT NULL,
    cliente_id integer NOT NULL
);

CREATE INDEX ix_transacao_idcliente ON transacao(
    cliente_id ASC
);


--alter table if exists transacao add constraint FKk44khwkynm4lmqa4ewiaaprdb foreign key (cliente_id) references cliente;
--ALTER TABLE cliente ADD CONSTRAINT balance_check CHECK (saldo >= -limite);

--DML
INSERT INTO public.cliente(	limite, saldo, id) 	VALUES (100000, 0, 1);
INSERT INTO public.cliente(	limite, saldo, id) 	VALUES (80000, 0, 2);
INSERT INTO public.cliente(	limite, saldo, id) 	VALUES (1000000, 0, 3);
INSERT INTO public.cliente(	limite, saldo, id) 	VALUES (10000000, 0, 4);
INSERT INTO public.cliente(	limite, saldo, id) 	VALUES (500000, 0, 5);