-- 在orders表中添加退换货状态字段
ALTER TABLE orders ADD COLUMN return_status CHAR(1) DEFAULT '0' COMMENT '退换货状态：0无 1处理中 2已完成';