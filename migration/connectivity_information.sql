drop table if exists connectivity_information cascade;
create table connectivity_information (
  rwo_code int,
  column_name text,
  closed_status text
);
grant select on connectivity_information to nis_readonly;
