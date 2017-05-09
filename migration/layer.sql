drop materialized view if exists topology_layer;
create materialized view topology_layer as
 select node_id,
    count(*),
    public.st_centroid(public.st_collect(public.st_pointn(geom, point_idx))) point
   from lookup_rwos()
  group by node_id;
grant select on topology_layer to nis_readonly;