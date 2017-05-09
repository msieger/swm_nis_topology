
create or replace function get_geom(p_rwo_id bigint, p_rwo_code int, p_app_code int) returns public.geometry as $$
declare
  def text;
  attr text;
  geom public.geometry;
begin
  def := (select table_name from definition where rwo_code = p_rwo_code);
  attr := (select column_name from geom_attribute where rwo_code = p_rwo_code and app_code = p_app_code);
  geom := null;
  execute 'select ' || attr || ' from ' || def || ' where rwo_id[3] = $1'
  into geom
  using p_rwo_id;
  return geom;
  exception when others then
  return null::geometry;
end;
$$ language 'plpgsql';


drop table if exists connection cascade;
create table connection (
  point_idx int,
  rwo_id bigint,
  rwo_code int,
  app_code int,
  node_id bigint references node(node_id),
  primary key (point_idx, rwo_id, rwo_code, app_code, node_id)
);
grant select on connection to nis_readonly;

drop table if exists sector_ncoords cascade;
create temporary table sector_ncoords (
  link_id bigint[3],
  sector_no int,
  ncoords int,
  primary key(link_id, sector_no)
);

insert into sector_ncoords
select link_id, sector_no, ncoords
from sw_gis_link_sector
where type = 8;/*linestring*/

insert into sector_ncoords
select distinct link_id, sector_no,
  abs((select path[1] from ST_DumpPoints(s.geom) where ST_Equals(st_dumppoints.geom, s.end_) order by path[1] desc limit 1) -
  (select path[1] from ST_DumpPoints(s.geom) where ST_Equals(st_dumppoints.geom, s.start_) order by path[1] limit 1))
from (
  select sector.link_id, sector.sector_no,
    ST_SetSRID(ST_MakePoint(sector.arc_coords[1] / 1000.0, sector.arc_coords[2] / 1000.0), 31468) start_,
    ST_SetSRID(ST_MakePoint(sector.arc_coords[5] / 1000.0, sector.arc_coords[6] / 1000.0), 31468) end_,
    get_geom(chain.rwo_id[3], chain.rwo_code, chain.app_code) geom
  from sw_gis_link_sector sector
    join sw_gis_chain_link chain_link on sector.link_id = chain_link.link_id
    join sw_gis_chain chain on chain_link.chain_id = chain.chain_id
  where type in (13, 1037, 3085) /*tangent point arc, circle, centre point arc*/
) s;

drop table if exists link_ncoords;
create temporary table link_ncoords (
  chain_id bigint[3],
  link_id bigint[3],
  seq int,
  reversed boolean,
  ncoords int,
  primary key(chain_id, link_id, seq)
);

insert into link_ncoords
select distinct chain.chain_id, link.link_id, chain_link.seq, chain_link.reversed_ reversed,
  case
  when link_sector.ncoords is null then link.n_coords_or_sectors
  else link_sector.ncoords
  end ncoords
from (
       select link_id, 1 + sum(ncoords) - count(*) ncoords
       from sector_ncoords
       group by link_id
     ) link_sector
  right join sw_gis_link link on link.link_id = link_sector.link_id
  join sw_gis_chain_link chain_link on link.link_id = chain_link.link_id
  join sw_gis_chain chain on chain_link.chain_id = chain.chain_id;

create index on link_ncoords(chain_id);

drop table if exists link_index cascade;
create temporary table link_index (
  chain_id bigint[3],
  link_id bigint[3],
  seq int,
  reversed boolean,
  idx int,
  ncoords int
);
insert into link_index
select lc1.chain_id, lc1.link_id, lc1.seq, lc1.reversed, 1 + sum(lc2.ncoords) - count(*) idx, lc1.ncoords
from link_ncoords lc1
  join link_ncoords lc2 on lc1.chain_id = lc2.chain_id
where lc2.seq < lc1.seq
group by lc1.chain_id, lc1.link_id, lc1.seq, lc1.reversed, lc1.ncoords
union
select lc1.chain_id, lc1.link_id, 1, false, 1, lc1.ncoords
from link_ncoords lc1;
create index on link_index(chain_id);

create or replace temporary view node_link as
select node.node_id, link.link_id, true first_node
from sw_gis_node node
join sw_gis_link link on node.node_id = link.first_node_id
union
select node.node_id, link.link_id, false
from sw_gis_node node
join sw_gis_link link on node.node_id = link.last_node_id;

create or replace temporary view node_rwo as
select node_link.node_id[3],
  coords.idx +
  case
    when ((not node_link.first_node)::int # coords.reversed::int)::boolean then coords.ncoords - 1
    else 0
  end point_index,
chain.rwo_id[3], chain.rwo_code, chain.app_code
from node_link
join sw_gis_chain_link chain_link on node_link.link_id = chain_link.link_id
join sw_gis_chain chain on chain_link.chain_id = chain.chain_id
join link_index coords on chain.chain_id = coords.chain_id
and node_link.link_id = coords.link_id and chain_link.seq = coords.seq
union
select node.node_id[3], 1, point.rwo_id[3], point.rwo_code, point.app_code
from sw_gis_node node
join sw_gis_point point on point.node_id = node.node_id;


insert into connection
select distinct point_index, rwo_id, rwo_code, app_code, node_id
from node_rwo;

create index on connection(node_id);
create index on connection(rwo_id, rwo_code, app_code);

