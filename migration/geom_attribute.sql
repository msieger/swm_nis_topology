
drop table if exists geom_attribute cascade;
create table geom_attribute (
	rwo_code int,
	app_code int,
	column_name text,
	primary key (rwo_code, app_code)
);
grant select on geom_attribute to nis_readonly;

insert into geom_attribute
select rwo_code, app_code, name
from sw_gis_rwo_geom_attribute;