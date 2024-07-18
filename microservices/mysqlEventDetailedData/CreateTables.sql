create table if not exists event
(
    id                int primary key auto_increment,
    publisher_full_id varchar(20),
    publish_datetime  datetime      not null,
    name              varchar(1024) not null,
    content    text,
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

create table if not exists seat_map_template
(
    id   int primary key auto_increment,
    name varchar(128),
    data varchar(5120)
);

create table if not exists seat_template
(
    seat_map_id int         not null,
    seat_id     varchar(10) not null,
    unique (seat_map_id, seat_id),
    type        varchar(10) not null,
    price       int
);
