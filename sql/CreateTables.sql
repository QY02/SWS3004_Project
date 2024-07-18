create database if not exists nus_cloud;

use nus_cloud;

create table if not exists user
(
    id           int primary key auto_increment,
    routing_hash varchar(8)   not null,
    name         varchar(50)  not null,
    password     varchar(150) not null,
    email        varchar(50)  not null unique
) auto_increment = 10000000;

create table if not exists event
(
    id                int primary key auto_increment,
    publisher_full_id varchar(20),
    publish_datetime  datetime      not null,
    name              varchar(1024) not null,
    content    text,
    poster_url varchar(1024)
);

create table if not exists brief_event
(
    id                     int,
    detailed_data_location int,
    primary key (id, detailed_data_location),
    publisher_full_id      varchar(20),
    publish_datetime       datetime,
    name       varchar(1024),
    poster_url varchar(1024)
);

create table if not exists event_session
(
    event_session_id        int primary key auto_increment,
    event_id                int           not null,
    registration_start_time datetime,
    registration_end_time   datetime,
    start_time              datetime,
    end_time                datetime,
    seat_map_id             int,
    venue                   varchar(1024) not null
);

create table if not exists seat_map_template
(
    id   int primary key auto_increment,
    name varchar(128),
    data varchar(5120)
);

create table if not exists seat_map
(
    id   int primary key auto_increment,
    name varchar(128),
    data varchar(5120)
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

create table if not exists seat_template
(
    seat_map_id int         not null,
    seat_id     varchar(10) not null,
    unique (seat_map_id, seat_id),
    type        varchar(10) not null,
    price       int
);

create table if not exists order_record
(
    id                     int primary key auto_increment,
    full_user_id varchar(20) not null,
    event_id               int         not null,
    detailed_data_location int not null,
    event_session_id       int         not null,
    unique (full_user_id, event_id, detailed_data_location, event_session_id),
    seat_id                varchar(10),
    additional_information varchar(10240),
    price                  int,
    submit_time            datetime    not null
) auto_increment = 100001;
