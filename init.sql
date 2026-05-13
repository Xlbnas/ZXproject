-- 校园教材循环利用平台 数据库初始化脚本
-- 建库、建表、插入完整测试数据

CREATE DATABASE IF NOT EXISTS campus_textbook DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_textbook;

-- ============================
-- 表结构
-- ============================

-- 表4-1 用户表
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name   VARCHAR(50)  COMMENT '真实姓名',
    student_id  VARCHAR(20)  COMMENT '学号',
    phone       VARCHAR(20)  COMMENT '手机号',
    email       VARCHAR(100) COMMENT '邮箱',
    avatar      VARCHAR(255) COMMENT '头像路径',
    major       VARCHAR(50)  COMMENT '专业',
    user_type   TINYINT      NOT NULL DEFAULT 1 COMMENT '用户类型 1-普通用户 2-管理员',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态 0-禁用 1-正常',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 表4-2 管理员表
DROP TABLE IF EXISTS t_admin;
CREATE TABLE t_admin (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '管理员ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码',
    real_name   VARCHAR(50)  COMMENT '真实姓名',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 表4-3 教材表（字段名与后端实体严格对应）
DROP TABLE IF EXISTS t_textbook;
CREATE TABLE t_textbook (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '教材ID',
    isbn            VARCHAR(30)  COMMENT 'ISBN号',
    book_name       VARCHAR(200) NOT NULL COMMENT '书名',
    author          VARCHAR(100) COMMENT '作者',
    publisher       VARCHAR(100) COMMENT '出版社',
    edition         VARCHAR(20)  COMMENT '版次',
    subject         VARCHAR(50)  COMMENT '科目/专业',
    description     TEXT         COMMENT '详细描述',
    expected_price  DECIMAL(8,2) NOT NULL COMMENT '出售价格',
    image_url       VARCHAR(255) COMMENT '封面图片路径',
    original_price  DECIMAL(8,2) COMMENT '原价',
    publisher_id    BIGINT       NOT NULL COMMENT '发布者用户ID',
    publish_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    condition_desc  VARCHAR(200) COMMENT '品相描述',
    trans_type      VARCHAR(10)  NOT NULL DEFAULT '1' COMMENT '交易方式 1-出售 2-捐赠',
    audit_status    TINYINT      NOT NULL DEFAULT 0 COMMENT '审核状态 0-待审核 1-通过 2-驳回',
    reject_reason   VARCHAR(255) COMMENT '驳回原因',
    status          TINYINT      NOT NULL DEFAULT 0 COMMENT '上架状态 0-下架/未过审 1-在架',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材表';

-- 表4-4 交易表
DROP TABLE IF EXISTS t_transaction;
CREATE TABLE t_transaction (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '交易ID',
    textbook_id     BIGINT       NOT NULL COMMENT '教材ID',
    requester_id    BIGINT       NOT NULL COMMENT '申请人（买家）ID',
    owner_id        BIGINT       NOT NULL COMMENT '持有人（卖家）ID',
    trans_price     DECIMAL(8,2) COMMENT '成交价格',
    trans_status    TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1-待确认 2-已确认 3-已完成 4-已取消 5-纠纷中',
    remark          VARCHAR(500) COMMENT '留言',
    confirm_time    DATETIME     COMMENT '确认时间',
    complete_time   DATETIME     COMMENT '完成时间',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发起时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易表';

-- 表4-5 收藏表
DROP TABLE IF EXISTS t_favorite;
CREATE TABLE t_favorite (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    textbook_id BIGINT   NOT NULL COMMENT '教材ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_user_book (user_id, textbook_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 表4-6 评价表
DROP TABLE IF EXISTS t_review;
CREATE TABLE t_review (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    transaction_id  BIGINT       NOT NULL COMMENT '交易ID',
    reviewer_id     BIGINT       NOT NULL COMMENT '评价人ID',
    reviewed_id     BIGINT       NOT NULL COMMENT '被评价人ID',
    rating          TINYINT      NOT NULL DEFAULT 5 COMMENT '评分 1-5星',
    content         VARCHAR(500) COMMENT '评价内容',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    update_time     DATETIME     COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 表4-7 公告表
DROP TABLE IF EXISTS t_announcement;
CREATE TABLE t_announcement (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公告ID',
    title       VARCHAR(200) NOT NULL COMMENT '标题',
    content     TEXT         NOT NULL COMMENT '内容',
    admin_id    BIGINT       COMMENT '发布管理员ID',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    update_time DATETIME     COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 表4-8 全局环保统计表
DROP TABLE IF EXISTS t_eco_stats;
CREATE TABLE t_eco_stats (
    id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    total_books  INT           NOT NULL DEFAULT 0 COMMENT '累计循环教材本数',
    total_saved  DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '累计节省金额（元）',
    total_co2    DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '累计减少碳排放（kg）',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局环保统计表';

-- 表4-9 用户环保贡献表
DROP TABLE IF EXISTS t_user_eco;
CREATE TABLE t_user_eco (
    id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT        NOT NULL UNIQUE COMMENT '用户ID',
    total_books  INT           NOT NULL DEFAULT 0 COMMENT '参与循环本数',
    total_saved  DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '节省金额',
    total_co2    DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '减排量（kg）',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户环保贡献表';


-- ============================
-- 测试数据
-- ============================

-- 管理员（密码 admin123）
INSERT INTO t_admin (username, password, real_name) VALUES
('admin', '$2b$10$abcdefghijklmnopqrstuOV/NYJoU.sdfr6C8Dly7wPd/UOSbcsvO', '系统管理员');

-- 普通用户（密码均为 test123）
INSERT INTO t_user (username, password, real_name, student_id, phone, email, major, user_type, status) VALUES
('zhangsan', '$2b$10$abcdefghijklmnopqrstuOPBlunhbqcPh2ROxs8a0YT6S5PAELQEK', '张三', '2022001001', '13800001111', 'zhangsan@example.com', '计算机科学与技术', 1, 1),
('lisi',     '$2b$10$abcdefghijklmnopqrstuOPBlunhbqcPh2ROxs8a0YT6S5PAELQEK', '李四', '2022001002', '13800002222', 'lisi@example.com',     '软件工程',         1, 1),
('wangwu',   '$2b$10$abcdefghijklmnopqrstuOPBlunhbqcPh2ROxs8a0YT6S5PAELQEK', '王五', '2022001003', '13800003333', 'wangwu@example.com',   '数据科学与大数据', 1, 1),
('zhaoliu',  '$2b$10$abcdefghijklmnopqrstuOPBlunhbqcPh2ROxs8a0YT6S5PAELQEK', '赵六', '2021003012', '13800004444', 'zhaoliu@example.com',  '网络工程',         1, 1),
('sunqi',    '$2b$10$abcdefghijklmnopqrstuOPBlunhbqcPh2ROxs8a0YT6S5PAELQEK', '孙七', '2021003013', '13800005555', 'sunqi@example.com',    '人工智能',         1, 1),
('admin',    '$2b$10$abcdefghijklmnopqrstuOV/NYJoU.sdfr6C8Dly7wPd/UOSbcsvO', '管理员','0000000000', '13900000000', 'admin@campus.edu',    NULL,               2, 1);

-- 初始化全局环保统计
INSERT INTO t_eco_stats (total_books, total_saved, total_co2) VALUES (0, 0.00, 0.00);

-- 用户环保贡献初始化
INSERT INTO t_user_eco (user_id, total_books, total_saved, total_co2)
SELECT id, 0, 0.00, 0.00 FROM t_user;

-- ============================
-- 教材数据（10本在架 + 1待审核 + 1已驳回）
-- user_id: zhangsan=1 lisi=2 wangwu=3 zhaoliu=4 sunqi=5 admin=6
-- ============================
INSERT INTO t_textbook
    (isbn, book_name, author, publisher, edition, subject, description, expected_price, image_url, original_price, publisher_id, publish_time, condition_desc, trans_type, audit_status, status)
VALUES
-- 1. zhangsan 发布
('9787040396461', '高等数学（上册）', '同济大学数学系', '高等教育出版社', '第六版', '数学',
 '同济版高等数学上册，第六版，内页干净无画线，适合大一学生使用。',
 15.00, '/uploads/books/book1.jpg', 35.00,
 1, DATE_SUB(NOW(), INTERVAL 30 DAY), '九成新，无涂写，封面略有磨损', '1', 1, 1),

-- 2. lisi 发布
('9787560089096', '大学英语（四级）', '许国璋', '外语教学与研究出版社', '第四版', '英语',
 '大学英语四级辅导教材，含历年真题解析，备考利器。',
 18.00, '/uploads/books/book2.jpg', 42.00,
 2, DATE_SUB(NOW(), INTERVAL 25 DAY), '八成新，有少量铅笔标注，可擦除', '1', 1, 1),

-- 3. wangwu 发布
('9787040280999', '线性代数', '同济大学数学系', '高等教育出版社', '第五版', '数学',
 '线性代数经典教材，同济版第五版，适合理工科学生。',
 12.00, '/uploads/books/book3.jpg', 28.00,
 3, DATE_SUB(NOW(), INTERVAL 20 DAY), '九成新，内页整洁', '1', 1, 1),

-- 4. zhaoliu 发布
('9787302318545', 'C程序设计（第四版）', '谭浩强', '清华大学出版社', '第四版', '计算机',
 '谭浩强C语言教材，国内使用最广泛的C语言教材之一，适合编程入门。',
 14.00, '/uploads/books/book4.jpg', 33.00,
 4, DATE_SUB(NOW(), INTERVAL 18 DAY), '七成新，有部分荧光笔标记', '1', 1, 1),

-- 5. sunqi 发布
('9787302237044', '数据结构（C语言版）', '严蔚敏', '清华大学出版社', '第二版', '计算机',
 '严蔚敏数据结构，考研必备，内容经典，附有课后习题答案。',
 20.00, '/uploads/books/book5.jpg', 38.00,
 5, DATE_SUB(NOW(), INTERVAL 15 DAY), '八成新，无写画', '1', 1, 1),

-- 6. zhangsan 发布
('9787121385209', '计算机网络（第七版）', '谢希仁', '电子工业出版社', '第七版', '计算机',
 '谢希仁计网第七版，内容全面，适合期末复习和考研冲刺。',
 22.00, '/uploads/books/book6.jpg', 45.00,
 1, DATE_SUB(NOW(), INTERVAL 12 DAY), '九成新，仅有少量铅笔记号', '1', 1, 1),

-- 7. lisi 发布
('9787111213826', 'Java核心技术（卷Ⅰ）', 'Cay S. Horstmann', '机械工业出版社', '第九版', '计算机',
 'Java领域经典书籍，涵盖Java基础到高级特性，开发者必读。',
 45.00, '/uploads/books/book7.jpg', 89.00,
 2, DATE_SUB(NOW(), INTERVAL 10 DAY), '八成新，书角略有折痕', '1', 1, 1),

-- 8. wangwu 发布（捐赠）
('9787040396478', '大学物理（上册）', '马文蔚', '高等教育出版社', '第六版', '物理',
 '大学物理上册，马文蔚版，内页全新无任何痕迹，免费捐赠给有需要的同学。',
 0.00, '/uploads/books/book8.jpg', 30.00,
 3, DATE_SUB(NOW(), INTERVAL 8 DAY), '九五成新，全新状态', '2', 1, 1),

-- 9. zhaoliu 发布
('9787115428028', 'Python编程：从入门到实践', 'Eric Matthes', '人民邮电出版社', '第一版', '计算机',
 'Python入门首选书目，案例丰富，实战性强，适合零基础学习。',
 30.00, '/uploads/books/book9.jpg', 59.00,
 4, DATE_SUB(NOW(), INTERVAL 6 DAY), '九成新，无涂写', '1', 1, 1),

-- 10. sunqi 发布
('9780470233993', '操作系统概念（第九版）', 'Abraham Silberschatz', '机械工业出版社', '第九版', '计算机',
 '操作系统经典恐龙书，考研和课程学习必备，内容权威。',
 38.00, '/uploads/books/book10.jpg', 75.00,
 5, DATE_SUB(NOW(), INTERVAL 4 DAY), '八成新，封面有使用痕迹，内页良好', '1', 1, 1),

-- 11. zhangsan 发布（待审核）
('9787040238822', '概率论与数理统计', '浙江大学', '高等教育出版社', '第四版', '数学',
 '概率论第四版，适合理工科大二学生。',
 16.00, NULL, 32.00,
 1, DATE_SUB(NOW(), INTERVAL 2 DAY), '八成新', '1', 0, 0),

-- 12. lisi 发布（已驳回）
('0000000001', '英语词汇乱写版', '某某', '未知出版社', '第一版', '英语',
 '内容不全，仅供参考。',
 1.00, NULL, 10.00,
 2, DATE_SUB(NOW(), INTERVAL 1 DAY), '差', '1', 2, 0);

-- 更新驳回原因
UPDATE t_textbook SET reject_reason='描述过于简单，无法判断教材品相，请补充详细说明' WHERE isbn='0000000001';

-- ============================
-- 交易记录（6笔）
-- ============================
-- 交易1：zhangsan买lisi的大学英语 → 已完成
INSERT INTO t_transaction (textbook_id, requester_id, owner_id, trans_price, trans_status, remark, confirm_time, complete_time, create_time)
VALUES (2, 1, 2, 18.00, 3, '书品相还不错，谢谢', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY));

-- 交易2：wangwu买zhangsan的高等数学 → 已完成
INSERT INTO t_transaction (textbook_id, requester_id, owner_id, trans_price, trans_status, remark, confirm_time, complete_time, create_time)
VALUES (1, 3, 1, 15.00, 3, '教材保存很好，推荐', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY));

-- 交易3：sunqi领取wangwu捐赠的大学物理 → 已完成
INSERT INTO t_transaction (textbook_id, requester_id, owner_id, trans_price, trans_status, remark, confirm_time, complete_time, create_time)
VALUES (8, 5, 3, 0.00, 3, '感谢无偿捐赠！', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY));

-- 交易4：lisi买zhaoliu的C程序设计 → 已确认待完成
INSERT INTO t_transaction (textbook_id, requester_id, owner_id, trans_price, trans_status, remark, confirm_time, create_time)
VALUES (4, 2, 4, 14.00, 2, '请约定好地点交接', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 交易5：zhaoliu买sunqi的数据结构 → 待确认
INSERT INTO t_transaction (textbook_id, requester_id, owner_id, trans_price, trans_status, remark, create_time)
VALUES (5, 4, 5, 20.00, 1, '这本书我急需，希望能尽快', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 交易6：sunqi买lisi的Java核心技术 → 已取消
INSERT INTO t_transaction (textbook_id, requester_id, owner_id, trans_price, trans_status, remark, create_time)
VALUES (7, 5, 2, 45.00, 4, '已在其他渠道购买', DATE_SUB(NOW(), INTERVAL 8 DAY));

-- ============================
-- 评价数据（3笔已完成交易的双向评价）
-- ============================
-- 交易1：zhangsan评lisi / lisi评zhangsan
INSERT INTO t_review (transaction_id, reviewer_id, reviewed_id, rating, content, create_time)
VALUES (1, 1, 2, 5, '卖家很诚信，书的品相和描述一致，交接很顺利，推荐！', DATE_SUB(NOW(), INTERVAL 19 DAY));
INSERT INTO t_review (transaction_id, reviewer_id, reviewed_id, rating, content, create_time)
VALUES (1, 2, 1, 5, '买家沟通很好，交接准时，好评！', DATE_SUB(NOW(), INTERVAL 19 DAY));

-- 交易2：wangwu评zhangsan / zhangsan评wangwu
INSERT INTO t_review (transaction_id, reviewer_id, reviewed_id, rating, content, create_time)
VALUES (2, 3, 1, 4, '书保存得不错，价格合理，就是取书地点稍微远了一点。', DATE_SUB(NOW(), INTERVAL 14 DAY));
INSERT INTO t_review (transaction_id, reviewer_id, reviewed_id, rating, content, create_time)
VALUES (2, 1, 3, 5, '买家很好，准时赴约，五星好评！', DATE_SUB(NOW(), INTERVAL 14 DAY));

-- 交易3：sunqi评wangwu / wangwu评sunqi
INSERT INTO t_review (transaction_id, reviewer_id, reviewed_id, rating, content, create_time)
VALUES (3, 5, 3, 5, '非常感谢无偿捐赠，书的品相很好，这种精神值得点赞！', DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO t_review (transaction_id, reviewer_id, reviewed_id, rating, content, create_time)
VALUES (3, 3, 5, 5, '同学很有礼貌，希望书对你有帮助！', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ============================
-- 收藏数据
-- ============================
INSERT INTO t_favorite (user_id, textbook_id, create_time) VALUES
(1, 5,  DATE_SUB(NOW(), INTERVAL 10 DAY)),
(1, 7,  DATE_SUB(NOW(), INTERVAL 9 DAY)),
(1, 9,  DATE_SUB(NOW(), INTERVAL 7 DAY)),
(2, 3,  DATE_SUB(NOW(), INTERVAL 8 DAY)),
(2, 6,  DATE_SUB(NOW(), INTERVAL 6 DAY)),
(3, 4,  DATE_SUB(NOW(), INTERVAL 5 DAY)),
(3, 9,  DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 10, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 1,  DATE_SUB(NOW(), INTERVAL 7 DAY)),
(4, 6,  DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, 2,  DATE_SUB(NOW(), INTERVAL 4 DAY)),
(5, 7,  DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ============================
-- 公告数据（5条）
-- ============================
INSERT INTO t_announcement (title, content, admin_id, create_time) VALUES
('平台上线公告',
 '欢迎使用校园教材循环利用平台！本平台旨在帮助同学们低价转让和获取二手教材，节省学习成本，践行绿色环保理念。祝大家学习顺利！',
 1, DATE_SUB(NOW(), INTERVAL 60 DAY)),
('教材发布规范说明',
 '请各位同学在发布教材时：1. 如实填写教材品相，不得夸大；2. ISBN号请务必填写正确；3. 上传清晰的教材封面照片；4. 定价合理，建议不超过原价50%。违规发布将被驳回。',
 1, DATE_SUB(NOW(), INTERVAL 45 DAY)),
('期末季教材回收活动',
 '期末季来临，平台特别发起"教材回收季"活动！即日起至本学期末，完成一笔交易即可获得积分奖励。欢迎大家积极参与，将闲置教材传递给有需要的同学！',
 1, DATE_SUB(NOW(), INTERVAL 20 DAY)),
('系统维护通知',
 '平台将于本周六凌晨 2:00-4:00 进行系统升级维护，届时服务暂时不可用。给您带来不便，敬请谅解。',
 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('新功能上线：教材收藏',
 '收藏功能已正式上线！登录后可在教材详情页点击"收藏"按钮，方便追踪感兴趣的教材。收藏列表可在个人中心查看。',
 1, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ============================
-- 更新环保统计（3笔已完成交易：节省金额18+15+0=33元，减碳3×0.5=1.5kg）
-- ============================
UPDATE t_eco_stats SET total_books=3, total_saved=33.00, total_co2=1.50;
UPDATE t_user_eco SET total_books=2, total_saved=15.00, total_co2=1.00 WHERE user_id=1;
UPDATE t_user_eco SET total_books=1, total_saved=18.00, total_co2=0.50 WHERE user_id=2;
UPDATE t_user_eco SET total_books=2, total_saved=15.00, total_co2=1.00 WHERE user_id=3;
UPDATE t_user_eco SET total_books=1, total_saved=0.00,  total_co2=0.50 WHERE user_id=5;
