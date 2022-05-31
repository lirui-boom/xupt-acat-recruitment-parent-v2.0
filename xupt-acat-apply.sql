/*
Navicat MySQL Data Transfer

Source Server         : recruitment
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : xupt-acat-apply

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2021-06-16 15:59:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `tb_apply_record`
-- ----------------------------
DROP TABLE IF EXISTS `tb_apply_record`;
CREATE TABLE `tb_apply_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nid` varchar(255) DEFAULT NULL COMMENT 'nid',
  `username` varchar(255) DEFAULT NULL COMMENT 'username',
  `snumber` varchar(255) DEFAULT NULL COMMENT '学号',
  `real_name` varchar(255) DEFAULT NULL COMMENT 'real name',
  `class_name` varchar(255) DEFAULT NULL COMMENT 'class name',
  `sex` int(11) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `content` varchar(1023) DEFAULT NULL COMMENT '留言',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_apply_record
-- ----------------------------
INSERT INTO `tb_apply_record` VALUES ('6', 'openjava@java@1', 'openjava', '04182023', '李睿', '网络1801', '0', '15691729703', '哈哈哈hahah', null, null);
INSERT INTO `tb_apply_record` VALUES ('8', '2515159119@java@1', '2515159119', '2341413', 'ljm', '1244', '1', '423425', 'rfrads', null, null);
INSERT INTO `tb_apply_record` VALUES ('9', '2495399053@java@1', '2495399053', '03182023', '萨斯给', '软件1801', '0', '15691729703', '软件', null, null);
