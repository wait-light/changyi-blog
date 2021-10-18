/*
 Navicat Premium Data Transfer

 Source Server         : adxd.top_3306
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : adxd.top:3306
 Source Schema         : changyi_blog

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 18/10/2021 12:16:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '文章名',
  `content` longtext COLLATE utf8_unicode_ci NOT NULL COMMENT '文章内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `publish_time` timestamp NULL DEFAULT NULL COMMENT '发布时间',
  `status` int(10) NOT NULL,
  `pageviews` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '浏览量',
  `typeid` int(10) unsigned DEFAULT NULL COMMENT '类型',
  `pure_string` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `userid` int(10) unsigned NOT NULL COMMENT '作者id',
  `pic` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `type` (`typeid`) USING BTREE,
  KEY `article_uiserid_1` (`userid`) USING BTREE,
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`typeid`) REFERENCES `article_type` (`id`) ON UPDATE NO ACTION,
  CONSTRAINT `article_uiserid_1` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for article_tag
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tagid` int(10) unsigned NOT NULL COMMENT '标签id',
  `articleid` int(10) unsigned NOT NULL COMMENT '文章id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `tagid` (`tagid`) USING BTREE,
  KEY `articleid` (`articleid`) USING BTREE,
  CONSTRAINT `article_tag_ibfk_1` FOREIGN KEY (`articleid`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `article_tag_ibfk_2` FOREIGN KEY (`tagid`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for article_type
-- ----------------------------
DROP TABLE IF EXISTS `article_type`;
CREATE TABLE `article_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '分类名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `content` varchar(2000) COLLATE utf8_unicode_ci NOT NULL COMMENT '评论内容',
  `userid` int(10) unsigned NOT NULL COMMENT '评论人',
  `parent_id` int(11) DEFAULT NULL COMMENT '是否层主',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `article_id` int(10) unsigned NOT NULL COMMENT '评论的文章',
  `floor_id` int(10) unsigned DEFAULT NULL COMMENT '楼层id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `userid` (`userid`) USING BTREE,
  KEY `comment_article` (`article_id`) USING BTREE,
  KEY `comment_floor_id` (`floor_id`) USING BTREE,
  CONSTRAINT `comment_floor_id` FOREIGN KEY (`floor_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id` int(10) unsigned NOT NULL,
  `userid` int(10) unsigned NOT NULL COMMENT '反馈用户',
  `content` text COLLATE utf8_unicode_ci NOT NULL COMMENT '反馈内容',
  `creatd_time` timestamp NULL DEFAULT NULL COMMENT '反馈时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `userid2` (`userid`) USING BTREE,
  CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for friendship_chain
-- ----------------------------
DROP TABLE IF EXISTS `friendship_chain`;
CREATE TABLE `friendship_chain` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '网站名称',
  `url` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '网站地址',
  `content` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '网站标语',
  `order` int(11) NOT NULL COMMENT '网站排行',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for history_article
-- ----------------------------
DROP TABLE IF EXISTS `history_article`;
CREATE TABLE `history_article` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `article_id` int(10) unsigned NOT NULL COMMENT '文章id',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `content` longtext COLLATE utf8_unicode_ci NOT NULL COMMENT '文章内容',
  `title` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '文章标题',
  `typeid` int(10) unsigned DEFAULT NULL COMMENT '文章类型',
  `pure_string` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '文本内容',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `aa` (`article_id`) USING BTREE,
  CONSTRAINT `history_article_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for map
-- ----------------------------
DROP TABLE IF EXISTS `map`;
CREATE TABLE `map` (
  `key` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `values` longtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for message_waiting
-- ----------------------------
DROP TABLE IF EXISTS `message_waiting`;
CREATE TABLE `message_waiting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userid` int(10) unsigned NOT NULL COMMENT '留言用户',
  `content` varchar(500) COLLATE utf8_unicode_ci NOT NULL COMMENT '留言内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '留言时间',
  `parentid` int(11) DEFAULT NULL COMMENT '用作回复',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `userid3` (`userid`) USING BTREE,
  CONSTRAINT `message_waiting_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for persistent_logins
-- ----------------------------
DROP TABLE IF EXISTS `persistent_logins`;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '标签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mobile` varchar(11) NOT NULL COMMENT '手机号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(20) DEFAULT NULL COMMENT '昵称',
  `avatar` longtext COMMENT '头像base64数据',
  `locked` char(1) NOT NULL DEFAULT 'N' COMMENT '是否启用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `user_type` tinyint(4) NOT NULL COMMENT '用户类型：\r\n0、普通用户 （只能评论、留言、浏览文章）\r\n1、管理员 （可以管理用户信息、管理文章、留言）\r\n2、超级管理员 （拥有所有权限）',
  `setting` longtext COMMENT '用户设置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
