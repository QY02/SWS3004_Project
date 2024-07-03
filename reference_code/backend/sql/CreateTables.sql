create database if not exists events_center_pre;

use events_center_pre;

create table if not exists user
(
    id                        varchar(8) primary key,
    name                      varchar(50) not null,
    type                      int         not null,
    two_factor_authentication boolean     not null,
    password                  varchar(150) not null,
    email                     varchar(50) not null,
    phone_number              varchar(20),
    department                varchar(50) not null,
    icon_id                   int
);

create table if not exists attachment
(
    id        int primary key auto_increment,
    file_path varchar(1024) not null
);

create table if not exists event
(
    id            int primary key auto_increment,
    publisher_id  varchar(8),
    publish_date  datetime      not null,
    name          varchar(1024) not null,
    content       text,
    type          int           not null,
    status        int           not null,
    highest_price int,
    lowest_price  int,
    event_policy text,
    visible       boolean       not null
);

create table if not exists event_session
(
    event_session_id                int primary key auto_increment,
    event_id                        int           not null,
    registration_required           boolean       not null,
    registration_start_time         datetime,
    registration_end_time           datetime,
    start_time                      datetime,
    end_time                        datetime,
    min_size                        int           not null,
    max_size                        int           not null,
    current_size                    int           not null,
    seat_map_id                     int,
    venue                           varchar(1024) not null,
    location                        varchar(1024),
    additional_information_required varchar(10240),
    visible                         boolean       not null
);

create table if not exists entity_attachment_relation
(
    entity_type        int not null,
    entity_id          int not null,
    attachment_type  int not null,
    attachment_id    int not null,
    primary key (entity_type, entity_id, attachment_type, attachment_id),
    attachment_title varchar(1024)
);

create table if not exists seat_map
(
    id          int primary key auto_increment,
    type        int not null,
    name varchar(128),
    description varchar(1024),
    data        varchar(5120)
);

create table if not exists seat
(
    seat_map_id  int         not null,
    seat_id      varchar(10) not null,
    unique (seat_map_id, seat_id),
    type         varchar(10) not null,
    availability boolean     not null,
    price        int
);

create table if not exists order_record
(
    id                     int primary key auto_increment,
    user_id                varchar(8) not null,
    event_id               int        not null,
    event_session_id       int        not null,
    seat_id                varchar(10),
    additional_information varchar(10240),
    price                  int,
    status                 int        not null,
    submit_time            datetime   not null,
    payment_time           datetime
) auto_increment = 100001;

create table if not exists history
(
    user_id    varchar(8) not null,
    event_id   int        not null,
    visit_time datetime   not null
);

create table if not exists notification
(
    id                      int primary key auto_increment,
    status                  tinyint     not null,
    type                    int     not null,
    notified_user_id        varchar(8)  not null,
    publisher_id            varchar(8)  not null,
    event_session_id        int,
    create_time             datetime    not null,
    notify_time             datetime    not null,
    title                   varchar(1024)   not null,
    content                 text    not null
    );

create table if not exists favorite
(
    user_id  varchar(8) not null,
    event_id int        not null,
    unique (user_id, event_id)
);

create table if not exists comment
(
    id           int primary key auto_increment,
    event_id     int        not null,
    publisher_id varchar(8) not null,
    publish_date datetime   not null,
    content      varchar(10240),
    up_vote      int        not null,
    down_vote    int        not null,
    score double
);

create table if not exists reply
(
    id           int primary key auto_increment,
    comment_id   int        not null,
    publisher_id varchar(8) not null,
    publish_date datetime   not null,
    content      varchar(10240),
    up_vote      int        not null,
    down_vote    int        not null
);

create table if not exists chat_message
(
    id int primary key auto_increment,
    sender_id varchar(8),
    receiver_id varchar(8),
    content varchar(5000),
    send_time datetime
);

create table if not exists user_blog_interaction
(
    user_id    varchar(8) null,
    comment_id int        null,
    vote_type  tinyint(1) null
);

create table user_favorite_type
(
    user_id VARCHAR(8) NOT NULL ,
    event_type INT NOT NULL
);
create table user_interaction
(
    user_id     varchar(8) not null,
    event_id    int        not null,
    update_type int        null,
    rating      int        null
);
