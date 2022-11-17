create materialized view luft_stat_mv as
  select 
    cat.id as cat_id,
    cat.category_text,
    area.area_id,
    area.name_of_area,
    obj.correctness,
    count(obj.id) as obj_count
  from 
    object obj,
    areas_in_dk area,
    category_join cat_j,
    category cat
  where 
    cat.id  = cat_j.cid
    and cat_j.oid = obj.id
    and cat_j.cid like '%/da/'
    and st_within(obj.point,area.polygon_col)
  group by 
    cat.id,
    cat.category_text,
    area.area_id,
    area.name_of_area,
    obj.correctness
with data;
