-- ============================================
-- JPetStore 数据库建表脚本
-- 适用于 MySQL 8.0
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS jpetstore DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
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

-- ============================================
-- 2. 创建表
-- ============================================

-- 账户表
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

-- 登录信息表
CREATE TABLE signon (
                        username VARCHAR(25) NOT NULL,
                        password VARCHAR(25) NOT NULL,
                        PRIMARY KEY (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户资料表
CREATE TABLE profile (
                         userid      VARCHAR(80) NOT NULL,
                         langpref    VARCHAR(80) NOT NULL,
                         favcategory VARCHAR(30) NULL,
                         mylistopt   TINYINT(1)  NULL,
                         banneropt   TINYINT(1)  NULL,
                         PRIMARY KEY (userid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分类表
CREATE TABLE category (
                          catid       VARCHAR(10) NOT NULL,
                          name        VARCHAR(80) NULL,
                          description VARCHAR(255) NULL,
                          PRIMARY KEY (catid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 产品表
CREATE TABLE product (
                         productid   VARCHAR(10) NOT NULL,
                         category    VARCHAR(10) NOT NULL,
                         name        VARCHAR(80) NULL,
                         description VARCHAR(255) NULL,
                         image       VARCHAR(255) NULL,
                         PRIMARY KEY (productid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品项表
CREATE TABLE item (
                      itemid    VARCHAR(10)    NOT NULL,
                      productid VARCHAR(10)    NOT NULL,
                      listprice DECIMAL(10, 2) NULL,
                      unitcost  DECIMAL(10, 2) NULL,
                      supplier  INT            NULL,
                      status    VARCHAR(2)     NULL,
                      attr1     VARCHAR(80)    NULL,
                      attr2     VARCHAR(80)    NULL,
                      attr3     VARCHAR(80)    NULL,
                      attr4     VARCHAR(80)    NULL,
                      attr5     VARCHAR(80)    NULL,
                      qty       INT            NULL DEFAULT 0,
                      PRIMARY KEY (itemid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
CREATE TABLE orders (
                        orderid        INT            NOT NULL AUTO_INCREMENT,
                        userid         VARCHAR(80)    NOT NULL,
                        orderdate      DATETIME       NOT NULL,
                        shipaddr1      VARCHAR(80)    NOT NULL,
                        shipaddr2      VARCHAR(40)    NULL,
                        shipcity       VARCHAR(80)    NOT NULL,
                        shipstate      VARCHAR(80)    NOT NULL,
                        shipzip        VARCHAR(20)    NOT NULL,
                        shipcountry    VARCHAR(20)    NOT NULL,
                        billaddr1      VARCHAR(80)    NOT NULL,
                        billaddr2      VARCHAR(40)    NULL,
                        billcity       VARCHAR(80)    NOT NULL,
                        billstate      VARCHAR(80)    NOT NULL,
                        billzip        VARCHAR(20)    NOT NULL,
                        billcountry    VARCHAR(20)    NOT NULL,
                        courier        VARCHAR(80)    NOT NULL,
                        totalprice     DECIMAL(10, 2) NOT NULL,
                        billtofirstname VARCHAR(80)   NOT NULL,
                        billtolastname  VARCHAR(80)   NOT NULL,
                        shiptofirstname VARCHAR(80)   NOT NULL,
                        shiptolastname  VARCHAR(80)   NOT NULL,
                        creditcard     VARCHAR(40)    NOT NULL,
                        exprdate       VARCHAR(7)     NOT NULL,
                        cardtype       VARCHAR(80)    NOT NULL,
                        locale         VARCHAR(80)    NOT NULL,
                        status         VARCHAR(2)     NULL DEFAULT 'P',
                        PRIMARY KEY (orderid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单项表
CREATE TABLE lineitem (
                          orderid  INT            NOT NULL,
                          linenum  INT            NOT NULL,
                          itemid   VARCHAR(10)    NOT NULL,
                          quantity INT            NOT NULL,
                          unitprice DECIMAL(10, 2) NOT NULL,
                          PRIMARY KEY (orderid, linenum)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单状态表
CREATE TABLE orderstatus (
                             orderid   INT      NOT NULL,
                             linenum   INT      NOT NULL,
                             timestamp DATETIME NOT NULL,
                             status    VARCHAR(2) NOT NULL,
                             PRIMARY KEY (orderid, linenum, timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 3. 插入测试数据
-- ============================================

-- 插入分类数据
INSERT INTO category (catid, name, description) VALUES
                                                    ('FISH', 'Fish', 'Various fish species for your aquarium'),
                                                    ('DOGS', 'Dogs', 'Different dog breeds as loyal companions'),
                                                    ('CATS', 'Cats', 'Cute and independent feline friends'),
                                                    ('REPTILES', 'Reptiles', 'Exotic reptile species'),
                                                    ('BIRDS', 'Birds', 'Colorful and singing bird species');

-- 插入产品数据
INSERT INTO product (productid, category, name, description, image) VALUES
                                                                        ('FI-SW-01', 'FISH', 'Angelfish', 'Freshwater angelfish, beautiful and peaceful', 'fish1.jpg'),
                                                                        ('FI-SW-02', 'FISH', 'Tiger Shark', 'A striking freshwater shark species', 'fish2.jpg'),
                                                                        ('FI-FW-01', 'FISH', 'Koi', 'Japanese ornamental carp, symbol of good luck', 'fish3.jpg'),
                                                                        ('FI-FW-02', 'FISH', 'Goldfish', 'Classic goldfish, easy to care for', 'fish4.jpg'),
                                                                        ('K9-BD-01', 'DOGS', 'Bulldog', 'Friendly and courageous breed', 'dog1.jpg'),
                                                                        ('K9-BD-02', 'DOGS', 'Poodle', 'Intelligent and hypoallergenic breed', 'dog2.jpg'),
                                                                        ('K9-DL-01', 'DOGS', 'Dalmatian', 'Energetic spotted breed', 'dog3.jpg'),
                                                                        ('K9-PO-02', 'DOGS', 'Golden Retriever', 'Friendly and devoted family dog', 'dog4.jpg'),
                                                                        ('K9-RT-01', 'DOGS', 'Labrador Retriever', 'Most popular dog breed in America', 'dog5.jpg'),
                                                                        ('K9-RT-02', 'DOGS', 'Chihuahua', 'Tiny but mighty companion dog', 'dog6.jpg'),
                                                                        ('C9-DL-01', 'CATS', 'Amazon Parrot', 'Colorful and talkative bird', 'cat1.jpg'),
                                                                        ('C9-DL-02', 'CATS', 'Persian Cat', 'Fluffy and gentle long-haired cat', 'cat2.jpg'),
                                                                        ('C9-PO-01', 'CATS', 'Siamese Cat', 'Elegant and vocal breed', 'cat3.jpg'),
                                                                        ('C9-PO-02', 'CATS', 'Maine Coon', 'Large and friendly breed', 'cat4.jpg'),
                                                                        ('RP-LI-02', 'REPTILES', 'Iguana', 'Large green lizard, popular pet', 'reptile1.jpg'),
                                                                        ('RP-SN-01', 'REPTILES', 'Rattlesnake', 'Venomous snake, for experienced keepers', 'reptile2.jpg'),
                                                                        ('RP-SN-02', 'REPTILES', 'Ball Python', 'Docile and easy to handle snake', 'reptile3.jpg'),
                                                                        ('AV-CB-01', 'BIRDS', 'Cockatiel', 'Friendly and musical small parrot', 'bird1.jpg'),
                                                                        ('AV-SB-01', 'BIRDS', 'Budgerigar', 'Small colorful parakeet', 'bird2.jpg'),
                                                                        ('AV-FW-01', 'BIRDS', 'Finch', 'Small and active songbird', 'bird3.jpg');

-- 插入商品项数据
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1, attr2, attr3, attr4, attr5, qty) VALUES
                                                                                                                        ('EST-1',  'FI-SW-01', 16.50, 10.00, 1, 'P', 'Large', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-2',  'FI-SW-01', 16.50, 10.00, 1, 'P', 'Small', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-3',  'FI-SW-02', 18.50, 12.00, 1, 'P', 'Toothless', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-4',  'FI-FW-01', 18.50, 12.00, 1, 'P', 'Spotted', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-5',  'FI-FW-01', 18.50, 12.00, 1, 'P', 'Spotless', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-6',  'FI-FW-02', 25.50, 18.00, 1, 'P', 'Male Adult', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-7',  'FI-FW-02', 25.50, 18.00, 1, 'P', 'Female Adult', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-8',  'FI-FW-02', 25.50, 18.00, 1, 'P', 'Male Puppy', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-9',  'K9-BD-01', 18.50, 12.00, 1, 'P', 'Male Adult', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-10', 'K9-BD-01', 18.50, 12.00, 1, 'P', 'Female Adult', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-11', 'K9-BD-01', 18.50, 12.00, 1, 'P', 'Male Puppy', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-12', 'K9-BD-01', 18.50, 12.00, 1, 'P', 'Female Puppy', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-13', 'K9-BD-02', 18.50, 12.00, 1, 'P', 'Male Adult', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-14', 'K9-BD-02', 18.50, 12.00, 1, 'P', 'Female Adult', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-15', 'K9-BD-02', 18.50, 12.00, 1, 'P', 'Male Puppy', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-16', 'K9-BD-02', 18.50, 12.00, 1, 'P', 'Female Puppy', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-17', 'K9-DL-01', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-18', 'K9-DL-01', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-19', 'K9-DL-01', 18.50, 12.00, 1, 'P', 'Puppy Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-20', 'K9-DL-01', 18.50, 12.00, 1, 'P', 'Puppy Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-21', 'K9-PO-02', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-22', 'K9-PO-02', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-23', 'K9-PO-02', 18.50, 12.00, 1, 'P', 'Puppy Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-24', 'K9-PO-02', 18.50, 12.00, 1, 'P', 'Puppy Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-25', 'K9-RT-01', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-26', 'K9-RT-01', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-27', 'K9-RT-01', 18.50, 12.00, 1, 'P', 'Puppy Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-28', 'K9-RT-01', 18.50, 12.00, 1, 'P', 'Puppy Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-29', 'K9-RT-02', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-30', 'K9-RT-02', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-31', 'K9-RT-02', 18.50, 12.00, 1, 'P', 'Puppy Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-32', 'K9-RT-02', 18.50, 12.00, 1, 'P', 'Puppy Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-33', 'C9-DL-01', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-34', 'C9-DL-01', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-35', 'C9-DL-01', 18.50, 12.00, 1, 'P', 'Puppy Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-36', 'C9-DL-01', 18.50, 12.00, 1, 'P', 'Puppy Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-37', 'C9-DL-02', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-38', 'C9-DL-02', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-39', 'C9-DL-02', 18.50, 12.00, 1, 'P', 'Puppy Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-40', 'C9-DL-02', 18.50, 12.00, 1, 'P', 'Puppy Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-41', 'C9-PO-01', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-42', 'C9-PO-01', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-43', 'C9-PO-01', 18.50, 12.00, 1, 'P', 'Puppy Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-44', 'C9-PO-01', 18.50, 12.00, 1, 'P', 'Puppy Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-45', 'C9-PO-02', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-46', 'C9-PO-02', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-47', 'C9-PO-02', 18.50, 12.00, 1, 'P', 'Puppy Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-48', 'C9-PO-02', 18.50, 12.00, 1, 'P', 'Puppy Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-49', 'RP-LI-02', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-50', 'RP-LI-02', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-51', 'RP-SN-01', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-52', 'RP-SN-01', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-53', 'RP-SN-02', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-54', 'RP-SN-02', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-55', 'AV-CB-01', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-56', 'AV-CB-01', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-57', 'AV-SB-01', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-58', 'AV-SB-01', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-59', 'AV-FW-01', 18.50, 12.00, 1, 'P', 'Adult Male', NULL, NULL, NULL, NULL, 10000),
                                                                                                                        ('EST-60', 'AV-FW-01', 18.50, 12.00, 1, 'P', 'Adult Female', NULL, NULL, NULL, NULL, 10000);

-- 插入用户登录信息
INSERT INTO signon (username, password) VALUES
                                            ('j2ee', 'j2ee'),
                                            ('ACID', 'ACID'),
                                            ('admin', 'admin');

-- 插入用户账户
INSERT INTO account (userid, email, firstname, lastname, status, addr1, addr2, city, state, zip, country, phone, role) VALUES
                                                                                                                     ('j2ee', 'j2ee@jpetstore.com', 'John', 'Doe', 'OK', '901 San Antonio Road', 'MS 100', 'Palo Alto', 'CA', '94303', 'US', '555-555-5555', 'USER'),
                                                                                                                     ('ACID', 'acid@jpetstore.com', 'Jane', 'Smith', 'OK', '100 Main Street', NULL, 'New York', 'NY', '10001', 'US', '555-123-4567', 'USER'),
                                                                                                                     ('admin', 'admin@jpetstore.com', 'Admin', 'Admin', 'OK', '1 Admin Street', NULL, 'Beijing', 'Beijing', '100000', 'CN', '010-12345678', 'ADMIN');

-- 插入用户资料
INSERT INTO profile (userid, langpref, favcategory, mylistopt, banneropt) VALUES
                                                                              ('j2ee', 'english', 'DOGS', 1, 1),
                                                                              ('ACID', 'english', 'CATS', 1, 0),
                                                                              ('admin', 'english', 'DOGS', 1, 1);

-- ============================================
-- 4. 验证数据
-- ============================================
SELECT 'Database setup complete!' AS message;
SELECT COUNT(*) AS category_count FROM category;
SELECT COUNT(*) AS product_count FROM product;
SELECT COUNT(*) AS item_count FROM item;
SELECT COUNT(*) AS account_count FROM account;