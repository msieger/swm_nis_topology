drop table if exists definition cascade;
create table definition (
	rwo_code int primary key,
	table_name text
);
grant select on definition to nis_readonly;

insert into definition
select rwo_code, name
from sw_gis_rwo_definition;

