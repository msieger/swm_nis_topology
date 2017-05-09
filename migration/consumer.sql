drop table if exists consumer_information cascade;
create table consumer_information (
  rwo_code int primary key
);
grant select on consumer_information to nis_readonly;

drop view if exists consuming_node cascade;
create view consuming_node as (
  select distinct node_id
  from connection con
  join consumer_information inf on con.rwo_code = inf.rwo_code
);
grant select on consuming_node to nis_readonly;

