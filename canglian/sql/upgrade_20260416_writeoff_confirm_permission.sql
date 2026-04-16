-- 核销确认权限增量补丁
insert into sys_menu (
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    query,
    route_name,
    is_frame,
    is_cache,
    menu_type,
    visible,
    status,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
)
select
    2364,
    '核销确认',
    2207,
    5,
    '#',
    '',
    '',
    '',
    1,
    0,
    'F',
    '0',
    '0',
    'business:writeOff:confirm',
    '#',
    'admin',
    sysdate(),
    '',
    null,
    ''
from dual
where not exists (
    select 1
    from sys_menu
    where menu_id = 2364
       or perms = 'business:writeOff:confirm'
);

insert into sys_role_menu (role_id, menu_id)
select 6, 2364
from dual
where exists (
    select 1
    from sys_menu
    where menu_id = 2364
)
and not exists (
    select 1
    from sys_role_menu
    where role_id = 6
      and menu_id = 2364
);

insert into sys_role_menu (role_id, menu_id)
select 2, 2364
from dual
where exists (
    select 1
    from sys_menu
    where menu_id = 2364
)
and not exists (
    select 1
    from sys_role_menu
    where role_id = 2
      and menu_id = 2364
);
