-- 2026-11-22 二期闭环修复增量脚本
-- 适用范围：退货明细库位/批次字段、收付款资金账户联动字段、坏账确认及盘点/调拨明细权限

drop procedure if exists add_column_if_absent;
delimiter $$
create procedure add_column_if_absent(
    in target_table_name varchar(64),
    in target_column_name varchar(64),
    in target_column_definition text
)
begin
    if not exists (
        select 1
        from information_schema.columns
        where table_schema = database()
          and table_name = target_table_name
          and column_name = target_column_name
    ) then
        set @alter_sql = concat('alter table ', target_table_name, ' add column ', target_column_definition);
        prepare alter_statement from @alter_sql;
        execute alter_statement;
        deallocate prepare alter_statement;
    end if;
end$$
delimiter ;

call add_column_if_absent('wms_purchase_return_item', 'location_id', 'location_id bigint(20) not null default 0 comment ''库位id'' after product_id');
call add_column_if_absent('wms_purchase_return_item', 'batch_no', 'batch_no varchar(64) not null default '''' comment ''批次号'' after location_id');

call add_column_if_absent('wms_sale_return_item', 'location_id', 'location_id bigint(20) not null default 0 comment ''库位id'' after product_id');
call add_column_if_absent('wms_sale_return_item', 'batch_no', 'batch_no varchar(64) not null default '''' comment ''批次号'' after location_id');

call add_column_if_absent('fin_receipt', 'fund_account_id', 'fund_account_id bigint(20) default null comment ''资金账户id'' after pay_channel');
call add_column_if_absent('fin_payment', 'fund_account_id', 'fund_account_id bigint(20) default null comment ''资金账户id'' after pay_channel');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2780, '坏账确认', 2208, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:badDebt:confirm', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2780 or perms = 'business:badDebt:confirm');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2781, '调拨明细列表', 2007, 7, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transferItem:list', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2781 or perms = 'business:transferItem:list');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2782, '调拨明细查询', 2007, 8, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transferItem:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2782 or perms = 'business:transferItem:query');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2783, '调拨明细新增', 2007, 9, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transferItem:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2783 or perms = 'business:transferItem:add');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2784, '调拨明细修改', 2007, 10, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transferItem:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2784 or perms = 'business:transferItem:edit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2785, '调拨明细删除', 2007, 11, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transferItem:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2785 or perms = 'business:transferItem:remove');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2786, '盘点明细列表', 2006, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheckItem:list', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2786 or perms = 'business:inventoryCheckItem:list');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2787, '盘点明细查询', 2006, 7, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheckItem:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2787 or perms = 'business:inventoryCheckItem:query');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2788, '盘点明细新增', 2006, 8, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheckItem:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2788 or perms = 'business:inventoryCheckItem:add');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2789, '盘点明细修改', 2006, 9, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheckItem:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2789 or perms = 'business:inventoryCheckItem:edit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2790, '盘点明细删除', 2006, 10, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheckItem:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2790 or perms = 'business:inventoryCheckItem:remove');

insert ignore into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (2780, 2781, 2782, 2783, 2784, 2785, 2786, 2787, 2788, 2789, 2790)
where r.role_id in (1, 2);

drop procedure if exists add_column_if_absent;
