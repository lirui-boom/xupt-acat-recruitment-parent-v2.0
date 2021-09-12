/*
Navicat MySQL Data Transfer

Source Server         : recruitment
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : xupt-acat-flowcontrol

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2021-06-16 15:59:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `tb_flow_record`
-- ----------------------------
DROP TABLE IF EXISTS `tb_flow_record`;
CREATE TABLE `tb_flow_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nid` varchar(255) NOT NULL DEFAULT 'nid=username+serviceline+version',
  `username` varchar(255) NOT NULL,
  `service_line` varchar(255) NOT NULL COMMENT '面试分组',
  `version` int(11) DEFAULT NULL COMMENT 'service_line版本',
  `work_type` varchar(255) NOT NULL COMMENT '流程状态',
  `status` int(11) NOT NULL COMMENT '流程是否结束 0 1pass 2 nopass',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_flow_record
-- ----------------------------
INSERT INTO `tb_flow_record` VALUES ('23', 'openjava@java@1', 'openjava', 'java', '1', 'process', '1', '2021-06-13 15:55:53', '2021-06-13 15:55:53');
INSERT INTO `tb_flow_record` VALUES ('25', '2515159119@java@1', '2515159119', 'java', '1', 'process', '0', '2021-06-13 17:20:26', '2021-06-13 17:20:26');
INSERT INTO `tb_flow_record` VALUES ('26', '2495399053@java@1', '2495399053', 'java', '1', 'process', '2', '2021-06-15 21:17:08', '2021-06-15 21:17:08');
