/*
Navicat MySQL Data Transfer

Source Server         : recruitment
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : xupt-acat-user

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2021-06-16 16:00:26
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `tb_user`
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `nick_name` varchar(255) DEFAULT NULL,
  `user_pic` varchar(1023) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1402189219388022807 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES ('1402189219388022803', 'openjava', '$2a$10$qoGKQC34S24gygAMLBzmV.OKrJ1X3/KlPSM/lRsojCAyWOL0.iFl2', 'openjava', 'http://acat.xupt.edu.cn/static/img/pic.4b8944a.png', 'openjava@sina.cn', '1', '2021-06-12 12:29:47', '2021-06-12 12:29:47');
INSERT INTO `tb_user` VALUES ('1402189219388022804', '2515159119', '$2a$10$AgxgmMC/seJ4keYyu9Ou9e0maflf3UL5D8xRV5cNJL98JAeT5uyMe', null, 'http://acat.xupt.edu.cn/static/img/pic.4b8944a.png', '2515159119@qq.com', '1', '2021-06-13 16:47:32', '2021-06-13 16:47:32');
INSERT INTO `tb_user` VALUES ('1402189219388022805', '2515159119', '$2a$10$IxMwXj11HFO5KIdcLvu98eTMcWJXXb6GDv/aZFQY5NnNIz8AiZNAO', null, 'http://acat.xupt.edu.cn/static/img/pic.4b8944a.png', '2515159119@qq.com', '1', '2021-06-13 16:49:15', '2021-06-13 16:49:15');
INSERT INTO `tb_user` VALUES ('1402189219388022806', '2495399053', '$2a$10$A9Xkq68eBRbpVjt1l.3zlOnlgNsPaKLNtoddPZ2Hu5PegNBCz/pbW', '萨斯给', 'http://acat.xupt.edu.cn/static/img/pic.4b8944a.png', '2495399053@qq.com', '1', '2021-06-15 21:10:29', '2021-06-15 21:10:29');

-- ----------------------------
-- Table structure for `tb_user_auth`
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_auth`;
CREATE TABLE `tb_user_auth` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `auth_key` varchar(255) NOT NULL,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_user_auth
-- ----------------------------
