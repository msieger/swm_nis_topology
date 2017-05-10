drop table if exists provider_information cascade;
create table provider_information (
  rwo_code int primary key
);
grant select on provider_information to nis_readonly;
insert into provider_information
values (2705 /*w_gewinnungsltg_abschnitt*/);

drop view if exists providing_node cascade;
create view providing_node as (
  select distinct node_id
  from connection con
  join provider_information pr on con.rwo_code = pr.rwo_code
);
grant select on providing_node to nis_readonly;