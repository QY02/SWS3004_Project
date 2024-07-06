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
