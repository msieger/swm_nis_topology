
create or replace function get_rwo(rwo_id bigint, rwo_code int, app_code int)
  returns table(tbl text, col text, system_id bigint, geom public.geometry) AS $$
declare
  def record;
  attr record;
  res record;
  system_id bigint;
  geom public.geometry;
  schem text;
begin
  schem := current_schema();
  execute 'select * from sw_gis_rwo_definition where rwo_code = $1'
  into def
  using rwo_code;
  execute 'select * from sw_gis_rwo_geom_attribute where rwo_code = $1 and app_code = $2'
  into attr
  using rwo_code, app_code;
  system_id := null;
  if (select exists(select 1 from information_schema.columns where table_schema = schem and table_name = def.name and column_name = 'system_id')) then
    execute 'select system_id from ' || def.name || ' where rwo_id[3] = $1'
    into system_id
    using rwo_id;
  end if;
  geom := null;
  if (select exists(select 1 from information_schema.columns where table_schema = schem and table_name = def.name and column_name = attr.name)) then
    execute 'select ' || attr.name || ' from ' || def.name || ' where rwo_id[3] = $1'
    into geom
    using rwo_id;
  end if;
  return query execute 'select cast($1 as text), cast($2 as text), $3 as system_id, $4 as geom'
  using def.name, attr.name, system_id, geom;
end;
$$ language plpgsql;
grant execute on function get_rwo(bigint, int, int) to nis_readonly;

create or replace function lookup_rwos()
  returns table(point_idx int, rwo_id bigint, rwo_code int, app_code int, node_id bigint, geom public.geometry) as $$
declare
  schem text;
  tbl text;
  col text;
  rec record;
begin
  schem := current_schema();
  for rec in select c.rwo_code, c.app_code from connection c group by c.rwo_code, c.app_code
  loop
    tbl := (select table_name from definition def where def.rwo_code = rec.rwo_code);
    col := (select column_name from geom_attribute attr where attr.rwo_code = rec.rwo_code and attr.app_code = rec.app_code);
    if not exists(select 1 from information_schema.columns where table_schema = schem and table_name = tbl and column_name = col) then
      return query execute 'select *, null::public.geometry from connection c'
      ' where c.rwo_code = $1 and c.app_code = $2'
      using rec.rwo_code, rec.app_code;
    else
      return query execute 'select c.*, ' || col || ' from connection c join ' || tbl || ' on c.rwo_id = ' || tbl || '.rwo_id[3] ' ||
                           'where c.rwo_code = $1 and c.app_code = $2'
      using rec.rwo_code, rec.app_code;
    end if;
  end loop;
end;
$$ language plpgsql;
grant execute on function lookup_rwos() to nis_readonly;

create or replace function
  is_closable(p_node_id bigint)
  returns boolean as $$
begin
  return (exists(
      select 1
      from connection con
        join connectivity_information coninf on con.rwo_code = coninf.rwo_code
      where con.node_id = p_node_id
  ));
end;
$$ language 'plpgsql';
grant execute on function is_closable(bigint) to nis_readonly;

create or replace function
  is_closed(p_node_id bigint)
  returns boolean as $$
declare
  closed boolean;
  rwo record;
  tbl_name text;
  col_name text;
  closed_status_name text;
begin
  for rwo in (select *
              from connection con
                join connectivity_information coninf on con.rwo_code = coninf.rwo_code
              where con.node_id = p_node_id)
  loop
    tbl_name := (
      select name
      from sw_gis_rwo_definition
      where rwo_code = rwo.rwo_code);
    col_name := (
      select column_name
      from connectivity_information
      where rwo_code = rwo.rwo_code);
    closed_status_name := (
      select closed_status
      from connectivity_information
      where rwo_code = rwo.rwo_code);
    execute 'select 1
		from ' || tbl_name || '
		where rwo_id[3] = $1 and ' || col_name || ' = ' || quote_literal(closed_status_name)
    into closed
    using rwo.rwo_id;
    if closed then
      return true;
    end if;
  end loop;
  return false;
end;
$$ language 'plpgsql';
grant execute on function is_closed(bigint) to nis_readonly;

create or replace function path_by_index(geom geometry, start_idx int, end_idx int)
  returns geometry as $$
declare
  start_loc float;
  end_loc float;
  tmp float;
begin
  start_loc := (select ST_LineLocatePoint(geom, ST_PointN(geom, start_idx)));
  end_loc := (select ST_LineLocatePoint(geom, ST_PointN(geom, end_idx)));
  if start_loc > end_loc then
    tmp := start_loc;
    start_loc := end_loc;
    end_loc := tmp;
  end if;
  return (select ST_LineSubstring(geom, start_loc, end_loc));
end;
$$ language 'plpgsql';
grant execute on function path_by_index(geometry, int, int) to nis_readonly;

drop view if exists neighbor cascade;
create view neighbor as (
  select
    c1.node_id source,
    c1.point_idx source_idx,
    c2.node_id target,
    c2.point_idx target_idx,
    c1.rwo_id,
    c1.rwo_code,
    c1.app_code,
    (
    select path_by_index(geom, c1.point_idx, c2.point_idx)
    from get_geom(c1.rwo_id, c1.rwo_code, c1.app_code) geom) geom
  from connection c1
  join connection c2 on c1.rwo_id = c2.rwo_id and c1.rwo_code = c2.rwo_code and c1.app_code = c2.app_code
  where c2.point_idx = (select max(c.point_idx)
                        from connection c
                        where c.rwo_id = c1.rwo_id
                              and c.rwo_code = c1.rwo_code
                              and c.app_code = c1.app_code
                              and c.point_idx < c1.point_idx)
    or
        c2.point_idx = (select min(c.point_idx)
                        from connection c
                        where c.rwo_id = c1.rwo_id
                              and c.rwo_code = c1.rwo_code
                              and c.app_code = c1.app_code
                              and c.point_idx > c1.point_idx)
);
grant select on neighbor to nis_readonly;

drop view if exists all_neighbor cascade;
create view all_neighbor as (
  select
    c1.node_id source,
    c1.point_idx source_idx,
    c2.node_id target,
    c2.point_idx target_idx,
    c1.rwo_id,
    c1.rwo_code,
    c1.app_code,
    (select path_by_index(rwos.geom, c1.point_idx, c2.point_idx)) geom
  from connection c1
    join connection c2 on c1.rwo_id = c2.rwo_id and c1.rwo_code = c2.rwo_code and c1.app_code = c2.app_code
    join lookup_rwos() rwos on c2.point_idx = rwos.point_idx and c2.rwo_id = rwos.rwo_id and c2.rwo_code = rwos.rwo_code and c2.app_code = rwos.app_code
  where c2.point_idx = (select max(c.point_idx)
                        from connection c
                        where c.rwo_id = c1.rwo_id
                              and c.rwo_code = c1.rwo_code
                              and c.app_code = c1.app_code
                              and c.point_idx < c1.point_idx)
        or
        c2.point_idx = (select min(c.point_idx)
                        from connection c
                        where c.rwo_id = c1.rwo_id
                              and c.rwo_code = c1.rwo_code
                              and c.app_code = c1.app_code
                              and c.point_idx > c1.point_idx)
);
grant select on all_neighbor to nis_readonly;

