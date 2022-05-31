/*
Navicat MySQL Data Transfer

Source Server         : recruitment
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : xupt-acat-recruitment

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2021-06-16 16:00:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `tb_evaluate_record`
-- ----------------------------
DROP TABLE IF EXISTS `tb_evaluate_record`;
CREATE TABLE `tb_evaluate_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nid` varchar(255) NOT NULL,
  `evaluate_user` varchar(255) NOT NULL,
  `content` varchar(1023) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `work_type` varchar(255) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_evaluate_record
-- ----------------------------
INSERT INTO `tb_evaluate_record` VALUES ('2', 'openjava@java@1', 'lirui', '基础ojbk', '80', 'complete_1', '2021-06-15 18:50:44', '2021-06-15 18:50:44');
INSERT INTO `tb_evaluate_record` VALUES ('3', 'openjava@java@1', 'lirui', '基础ojbk', '80', 'complete_1', '2021-06-15 18:58:12', '2021-06-15 18:58:12');
INSERT INTO `tb_evaluate_record` VALUES ('4', 'openjava@java@1', 'lirui', '二面ok', '80', 'complete_2', '2021-06-15 20:35:23', '2021-06-15 20:35:23');
INSERT INTO `tb_evaluate_record` VALUES ('5', 'openjava@java@1', 'lirui', '三面ok', '90', 'complete_3', '2021-06-15 20:39:06', '2021-06-15 20:39:06');
INSERT INTO `tb_evaluate_record` VALUES ('6', 'openjava@java@1', 'lirui', '一面基础不行', '59', 'complete_3', '2021-06-15 21:20:35', '2021-06-15 21:20:35');
INSERT INTO `tb_evaluate_record` VALUES ('7', '2495399053@java@1', 'lirui', '一面基础不行', '59', 'complete_1', '2021-06-15 21:21:43', '2021-06-15 21:21:43');

-- ----------------------------
-- Table structure for `tb_recruitment_record`
-- ----------------------------
DROP TABLE IF EXISTS `tb_recruitment_record`;
CREATE TABLE `tb_recruitment_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `nid` varchar(255) NOT NULL,
  `work_type` varchar(255) NOT NULL,
  `notice_status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nid_work` (`nid`,`work_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_recruitment_record
-- ----------------------------
INSERT INTO `tb_recruitment_record` VALUES ('2', 'openjava', 'openjava@java@1', 'recruitment_1', '1', '2021-06-13 13:51:31');
INSERT INTO `tb_recruitment_record` VALUES ('5', '2515159119', '2515159119@java@1', 'recruitment_1', null, '2021-06-13 17:09:37');
INSERT INTO `tb_recruitment_record` VALUES ('7', 'openjava', 'openjava@java@1', 'pass_2', '0', '2021-06-15 20:17:21');
INSERT INTO `tb_recruitment_record` VALUES ('8', 'openjava', 'openjava@java@1', 'pass_3', '0', '2021-06-15 20:36:58');
INSERT INTO `tb_recruitment_record` VALUES ('9', 'openjava', 'openjava@java@1', 'pass_2', '0', '2021-06-15 21:05:52');
INSERT INTO `tb_recruitment_record` VALUES ('10', 'openjava', 'openjava@java@1', 'pass_3', '0', '2021-06-15 21:07:04');
INSERT INTO `tb_recruitment_record` VALUES ('11', '2495399053', '2495399053@java@1', 'pass_1', '0', '2021-06-15 21:17:16');

-- ----------------------------
-- Table structure for `tb_service_line`
-- ----------------------------
DROP TABLE IF EXISTS `tb_service_line`;
CREATE TABLE `tb_service_line` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_line` varchar(255) DEFAULT NULL COMMENT '分组(业务线)名称',
  `qps` int(11) DEFAULT NULL,
  `work_details` varchar(1023) DEFAULT NULL COMMENT '流转配置json',
  `turns` int(11) NOT NULL COMMENT '轮次',
  `auth_key` varchar(255) NOT NULL COMMENT '权限关键字',
  `desc` varchar(255) DEFAULT NULL COMMENT '描述信息（富文本）',
  `status` int(11) NOT NULL COMMENT '开放状态',
  `version` int(11) NOT NULL COMMENT '版本',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_service_line
-- ----------------------------
INSERT INTO `tb_service_line` VALUES ('3', 'java', '3', '[{\"flowName\":\"apply\",\"next\":\"pass_1\"},{\"flowName\":\"pass_1\",\"next\":\"pass_2\"},{\"flowName\":\"pass_2\",\"next\":\"pass_3\"},{\"flowName\":\"pass_3\"}]', '3', 'auth_java', null, '1', '1', '2021-06-15 20:15:18', '2021-06-15 20:15:18');
