/*
Navicat MySQL Data Transfer

Source Server         : recruitment
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : xupt-acat-logs

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2021-06-16 15:59:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `tb_logs_record`
-- ----------------------------
DROP TABLE IF EXISTS `tb_logs_record`;
CREATE TABLE `tb_logs_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nid` varchar(255) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `op_user` varchar(255) DEFAULT NULL COMMENT '操作人',
  `op_details` varchar(255) DEFAULT '' COMMENT '操作细节',
  `work_type` varchar(255) DEFAULT NULL,
  `op_ext` varchar(1023) DEFAULT NULL COMMENT '扩展信息',
  `op_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_logs_record
-- ----------------------------
INSERT INTO `tb_logs_record` VALUES ('1', 'openjava@java@1', 'openjava', 'flow_system', '送入报名系统', 'process', null, '2021-06-11 21:32:32');
INSERT INTO `tb_logs_record` VALUES ('2', 'openjava@java@1', 'openjava', 'flow_system', '送入报名系统', 'process', null, '2021-06-11 21:35:04');
INSERT INTO `tb_logs_record` VALUES ('3', 'openjava@java@1', 'openjava', 'flow_system', '送入报名系统', 'process', null, '2021-06-11 21:39:29');
INSERT INTO `tb_logs_record` VALUES ('4', 'openjava@java@1', 'openjava', 'flow_system', '送入报名系统', 'process', null, '2021-06-11 21:42:14');
INSERT INTO `tb_logs_record` VALUES ('5', 'openjava@java@1', 'openjava', 'flow_system', '送入报名系统', 'process', null, '2021-06-11 22:10:03');
INSERT INTO `tb_logs_record` VALUES ('6', 'openjava@java@1', 'openjava', 'flow_system', '送入面试系统', 'apply', null, '2021-06-11 22:10:06');
INSERT INTO `tb_logs_record` VALUES ('7', 'openjava@java@1', 'openjava', 'flow_system', '送入报名系统', 'process', null, '2021-06-12 21:06:48');
INSERT INTO `tb_logs_record` VALUES ('8', 'openjava@java@1', 'openjava', 'flow_system', '送入面试系统', 'apply', null, '2021-06-12 21:06:50');
INSERT INTO `tb_logs_record` VALUES ('9', 'openjava@java@1', 'openjava', 'openjava', '已签到', 'process', null, '2021-06-13 13:29:25');
INSERT INTO `tb_logs_record` VALUES ('10', 'openjava@java@1', 'openjava', 'openjava', '已签退', 'process', null, '2021-06-13 13:35:35');
INSERT INTO `tb_logs_record` VALUES ('11', 'openjava@java@1', 'openjava', 'flow_system', '送入报名系统', 'process', null, '2021-06-13 13:51:31');
INSERT INTO `tb_logs_record` VALUES ('12', 'openjava@java@1', 'openjava', 'flow_system', '送入面试系统', 'apply', null, '2021-06-13 13:51:31');
INSERT INTO `tb_logs_record` VALUES ('13', 'openjava@java@1', 'openjava', 'recruitment_system', '等待签到', 'nosign_1', null, '2021-06-13 13:51:32');
INSERT INTO `tb_logs_record` VALUES ('14', 'openjava@java@1', 'openjava', 'openjava', '已签到', 'process', null, '2021-06-13 15:09:48');
INSERT INTO `tb_logs_record` VALUES ('15', 'openjava@java@1', 'openjava', 'flow_system', '送入报名系统', 'process', null, '2021-06-13 15:32:21');
INSERT INTO `tb_logs_record` VALUES ('16', 'openjava@java@1', 'openjava', 'flow_system', '送入面试系统', 'apply', null, '2021-06-13 15:32:24');
INSERT INTO `tb_logs_record` VALUES ('17', 'openjava@java@1', 'openjava', 'recruitment_system', '等待签到', 'nosign_1', null, '2021-06-13 15:32:29');
INSERT INTO `tb_logs_record` VALUES ('18', 'openjava@java@1', 'openjava', 'flow_system', '送入报名系统', 'process', null, '2021-06-13 15:55:56');
INSERT INTO `tb_logs_record` VALUES ('19', 'openjava@java@1', 'openjava', 'flow_system', '送入面试系统', 'apply', null, '2021-06-13 15:55:58');
INSERT INTO `tb_logs_record` VALUES ('20', 'openjava@java@1', 'openjava', 'recruitment_system', '等待签到', 'nosign_1', null, '2021-06-13 15:56:00');
INSERT INTO `tb_logs_record` VALUES ('21', 'openjava@java@1', 'openjava', 'openjava', '已签到', 'nosign_1', null, '2021-06-13 15:57:52');
INSERT INTO `tb_logs_record` VALUES ('22', 'openjava@java@1', 'openjava', 'openjava', '已签退', 'nosign_1', null, '2021-06-13 16:05:29');
INSERT INTO `tb_logs_record` VALUES ('23', 'openjava@java@1', 'openjava', 'openjava', '已签到', 'sign_1', null, '2021-06-13 16:05:58');
INSERT INTO `tb_logs_record` VALUES ('24', 'openjava@java@1', 'openjava', 'hehe', '获取面试任务', 'doing_1', null, '2021-06-13 16:06:47');
INSERT INTO `tb_logs_record` VALUES ('25', 'openjava@java@1', 'openjava', 'hehe', '释放面试任务', 'sign_1', null, '2021-06-13 16:08:16');
INSERT INTO `tb_logs_record` VALUES ('26', 'openjava@java@1', 'openjava', 'openjava', '已签退', 'nosign_1', null, '2021-06-13 16:21:22');
INSERT INTO `tb_logs_record` VALUES ('27', '2515159119@java@1', '2515159119', 'flow_system', '送入报名系统', 'process', null, '2021-06-13 17:09:37');
INSERT INTO `tb_logs_record` VALUES ('28', '2515159119@java@1', '2515159119', 'flow_system', '送入面试系统', 'apply', null, '2021-06-13 17:09:38');
INSERT INTO `tb_logs_record` VALUES ('29', '2515159119@java@1', '2515159119', 'recruitment_system', '等待签到', 'nosign_1', null, '2021-06-13 17:09:40');
INSERT INTO `tb_logs_record` VALUES ('30', '2515159119@java@1', '2515159119', 'recruitment_system', '等待签到', 'nosign_1', null, '2021-06-13 17:20:29');
INSERT INTO `tb_logs_record` VALUES ('31', '2515159119@java@1', '2515159119', 'flow_system', '送入面试系统', 'apply', null, '2021-06-13 17:20:30');
INSERT INTO `tb_logs_record` VALUES ('32', '2515159119@java@1', '2515159119', 'flow_system', '送入报名系统', 'process', null, '2021-06-13 17:20:30');
INSERT INTO `tb_logs_record` VALUES ('33', 'openjava@java@1', 'openjava', 'openjava', '已签到', 'sign_1', null, '2021-06-15 18:16:19');
INSERT INTO `tb_logs_record` VALUES ('34', 'openjava@java@1', 'openjava', 'lirui', '获取面试任务', 'doing_1', null, '2021-06-15 18:24:04');
INSERT INTO `tb_logs_record` VALUES ('35', 'openjava@java@1', 'openjava', 'lirui', '释放面试任务', 'sign_1', null, '2021-06-15 18:35:42');
INSERT INTO `tb_logs_record` VALUES ('36', 'openjava@java@1', 'openjava', 'lirui', '获取面试任务', 'doing_1', null, '2021-06-15 18:39:23');
INSERT INTO `tb_logs_record` VALUES ('37', 'openjava@java@1', 'openjava', 'lirui', '释放面试任务', 'sign_1', null, '2021-06-15 18:42:57');
INSERT INTO `tb_logs_record` VALUES ('38', 'openjava@java@1', 'openjava', 'lirui', '获取面试任务', 'doing_1', null, '2021-06-15 18:43:22');
INSERT INTO `tb_logs_record` VALUES ('39', 'openjava@java@1', 'openjava', 'lirui', '提交评价', 'complete_1', null, '2021-06-15 18:50:51');
INSERT INTO `tb_logs_record` VALUES ('40', 'openjava@java@1', 'openjava', 'lirui', '提交评价', 'complete_1', null, '2021-06-15 18:58:18');
INSERT INTO `tb_logs_record` VALUES ('41', 'openjava@java@1', 'openjava', 'lirui', '面试通过', 'pass_1', null, '2021-06-15 19:06:31');
INSERT INTO `tb_logs_record` VALUES ('42', 'openjava@java@1', 'openjava', 'lirui', '面试通过', 'pass_1', null, '2021-06-15 19:13:51');
INSERT INTO `tb_logs_record` VALUES ('43', 'openjava@java@1', 'openjava', 'lirui', '面试通过', 'pass_1', null, '2021-06-15 19:16:24');
INSERT INTO `tb_logs_record` VALUES ('44', 'openjava@java@1', 'openjava', 'flow_system', '送入面试系统', 'pass_1', null, '2021-06-15 20:17:21');
INSERT INTO `tb_logs_record` VALUES ('45', 'openjava@java@1', 'openjava', 'lirui', '面试通过', 'pass_1', null, '2021-06-15 20:17:22');
INSERT INTO `tb_logs_record` VALUES ('46', 'openjava@java@1', 'openjava', 'recruitment_system', '等待签到', 'nosign_2', null, '2021-06-15 20:17:24');
INSERT INTO `tb_logs_record` VALUES ('47', 'openjava@java@1', 'openjava', 'openjava', '已签到', 'sign_2', null, '2021-06-15 20:20:56');
INSERT INTO `tb_logs_record` VALUES ('48', 'openjava@java@1', 'openjava', 'lirui', '获取面试任务', 'doing_2', null, '2021-06-15 20:22:50');
INSERT INTO `tb_logs_record` VALUES ('49', 'openjava@java@1', 'openjava', 'lirui', '提交评价', 'complete_2', null, '2021-06-15 20:35:32');
INSERT INTO `tb_logs_record` VALUES ('50', 'openjava@java@1', 'openjava', 'lirui', '面试通过', 'pass_2', null, '2021-06-15 20:36:57');
INSERT INTO `tb_logs_record` VALUES ('51', 'openjava@java@1', 'openjava', 'flow_system', '送入面试系统', 'pass_2', null, '2021-06-15 20:36:58');
INSERT INTO `tb_logs_record` VALUES ('52', 'openjava@java@1', 'openjava', 'recruitment_system', '等待签到', 'nosign_3', null, '2021-06-15 20:37:00');
INSERT INTO `tb_logs_record` VALUES ('53', 'openjava@java@1', 'openjava', 'openjava', '已签到', 'sign_3', null, '2021-06-15 20:38:26');
INSERT INTO `tb_logs_record` VALUES ('54', 'openjava@java@1', 'openjava', 'lirui', '获取面试任务', 'doing_3', null, '2021-06-15 20:38:41');
INSERT INTO `tb_logs_record` VALUES ('55', 'openjava@java@1', 'openjava', 'lirui', '提交评价', 'complete_3', null, '2021-06-15 20:39:09');
INSERT INTO `tb_logs_record` VALUES ('56', 'openjava@java@1', 'openjava', 'lirui', '面试通过', 'pass_3', null, '2021-06-15 20:39:19');
INSERT INTO `tb_logs_record` VALUES ('57', 'openjava@java@1', 'openjava', 'flow_system', '面试流程结束', 'pass_3', null, '2021-06-15 20:39:19');
INSERT INTO `tb_logs_record` VALUES ('58', 'openjava@java@1', 'openjava', 'lirui', '面试通过', 'pass_3', null, '2021-06-15 20:57:12');
INSERT INTO `tb_logs_record` VALUES ('59', 'openjava@java@1', 'openjava', 'flow_system', '面试流程结束', 'pass_3', null, '2021-06-15 20:57:15');
INSERT INTO `tb_logs_record` VALUES ('60', 'openjava@java@1', 'openjava', 'lirui', '面试通过', 'pass_1', null, '2021-06-15 21:05:52');
INSERT INTO `tb_logs_record` VALUES ('61', 'openjava@java@1', 'openjava', 'flow_system', '送入面试系统', 'pass_1', null, '2021-06-15 21:05:54');
INSERT INTO `tb_logs_record` VALUES ('62', 'openjava@java@1', 'openjava', 'recruitment_system', '等待签到', 'nosign_2', null, '2021-06-15 21:05:54');
INSERT INTO `tb_logs_record` VALUES ('63', 'openjava@java@1', 'openjava', 'lirui', '面试通过', 'pass_2', null, '2021-06-15 21:07:04');
INSERT INTO `tb_logs_record` VALUES ('64', 'openjava@java@1', 'openjava', 'recruitment_system', '等待签到', 'nosign_3', null, '2021-06-15 21:07:05');
INSERT INTO `tb_logs_record` VALUES ('65', 'openjava@java@1', 'openjava', 'flow_system', '送入面试系统', 'pass_2', null, '2021-06-15 21:07:05');
INSERT INTO `tb_logs_record` VALUES ('66', 'openjava@java@1', 'openjava', 'lirui', '面试通过', 'pass_3', null, '2021-06-15 21:07:19');
INSERT INTO `tb_logs_record` VALUES ('67', 'openjava@java@1', 'openjava', 'flow_system', '面试流程结束', 'pass_3', null, '2021-06-15 21:07:19');
INSERT INTO `tb_logs_record` VALUES ('68', '2495399053@java@1', '2495399053', 'flow_system', '送入报名系统', 'process', null, '2021-06-15 21:17:09');
INSERT INTO `tb_logs_record` VALUES ('69', '2495399053@java@1', '2495399053', 'flow_system', '送入面试系统', 'apply', null, '2021-06-15 21:17:16');
INSERT INTO `tb_logs_record` VALUES ('70', '2495399053@java@1', '2495399053', 'recruitment_system', '等待签到', 'nosign_1', null, '2021-06-15 21:17:18');
INSERT INTO `tb_logs_record` VALUES ('71', '2495399053@java@1', '2495399053', '2495399053', '已签到', 'sign_1', null, '2021-06-15 21:19:44');
INSERT INTO `tb_logs_record` VALUES ('72', '2495399053@java@1', '2495399053', 'lirui', '获取面试任务', 'doing_1', null, '2021-06-15 21:19:59');
INSERT INTO `tb_logs_record` VALUES ('73', 'openjava@java@1', 'openjava', 'lirui', '提交评价', 'complete_3', null, '2021-06-15 21:20:35');
INSERT INTO `tb_logs_record` VALUES ('74', '2495399053@java@1', '2495399053', 'lirui', '提交评价', 'complete_1', null, '2021-06-15 21:21:44');
INSERT INTO `tb_logs_record` VALUES ('75', '2495399053@java@1', '2495399053', 'lirui', '面试未通过', 'nopass_1', null, '2021-06-15 21:22:26');
INSERT INTO `tb_logs_record` VALUES ('76', '2495399053@java@1', '2495399053', 'lirui', '面试未通过', 'nopass_1', null, '2021-06-15 21:32:44');
INSERT INTO `tb_logs_record` VALUES ('77', '2495399053@java@1', '2495399053', 'flow_system', '面试流程结束', 'nopass_1', null, '2021-06-15 21:32:46');
