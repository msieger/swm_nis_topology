drop table if exists rich_connection cascade;
create table rich_connection (
  node_id bigint,
  geom geometry,
  point_idx int,
  rwo_id bigint,
  rwo_code int,
  app_code int,
  point geometry,
  primary key(node_id, point_idx, rwo_id, rwo_code, app_code)
);
grant select on rich_connection to nis_readonly;

insert into rich_connection
  select distinct *,
    case ST_GeometryType(geom)
    when 'ST_LineString' then ST_PointN(geom, point_idx)
    when 'ST_Point' then geom
    else null
    end point
  from (
         select c.node_id, l.geom, c.point_idx, c.rwo_id, c.rwo_code, c.app_code
         from lookup_rwos() l
           join connection c on c.rwo_id = l.rwo_id and c.rwo_code = l.rwo_code and c.app_code = l.app_code
       ) s;
create index on rich_connection(node_id);
