drop table if exists node cascade;
create table node (
	node_id bigint primary key
);
grant select on node to nis_readonly;

insert into node select distinct node_id[3] from sw_gis_node;