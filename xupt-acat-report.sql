/*
Navicat MySQL Data Transfer

Source Server         : recruitment
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : xupt-acat-report

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2021-06-16 16:00:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `tb_report_conf`
-- ----------------------------
DROP TABLE IF EXISTS `tb_report_conf`;
CREATE TABLE `tb_report_conf` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_report_conf
-- ----------------------------

-- ----------------------------
-- Table structure for `tb_report_record`
-- ----------------------------
DROP TABLE IF EXISTS `tb_report_record`;
CREATE TABLE `tb_report_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `op_user` varchar(255) NOT NULL,
  `conds` varchar(1023) NOT NULL COMMENT 'ES查询条件',
  `status` int(11) DEFAULT NULL COMMENT '运行状态 0:新建 1:wait 2:执行ing 3:ok -1:fail',
  `detail` varchar(1023) DEFAULT NULL,
  `download` varchar(1023) DEFAULT NULL COMMENT 'url',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `status` (`status`) USING BTREE,
  KEY `op_user` (`op_user`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_report_record
-- ----------------------------
INSERT INTO `tb_report_record` VALUES ('1', 'openjava', '{\"username\":\"openjava\"}', '3', null, 'http://192.168.1.29:8000/2021/06/14/2021-06-14-10-31-50_95a7e47e7e014c16bdd78d6f82c63b77.xls', '2021-06-13 18:58:32', '2021-06-14 10:31:50');
INSERT INTO `tb_report_record` VALUES ('2', 'openjava', '{\"realName\":\"中国 中\"}', '2', null, null, '2021-06-14 10:44:25', '2021-06-14 10:49:25');
INSERT INTO `tb_report_record` VALUES ('3', 'openjava', '{\"realName\":\"中国 中\"}', '3', null, 'http://192.168.1.29:8000/2021/06/14/2021-06-14-10-54-26_de5c51028b92475fab1fd16185ee45d7.xls', '2021-06-14 10:54:18', '2021-06-14 10:54:26');
INSERT INTO `tb_report_record` VALUES ('4', 'openjava', '{\"realName\":\"中国 中\"}', '3', null, 'http://192.168.1.29:8000/2021/06/14/2021-06-14-11-29-52_bb4bbc91af0543f9bd884b1faf728849.xls', '2021-06-14 11:29:34', '2021-06-14 11:29:52');
INSERT INTO `tb_report_record` VALUES ('5', '2545159119', '{\"nid\":\"\",\"realName\":\"\",\"snumber\":\"\",\"className\":\"\",\"email\":\"\",\"phone\":\"\",\"serviceLine\":\"\",\"version\":\"\",\"turns\":\"\",\"status\":\"\"}', '3', null, 'http://192.168.1.29:8000/2021/06/14/2021-06-14-11-40-50_d42e90a29ed84c2e9f7bd51b36743ddf.xls', '2021-06-14 11:40:44', '2021-06-14 11:40:50');
