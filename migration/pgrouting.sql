drop table if exists pgr_edge;
create table pgr_edge (
        id int,
        source int,
        target int,
        cost float,
        x1 float,
        y1 float,
        x2 float,
        y2 float
);
grant select on pgr_edge to nis_readonly;

insert into pgr_edge
select
0 as id,
source::int4,
target::int4,
ST_Length(geom) as cost,
ST_X(ST_StartPoint(geom)) as x1,
ST_Y(ST_StartPoint(geom)) as y1,
ST_X(ST_EndPoint(geom)) as x2,
ST_Y(ST_EndPoint(geom)) as y2
from neighbor
where geom is not null;
