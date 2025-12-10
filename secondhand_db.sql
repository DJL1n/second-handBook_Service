create table if not exists orders
(
    ID               int auto_increment
        primary key,
    PRODUCT_ID       int                             not null comment '商品ID',
    BUYER_ID         int                             not null comment '买家ID',
    SELLER_ID        int                             not null comment '卖家ID',
    PRICE            decimal(10, 2)                  not null comment '成交价格 (就是你缺少的这一列)',
    STATUS           varchar(50) default 'WAIT_SHIP' null comment '订单状态',
    SHIPPING_ADDRESS varchar(255)                    null comment '收货地址',
    CREATED_AT       datetime                        null,
    UPDATED_AT       datetime                        null
);

create table if not exists users
(
    user_id  int auto_increment
        primary key,
    username varchar(50)                not null,
    password varchar(100)               not null,
    enabled  tinyint(1)  default 1      null,
    role     varchar(20) default 'USER' null,
    last_active_at DATETIME DEFAULT NULL COMMENT '最后活跃时间',
    constraint username
        unique (username)
);

create table if not exists products
(
    id          int auto_increment
        primary key,
    title       varchar(100)                          not null,
    description text                                  null,
    price       decimal(10, 2)                        not null,
    image_url   varchar(255)                          null,
    seller_id   int                                   not null,
    status      varchar(20) default 'ON_SALE'         null,
    created_at  timestamp   default CURRENT_TIMESTAMP null,
    constraint products_ibfk_1
        foreign key (seller_id) references users (user_id)
);

create table if not exists messages
(
    id          int auto_increment
        primary key,
    sender_id   int                  not null comment '发送者ID',
    receiver_id int                  not null comment '接收者ID',
    product_id  int                  null comment '关联商品ID',
    content     text                 null comment '消息内容',
    created_at  datetime             null comment '发送时间',
    is_read     tinyint(1) default 0 null comment '是否已读'
);



create index seller_id
    on products (seller_id);

INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (1, 'admin', '123456', 1, 'ADMIN');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (2, 'zhangsan', '123456', 1, 'USER');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (3, 'lisi', '123456', 1, 'USER');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (4, 'wangwu', '123456', 1, 'USER');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (5, 'zhaoliu', '123456', 1, 'USER');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (6, 'qianqi', '123456', 1, 'USER');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (7, 'wjs', '1', 1, 'USER');

INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (1, 'Java编程思想 (第4版)', '经典的Java红宝书，95新，稍微有点笔记，不影响阅读。太厚了啃不动，出给有缘人。', 45.00, 'images/java_thinking.jpg', 2, 'ON_SALE', '2025-12-06 20:20:50');
INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (2, '三体全集 (刘慈欣著)', '科幻神作，大刘亲笔签名版（虽然是打印的）。过年回血，含泪转让。', 28.00, null, 3, 'ON_SALE', '2025-12-06 20:20:50');
INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (3, '算法导论 (原书第3版)', '计算机系必备，垫显示器神器。全新未拆封，买来就没打开过。', 65.50, 'images/algo.jpg', 4, 'ON_SALE', '2025-12-06 20:20:50');
INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (4, '活着 - 余华', '看完很致郁，不想留着了。书况良好，无折痕。', 12.00, null, 5, 'ON_SALE', '2025-12-06 20:20:50');
INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (5, 'CSAPP 深入理解计算机系统', '黑皮书，CS专业的圣经。封面有点磨损，内页干净。', 88.00, 'images/csapp.jpg', 6, 'ON_SALE', '2025-12-06 20:20:50');
INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (6, '百年孤独 (马尔克斯)', '人名太长记不住，看不下去。正版，书脊完好。', 20.00, null, 2, 'ON_SALE', '2025-12-06 20:20:50');


INSERT INTO secondhand_db.orders (ID, PRODUCT_ID, BUYER_ID, SELLER_ID, PRICE, STATUS, SHIPPING_ADDRESS, CREATED_AT, UPDATED_AT) VALUES (1, 1, 7, 2, 45.00, 'CANCELLED', '学校图书馆大厅', '2025-12-09 00:58:52', '2025-12-09 03:21:23');
