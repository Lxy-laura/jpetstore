-- ============================================
-- JPetStore 数据库建表脚本
-- 适用于 MySQL 8.0
-- ============================================

CREATE DATABASE IF NOT EXISTS jpetstore DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE jpetstore;

-- ============================================
-- 1. 删除已有表（按依赖关系逆序删除）
-- ============================================
DROP TABLE IF EXISTS orderstatus;
DROP TABLE IF EXISTS lineitem;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS profile;
DROP TABLE IF EXISTS signon;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS banner;

-- ============================================
-- 2. 创建表
-- ============================================

CREATE TABLE account (
    userid    VARCHAR(80) NOT NULL,
    email     VARCHAR(80) NOT NULL,
    firstname VARCHAR(80) NOT NULL,
    lastname  VARCHAR(80) NOT NULL,
    status    VARCHAR(2)  NULL,
    addr1     VARCHAR(80) NOT NULL,
    addr2     VARCHAR(40) NULL,
    city      VARCHAR(80) NOT NULL,
    state     VARCHAR(80) NOT NULL,
    zip       VARCHAR(20) NOT NULL,
    country   VARCHAR(20) NOT NULL,
    phone     VARCHAR(80) NOT NULL,
    role      VARCHAR(20) NULL DEFAULT 'USER',
    PRIMARY KEY (userid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE signon (
    username VARCHAR(25) NOT NULL,
    password VARCHAR(25) NOT NULL,
    PRIMARY KEY (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE profile (
    userid      VARCHAR(80) NOT NULL,
    langpref    VARCHAR(80) NOT NULL,
    favcategory VARCHAR(30) NULL,
    mylistopt   TINYINT(1)  NULL,
    banneropt   TINYINT(1)  NULL,
    PRIMARY KEY (userid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE category (
    catid       VARCHAR(10) NOT NULL,
    name        VARCHAR(80) NULL,
    description VARCHAR(255) NULL,
    PRIMARY KEY (catid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product (
    productid   VARCHAR(10) NOT NULL,
    category    VARCHAR(10) NOT NULL,
    name        VARCHAR(80) NULL,
    description VARCHAR(255) NULL,
    image       VARCHAR(255) NULL,
    price       DECIMAL(10,2) NULL DEFAULT 0.00,
    status      VARCHAR(20) NULL DEFAULT 'ON_SALE',
    PRIMARY KEY (productid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE item (
    itemid    VARCHAR(10) NOT NULL,
    productid VARCHAR(10) NOT NULL,
    listprice DECIMAL(10,2) NULL,
    unitcost  DECIMAL(10,2) NULL,
    supplier  INT NULL,
    status    VARCHAR(2) NULL,
    attr1     VARCHAR(80) NULL,
    attr2     VARCHAR(80) NULL,
    attr3     VARCHAR(80) NULL,
    attr4     VARCHAR(80) NULL,
    attr5     VARCHAR(80) NULL,
    qty       INT NULL DEFAULT 0,
    PRIMARY KEY (itemid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE orders (
    orderid        INT NOT NULL AUTO_INCREMENT,
    userid         VARCHAR(80) NOT NULL,
    orderdate      DATETIME NOT NULL,
    shipaddr1      VARCHAR(80) NOT NULL,
    shipaddr2      VARCHAR(40) NULL,
    shipcity       VARCHAR(80) NOT NULL,
    shipstate      VARCHAR(80) NOT NULL,
    shipzip        VARCHAR(20) NOT NULL,
    shipcountry    VARCHAR(20) NOT NULL,
    billaddr1      VARCHAR(80) NOT NULL,
    billaddr2      VARCHAR(40) NULL,
    billcity       VARCHAR(80) NOT NULL,
    billstate      VARCHAR(80) NOT NULL,
    billzip        VARCHAR(20) NOT NULL,
    billcountry    VARCHAR(20) NOT NULL,
    courier        VARCHAR(80) NOT NULL,
    totalprice     DECIMAL(10,2) NOT NULL,
    billtofirstname VARCHAR(80) NOT NULL,
    billtolastname  VARCHAR(80) NOT NULL,
    shiptofirstname VARCHAR(80) NOT NULL,
    shiptolastname  VARCHAR(80) NOT NULL,
    creditcard     VARCHAR(40) NOT NULL,
    exprdate       VARCHAR(7) NOT NULL,
    cardtype       VARCHAR(80) NOT NULL,
    locale         VARCHAR(80) NOT NULL,
    status         VARCHAR(2) NULL DEFAULT 'P',
    PRIMARY KEY (orderid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE lineitem (
    orderid  INT NOT NULL,
    linenum  INT NOT NULL,
    itemid   VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    unitprice DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (orderid, linenum)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE orderstatus (
    orderid   INT NOT NULL,
    linenum   INT NOT NULL,
    timestamp DATETIME NOT NULL,
    status    VARCHAR(2) NOT NULL,
    PRIMARY KEY (orderid, linenum, timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 首页轮播图表
CREATE TABLE banner (
    id          INT NOT NULL AUTO_INCREMENT,
    title       VARCHAR(100) NOT NULL,
    subtitle    VARCHAR(200) NULL,
    image       VARCHAR(255) NULL,
    link        VARCHAR(255) NULL,
    sort_order  INT NULL DEFAULT 0,
    active      TINYINT(1) NULL DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 3. 插入测试数据
-- ============================================

INSERT INTO category (catid, name, description) VALUES
    ('FISH', '鱼类', '各种观赏鱼品种'),
    ('DOGS', '狗狗', '忠诚伴侣犬品种'),
    ('CATS', '猫咪', '可爱独立的猫咪'),
    ('REPTILES', '爬行动物', '异域爬宠品种'),
    ('BIRDS', '鸟类', '色彩缤纷的鸣鸟'),
    ('SNACKS', '宠物零食', '美味健康的宠物零食'),
    ('SUPPLIES', '宠物用品', '宠物生活必备用品');

INSERT INTO product (productid, category, name, description, price, status) VALUES
    ('FI-SW-01', 'FISH', '神仙鱼', '淡水神仙鱼，美丽温顺', 18.50, 'ON_SALE'),
    ('FI-SW-02', 'FISH', '虎鲨', '引人注目的淡水鲨鱼', 25.00, 'ON_SALE'),
    ('FI-FW-01', 'FISH', '锦鲤', '日本观赏鲤鱼，好运象征', 35.00, 'ON_SALE'),
    ('FI-FW-02', 'FISH', '金鱼', '经典金鱼，易于饲养', 12.00, 'ON_SALE'),
    ('K9-BD-01', 'DOGS', '斗牛犬', '友善勇敢的犬种', 1500.00, 'ON_SALE'),
    ('K9-BD-02', 'DOGS', '贵宾犬', '聪明低致敏的犬种', 2000.00, 'ON_SALE'),
    ('K9-DL-01', 'DOGS', '斑点犬', '充满活力的斑点犬', 1800.00, 'ON_SALE'),
    ('K9-PO-02', 'DOGS', '金毛寻回犬', '友善忠诚的家庭犬', 2200.00, 'ON_SALE'),
    ('K9-RT-01', 'DOGS', '拉布拉多', '美国最受欢迎的犬种', 2000.00, 'ON_SALE'),
    ('K9-RT-02', 'DOGS', '吉娃娃', '小巧但勇敢的伴侣犬', 1200.00, 'ON_SALE'),
    ('C9-DL-01', 'CATS', '布偶猫', '温柔美丽的布偶猫', 3000.00, 'ON_SALE'),
    ('C9-DL-02', 'CATS', '波斯猫', ' fluffy温顺的长毛猫', 3500.00, 'ON_SALE'),
    ('C9-PO-01', 'CATS', '暹罗猫', '优雅爱叫的猫种', 2800.00, 'ON_SALE'),
    ('C9-PO-02', 'CATS', '缅因猫', '大型友善的猫种', 4000.00, 'ON_SALE'),
    ('RP-LI-02', 'REPTILES', '绿鬣蜥', '大型绿色蜥蜴，热门宠物', 500.00, 'ON_SALE'),
    ('RP-SN-01', 'REPTILES', '响尾蛇', '毒蛇，适合经验丰富的饲养者', 800.00, 'ON_SALE'),
    ('RP-SN-02', 'REPTILES', '球蟒', '温顺易操作的蛇类', 600.00, 'ON_SALE'),
    ('AV-CB-01', 'BIRDS', '玄凤鹦鹉', '友善爱音乐的小鹦鹉', 300.00, 'ON_SALE'),
    ('AV-SB-01', 'BIRDS', '虎皮鹦鹉', '色彩斑斓的小鹦鹉', 150.00, 'ON_SALE'),
    ('AV-FW-01', 'BIRDS', '雀鸟', '小巧活泼的鸣鸟', 100.00, 'ON_SALE'),
    ('SN-001', 'SNACKS', '鸡肉味猫条', '猫咪爱吃的鸡肉味零食条', 15.00, 'ON_SALE'),
    ('SN-002', 'SNACKS', '狗狗磨牙棒', '帮助狗狗清洁牙齿的磨牙棒', 25.00, 'ON_SALE'),
    ('SN-003', 'SNACKS', '冻干鸡肉粒', '纯天然冻干鸡肉零食', 35.00, 'ON_SALE'),
    ('SN-004', 'SNACKS', '猫草种子套装', '新鲜猫草种植套装', 12.00, 'ON_SALE'),
    ('SP-001', 'SUPPLIES', '宠物梳毛刷', '舒适顺毛的宠物梳子', 30.00, 'ON_SALE'),
    ('SP-002', 'SUPPLIES', '猫砂盆', '封闭式除臭猫砂盆', 80.00, 'ON_SALE'),
    ('SP-003', 'SUPPLIES', '狗窝垫子', '柔软舒适的狗窝垫', 60.00, 'ON_SALE'),
    ('SP-004', 'SUPPLIES', '宠物饮水机', '循环过滤宠物饮水机', 120.00, 'ON_SALE'),
    ('SP-005', 'SUPPLIES', '牵引绳套装', '安全舒适的宠物牵引绳', 45.00, 'ON_SALE');

INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1, qty) VALUES
    ('EST-1',  'FI-SW-01', 16.50, 10.00, 1, 'P', 'Large', 100),
    ('EST-2',  'FI-SW-01', 16.50, 10.00, 1, 'P', 'Small', 100),
    ('EST-3',  'FI-SW-02', 18.50, 12.00, 1, 'P', 'Standard', 100),
    ('EST-4',  'FI-FW-01', 18.50, 12.00, 1, 'P', 'Spotted', 100),
    ('EST-5',  'FI-FW-01', 18.50, 12.00, 1, 'P', 'Spotless', 100),
    ('EST-6',  'FI-FW-02', 25.50, 18.00, 1, 'P', 'Adult', 100),
    ('EST-7',  'FI-FW-02', 25.50, 18.00, 1, 'P', 'Young', 100),
    ('EST-8',  'K9-BD-01', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-9',  'K9-BD-02', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-10', 'K9-DL-01', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-11', 'K9-PO-02', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-12', 'K9-RT-01', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-13', 'K9-RT-02', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-14', 'C9-DL-01', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-15', 'C9-DL-02', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-16', 'C9-PO-01', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-17', 'C9-PO-02', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-18', 'RP-LI-02', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-19', 'RP-SN-01', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-20', 'RP-SN-02', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-21', 'AV-CB-01', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-22', 'AV-SB-01', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-23', 'AV-FW-01', 18.50, 12.00, 1, 'P', 'Adult', 100),
    ('EST-24', 'SN-001', 15.00, 8.00, 1, 'P', 'Standard', 200),
    ('EST-25', 'SN-002', 25.00, 15.00, 1, 'P', 'Standard', 150),
    ('EST-26', 'SN-003', 35.00, 20.00, 1, 'P', 'Standard', 180),
    ('EST-27', 'SN-004', 12.00, 6.00, 1, 'P', 'Standard', 100),
    ('EST-28', 'SP-001', 30.00, 18.00, 1, 'P', 'Standard', 100),
    ('EST-29', 'SP-002', 80.00, 45.00, 1, 'P', 'Standard', 50),
    ('EST-30', 'SP-003', 60.00, 35.00, 1, 'P', 'Standard', 60),
    ('EST-31', 'SP-004', 120.00, 70.00, 1, 'P', 'Standard', 40),
    ('EST-32', 'SP-005', 45.00, 25.00, 1, 'P', 'Standard', 80);

INSERT INTO signon (username, password) VALUES ('j2ee', 'j2ee'), ('ACID', 'ACID');

INSERT INTO account (userid, email, firstname, lastname, status, addr1, addr2, city, state, zip, country, phone, role) VALUES
    ('j2ee', 'j2ee@jpetstore.com', 'John', 'Doe', 'OK', '901 San Antonio Road', 'MS 100', 'Palo Alto', 'CA', '94303', 'US', '555-555-5555', 'ADMIN'),
    ('ACID', 'acid@jpetstore.com', 'Jane', 'Smith', 'OK', '100 Main Street', NULL, 'New York', 'NY', '10001', 'US', '555-123-4567', 'USER');

INSERT INTO profile (userid, langpref, favcategory, mylistopt, banneropt) VALUES
    ('j2ee', 'english', 'DOGS', 1, 1),
    ('ACID', 'english', 'CATS', 1, 0);

-- 插入默认轮播图数据
INSERT INTO banner (title, subtitle, image, link, sort_order, active) VALUES
    ('欢迎来到 JPetStore', '您值得信赖的宠物用品电商平台', NULL, '/explore', 1, 1),
    ('新到宠物零食', '美味健康的宠物零食，宠物最爱', NULL, '/explore', 2, 1),
    ('宠物用品大促', '宠物生活用品一站式购齐', NULL, '/explore', 3, 1);

SELECT COUNT(*) AS category_count FROM category;
SELECT COUNT(*) AS product_count FROM product;
SELECT COUNT(*) AS item_count FROM item;
