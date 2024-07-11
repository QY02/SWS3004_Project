create table if not exists user
(
    id           int primary key auto_increment,
    routing_hash varchar(8)   not null,
    name         varchar(50)  not null,
    password     varchar(150) not null,
    email        varchar(50)  not null unique
) auto_increment = 10000000;

create table if not exists order_record
(
    id                     int primary key auto_increment,
    full_user_id           varchar(20) not null,
    event_id               int         not null,
    event_session_id       int         not null,
    unique (full_user_id, event_id, event_session_id),
    seat_id                varchar(10),
    additional_information varchar(10240),
    price                  int,
    submit_time            datetime    not null
) auto_increment = 100001;
