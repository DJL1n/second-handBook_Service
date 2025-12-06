create table if not exists users
(
    user_id  int auto_increment
        primary key,
    username varchar(50)                not null,
    password varchar(100)               not null,
    enabled  tinyint(1)  default 1      null,
    role     varchar(20) default 'USER' null,
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

create table if not exists orders
(
    id         int auto_increment
        primary key,
    product_id int                                   not null,
    buyer_id   int                                   not null,
    seller_id  int                                   not null,
    status     varchar(20) default 'COMPLETED'       null,
    created_at timestamp   default CURRENT_TIMESTAMP null,
    constraint orders_ibfk_1
        foreign key (product_id) references products (id),
    constraint orders_ibfk_2
        foreign key (buyer_id) references users (user_id),
    constraint orders_ibfk_3
        foreign key (seller_id) references users (user_id)
);

create index buyer_id
    on orders (buyer_id);

create index product_id
    on orders (product_id);

create index seller_id
    on orders (seller_id);

create index seller_id
    on products (seller_id);

INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (1, 'admin', '123456', 1, 'ADMIN');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (2, 'zhangsan', '123456', 1, 'USER');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (3, 'lisi', '123456', 1, 'USER');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (4, 'wangwu', '123456', 1, 'USER');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (5, 'zhaoliu', '123456', 1, 'USER');
INSERT INTO secondhand_db.users (user_id, username, password, enabled, role) VALUES (6, 'qianqi', '123456', 1, 'USER');


INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (1, 'Java编程思想 (第4版)', '经典的Java红宝书，95新，稍微有点笔记，不影响阅读。太厚了啃不动，出给有缘人。', 45.00, 'images/java_thinking.jpg', 2, 'ON_SALE', '2025-12-06 20:20:50');
INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (2, '三体全集 (刘慈欣著)', '科幻神作，大刘亲笔签名版（虽然是打印的）。过年回血，含泪转让。', 28.00, null, 3, 'ON_SALE', '2025-12-06 20:20:50');
INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (3, '算法导论 (原书第3版)', '计算机系必备，垫显示器神器。全新未拆封，买来就没打开过。', 65.50, 'images/algo.jpg', 4, 'ON_SALE', '2025-12-06 20:20:50');
INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (4, '活着 - 余华', '看完很致郁，不想留着了。书况良好，无折痕。', 12.00, null, 5, 'ON_SALE', '2025-12-06 20:20:50');
INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (5, 'CSAPP 深入理解计算机系统', '黑皮书，CS专业的圣经。封面有点磨损，内页干净。', 88.00, 'images/csapp.jpg', 6, 'ON_SALE', '2025-12-06 20:20:50');
INSERT INTO secondhand_db.products (id, title, description, price, image_url, seller_id, status, created_at) VALUES (6, '百年孤独 (马尔克斯)', '人名太长记不住，看不下去。正版，书脊完好。', 20.00, null, 2, 'ON_SALE', '2025-12-06 20:20:50');
