create table if not exists attachment
(
    id        int auto_increment
        primary key,
    file_path varchar(1024) not null
);

create table if not exists chat_message
(
    id          int auto_increment
        primary key,
    sender_id   varchar(8)    null,
    receiver_id varchar(8)    null,
    content     varchar(5000) null,
    send_time   datetime      null,
    has_read    tinyint(1)    null
);

create table if not exists comment
(
    id           int auto_increment
        primary key,
    event_id     int            not null,
    publisher_id varchar(8)     not null,
    publish_date datetime       not null,
    content      varchar(10240) null,
    up_vote      int            null,
    down_vote    int            null,
    score        double         null,
    type         int            null,
    title        text           null,
    media_type   tinyint(1)     null
);

create table if not exists entity_attachment_relation
(
    entity_type      int           not null,
    entity_id        int           not null,
    attachment_type  int           not null,
    attachment_id    int           not null,
    attachment_title varchar(1024) null,
    primary key (entity_type, entity_id, attachment_type, attachment_id)
);

create table if not exists event
(
    id            int auto_increment
        primary key,
    publisher_id  varchar(8)    null,
    publish_date  datetime      not null,
    name          varchar(1024) not null,
    content       text          null,
    type          int           not null,
    status        int           not null,
    highest_price int           null,
    lowest_price  int           null,
    event_policy  text          null,
    visible       tinyint(1)    not null,
    audit_record  text          null
);

create table if not exists event_session
(
    event_session_id                int auto_increment
        primary key,
    event_id                        int            not null,
    registration_required           tinyint(1)     not null,
    registration_start_time         datetime       null,
    registration_end_time           datetime       null,
    start_time                      datetime       null,
    end_time                        datetime       null,
    min_size                        int            not null,
    max_size                        int            not null,
    current_size                    int            not null,
    seat_map_id                     int            null,
    venue                           varchar(1024)  not null,
    location                        varchar(1024)  null,
    additional_information_required varchar(10240) null,
    visible                         tinyint(1)     not null
);

create table if not exists favorite
(
    user_id  varchar(8) not null,
    event_id int        not null,
    constraint user_id
        unique (user_id, event_id)
);

create table if not exists history
(
    user_id    varchar(8) not null,
    event_id   int        not null,
    visit_time datetime   not null
);

create table if not exists notification
(
    id               int auto_increment
        primary key,
    status           tinyint       not null,
    type             int           not null,
    notified_user_id varchar(8)    not null,
    publisher_id     varchar(8)    not null,
    event_session_id int           null,
    create_time      datetime      not null,
    notify_time      datetime      not null,
    title            varchar(1024) not null,
    content          text          not null
);

create table if not exists order_record
(
    id                     int auto_increment
        primary key,
    user_id                varchar(8)     not null,
    event_id               int            not null,
    event_session_id       int            not null,
    seat_id                varchar(10)    null,
    additional_information varchar(10240) null,
    price                  int            null,
    status                 int            not null,
    submit_time            datetime       not null,
    payment_time           datetime       null
);

create table if not exists reply
(
    id           int auto_increment
        primary key,
    comment_id   int            not null,
    publisher_id varchar(8)     not null,
    publish_date datetime       not null,
    content      varchar(10240) null,
    up_vote      int            not null,
    down_vote    int            not null
);

create table if not exists seat
(
    seat_map_id  int         not null,
    seat_id      varchar(10) not null,
    type         varchar(10) not null,
    availability tinyint(1)  not null,
    price        int         null,
    constraint seat_map_id
        unique (seat_map_id, seat_id)
);

create table if not exists seat_map
(
    id          int auto_increment
        primary key,
    type        int           not null,
    name        varchar(128)  null,
    description varchar(1024) null,
    data        varchar(5120) null
);

create table if not exists user
(
    id                        varchar(8)   not null
        primary key,
    name                      varchar(50)  not null,
    type                      int          not null,
    two_factor_authentication tinyint(1)   not null,
    password                  varchar(150) not null,
    email                     varchar(50)  not null,
    phone_number              varchar(20)  null,
    department                varchar(50)  not null,
    icon_id                   int          null
);

create table if not exists user_blog_interaction
(
    user_id    varchar(8) null,
    comment_id int        null,
    vote_type  tinyint(1) null
);

create table if not exists user_favorite_type
(
    user_id    varchar(8) not null,
    event_type int        not null
);

create table if not exists user_interaction
(
    user_id     varchar(8) not null,
    event_id    int        not null,
    update_type int        null,
    rating      int        null
);


