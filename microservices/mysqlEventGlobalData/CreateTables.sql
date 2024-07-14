create table if not exists brief_event
(
    id                     int,
    publisher_full_id      varchar(20),
    publish_datetime       datetime,
    name                   varchar(1024),
    detailed_data_location int
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
