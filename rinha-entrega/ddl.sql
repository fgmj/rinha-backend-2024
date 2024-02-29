--DDL
create UNLOGGED table cliente (
  id bigserial not null,
  limite bigint,
  saldo bigint,
  primary key (id)
);

create UNLOGGED table transacao (
  tipo char(1) not null,
  valor numeric(10, 0) not null,
  cliente_id bigint not null,
  data_lancamento timestamp(6),
  id bigserial not null,
  descricao varchar(10) not null,
  primary key (id)
);

alter table
add
  constraint FKk44khwkynm4lmqa4ewiaaprdb foreign key (cliente_id) references cliente;
--ALTER TABLE cliente ADD CONSTRAINT balance_check CHECK (saldo >= -limite);

CREATE INDEX idx_account_transaction_created_at ON transacao (data_lancamento);
--DML
INSERT INTO public.cliente(limite, saldo, id) VALUES (100000, 0, 1);
INSERT INTO public.cliente(limite, saldo, id) VALUES (80000, 0, 2);
INSERT INTO public.cliente(limite, saldo, id) VALUES (1000000, 0, 3);
INSERT INTO public.cliente(limite, saldo, id) VALUES (10000000, 0, 4);
INSERT INTO public.cliente(limite, saldo, id) VALUES (500000, 0, 5);