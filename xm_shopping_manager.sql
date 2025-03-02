/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : xm_shopping_manager

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 02/03/2025 15:00:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货人',
  `useraddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货地址',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '地址信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, 6, '张三', ' 1', '13531744059');

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色标识',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', 'admin', '管理员', 'http://localhost:9090/files/1697438073596-avatar.png', 'ADMIN', '13677889922', 'admin@xm.com');

-- ----------------------------
-- Table structure for business
-- ----------------------------
DROP TABLE IF EXISTS `business`;
CREATE TABLE `business`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商家姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色标识',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商家介绍',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商家信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of business
-- ----------------------------
INSERT INTO `business` VALUES (2, 'sp', '123', '闪电自行车', 'http://localhost:9090/files/1725555726576-avatar.png', 'BUSINESS', '18877776666', 'meshe@xm.com', '', '审核通过');
INSERT INTO `business` VALUES (3, 'shi', '123', '禧玛诺自行车配件', 'http://localhost:9090/files/1725569094268-鞋_箱包.png', 'BUSINESS', '18899990000', 'perfume@xm.com', '', '审核通过');
INSERT INTO `business` VALUES (4, 'cs', '123', '测试店铺', 'http://localhost:9090/files/1725568992004-1.png', 'BUSINESS', '18899990000', 'computer@xm.com', '', '审核通过');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户ID',
  `goods_id` int(0) NULL DEFAULT NULL COMMENT '商品ID',
  `business_id` int(0) NULL DEFAULT NULL COMMENT '店铺ID',
  `num` int(0) NULL DEFAULT NULL COMMENT '数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '商品的唯一标识，主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类的名称',
  `parent_id` int(0) NULL DEFAULT NULL COMMENT '父分类的 id，可为 NULL',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, '品牌分类', 0);
INSERT INTO `categories` VALUES (2, '车型分类', 0);
INSERT INTO `categories` VALUES (3, 'Canyon', 1);
INSERT INTO `categories` VALUES (4, 'Trek', 1);
INSERT INTO `categories` VALUES (5, '山地车', 2);
INSERT INTO `categories` VALUES (6, '公路车', 2);

-- ----------------------------
-- Table structure for collect
-- ----------------------------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户ID',
  `goods_id` int(0) NULL DEFAULT NULL COMMENT '商品ID',
  `business_id` int(0) NULL DEFAULT NULL COMMENT '店铺ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collect
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户ID',
  `goods_id` int(0) NULL DEFAULT NULL COMMENT '商品ID',
  `business_id` int(0) NULL DEFAULT NULL COMMENT '店铺ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评论内容',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评论时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品名称',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品主图',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商品介绍',
  `price` double(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
  `unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '计件单位',
  `count` int(0) NULL DEFAULT 0 COMMENT '商品销量',
  `type_id` int(0) NULL DEFAULT NULL COMMENT '分类ID',
  `business_id` int(0) NULL DEFAULT NULL COMMENT '商家ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES (18, 'SPECIALIZED闪电 TARMAC SL7 SPORT 碳纤维轻量气动公路自行车', 'http://localhost:9090/files/1740639894411-sp-sl7-1.jpg_.webp', '<p data-we-empty-p=\"\" class=\"extension-adhd-reader-p\"><a target=\"_blank\" href=\"https://detail.tmall.com/item.htm?abbucket=1&amp;id=738889100449&amp;ns=1&amp;pisk=gcASed_Y_uqSRyiHn30Vl2BXmbfQOqlaVy_pSeFzJ_CRvKTMAgunE_JfAnKf27uoE9IBq_BHUMSPA6THfqoZ_f8kr6XKbclNaD-5L12L28EUHrQFOahSgxuJr6fKArPKQfTkXWL7F8Qdkq_F87EJvTCADNjAJaQJviBAWNrR9HCKltQCJ7URe9LAHaSY27FL9rhA8wIL9Me-lEIcJ6IpvTHUxl7I8gTSyfoOT2w_lEIb9WdfkDbkPSVOrIQ5EGTJN7F--a65XUIxjhA_RtLhdB43OgLJIHb92unNEepXca1Ig8C9lpK1oQhaVZvykFSv5yVHr_961_pSm-fyVICvwdaL9E15q9OVhuifXQYvssBq6VTvi38kGezK9Z-NDUAROf3GN_IRGZAEm7IBHpdPE6q-bTYphn_56gRu_GskUJafIW_fbqgbKJxMRxRdPtmR4TQcPsgjlu1AeZbfbqgbKJXRoZyjlqZ5M&amp;priceTId=2150449417406384423477883e09ec&amp;skuId=5902260999407&amp;spm=a21n57.1.hoverItem.3&amp;utparam=%7B%22aplus_abtest%22%3A%2233e86f9d1ff36ab7e116529655918ab1%22%7D&amp;xxc=taobaoSearch\">https://detail.tmall.com/item.htm?abbucket=1&amp;id=738889100449&amp;ns=1&amp;pisk=gcASed_Y_uqSRyiHn30Vl2BXmbfQOqlaVy_pSeFzJ_CRvKTMAgunE_JfAnKf27uoE9IBq_BHUMSPA6THfqoZ_f8kr6XKbclNaD-5L12L28EUHrQFOahSgxuJr6fKArPKQfTkXWL7F8Qdkq_F87EJvTCADNjAJaQJviBAWNrR9HCKltQCJ7URe9LAHaSY27FL9rhA8wIL9Me-lEIcJ6IpvTHUxl7I8gTSyfoOT2w_lEIb9WdfkDbkPSVOrIQ5EGTJN7F--a65XUIxjhA_RtLhdB43OgLJIHb92unNEepXca1Ig8C9lpK1oQhaVZvykFSv5yVHr_961_pSm-fyVICvwdaL9E15q9OVhuifXQYvssBq6VTvi38kGezK9Z-NDUAROf3GN_IRGZAEm7IBHpdPE6q-bTYphn_56gRu_GskUJafIW_fbqgbKJxMRxRdPtmR4TQcPsgjlu1AeZbfbqgbKJXRoZyjlqZ5M&amp;priceTId=2150449417406384423477883e09ec&amp;skuId=5902260999407&amp;spm=a21n57.1.hoverItem.3&amp;utparam=%7B%22aplus_abtest%22%3A%2233e86f9d1ff36ab7e116529655918ab1%22%7D&amp;xxc=taobaoSearch</a></p>', 22990.00, '辆', 0, 2, 2);
INSERT INTO `goods` VALUES (19, 'SPECIALIZED闪电 ROCKHOPPER SPORT铝合金越野变速减震山地自行车', 'http://localhost:9090/files/1740724612311-sp-rock.png', '<p data-we-empty-p=\"\" class=\"extension-adhd-reader-p\"><img src=\"http://localhost:9090/files/1740724679814-image.png\" style=\"max-width:100%;\" contenteditable=\"false\"/></p><p data-we-empty-p=\"\" class=\"extension-adhd-reader-p\"><img src=\"http://localhost:9090/files/1740724697983-image.png\" style=\"max-width:100%;\" contenteditable=\"false\"/></p><p data-we-empty-p=\"\" class=\"extension-adhd-reader-p\"><img src=\"http://localhost:9090/files/1740724709788-image.png\" style=\"max-width:100%;\" contenteditable=\"false\"/></p>', 2990.00, '辆', 0, 1, 2);
INSERT INTO `goods` VALUES (20, 'SPECIALIZED闪电 Roubaix SL8 Comp 碳纤维公路车', 'http://localhost:9090/files/1740828911181-94423-52_ROUBAIX-COMP-DPLAKEMET-METWHTSGE_HERO.webp', '', 32990.00, '辆', 0, 2, 2);
INSERT INTO `goods` VALUES (21, 'SPECIALIZED闪电 FOUNDATION 男女式舒适弹力短袖公路车骑行服', 'http://localhost:9090/files/1740829204529-O1CN01voVq672B7nDN5OjDA_!!4611686018427384612-0-item_pic.jpg_180x180.avif', '', 490.00, '件', 0, 7, 2);

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建时间',
  `user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '1', '测试ing1', '2023-09-05', 'admin');
INSERT INTO `notice` VALUES (2, '2', '测试ing2', '2023-09-05', 'admin');
INSERT INTO `notice` VALUES (3, '3', '测试ing3', '2023-09-05', 'admin');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单ID',
  `goods_id` int(0) NULL DEFAULT NULL COMMENT '商品ID',
  `business_id` int(0) NULL DEFAULT NULL COMMENT '商家ID',
  `num` int(0) NULL DEFAULT NULL COMMENT '商品数量',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户ID',
  `price` double(10, 2) NULL DEFAULT NULL COMMENT '订单价格',
  `address_id` int(0) NULL DEFAULT NULL COMMENT '地址ID',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for type
-- ----------------------------
DROP TABLE IF EXISTS `type`;
CREATE TABLE `type`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类图标',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of type
-- ----------------------------
INSERT INTO `type` VALUES (1, '山地车', '', 'http://localhost:9090/files/1725484086574-珠宝饰品.png');
INSERT INTO `type` VALUES (2, '公路车', '', 'http://localhost:9090/files/1725484086574-珠宝饰品.png');
INSERT INTO `type` VALUES (3, '电助力E-bike', '', 'http://localhost:9090/files/1725484086574-珠宝饰品.png');
INSERT INTO `type` VALUES (4, '儿童车', NULL, 'http://localhost:9090/files/1725484086574-珠宝饰品.png');
INSERT INTO `type` VALUES (5, '城市车', NULL, 'http://localhost:9090/files/1725484086574-珠宝饰品.png');
INSERT INTO `type` VALUES (6, 'BMX', NULL, 'http://localhost:9090/files/1725484086574-珠宝饰品.png');
INSERT INTO `type` VALUES (7, '骑行服', NULL, 'http://localhost:9090/files/1725484086574-珠宝饰品.png');
INSERT INTO `type` VALUES (8, '轮组', NULL, 'http://localhost:9090/files/1725484086574-珠宝饰品.png');
INSERT INTO `type` VALUES (9, '套件', NULL, 'http://localhost:9090/files/1725484086574-珠宝饰品.png');
INSERT INTO `type` VALUES (10, '轮胎', NULL, 'http://localhost:9090/files/1725484086574-珠宝饰品.png');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色标识',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (6, 'yy', '123', '于越', 'http://localhost:9090/files/1740556119582-屏幕截图 2024-04-24 205317.png', 'USER', '15764290908', '1992106767@qq.com');

SET FOREIGN_KEY_CHECKS = 1;
