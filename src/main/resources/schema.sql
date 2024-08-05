create table if not exists Taco_Order
(
    id              identity,
    delivery_Name   varchar(50) not null,
    delivery_Street varchar(50) not null,
    delivery_City   varchar(50) not null,
    delivery_State  varchar(2)  not null,
    delivery_Zip    varchar(10) not null,
    cc_number       varchar(16) not null,
    cc_expiration   varchar(5)  not null,
    cc_cvv          varchar(3)  not null,
    placed_at       timestamp   not null
);

create table if not exists Taco
(
    id             identity,
    name           varchar(50) not null,
    taco_order     bigint      not null,
    taco_order_key bigint      not null,
    created_at     timestamp   not null
);

create table if not exists Ingredient_Ref
(
    ingredient varchar(4) not null,
    taco       bigint     not null,
    taco_key   bigint     not null
);

create table if not exists Ingredient
(
    id   varchar(4)  not null primary key,
    name varchar(25) not null,
    type varchar(10) not null
);

create table if not exists Custom_user
(
    id           identity,
    username     varchar(30) not null,
    password     varchar     not null,
    fullname     varchar(30) not null,
    street       varchar(45) not null,
    city         varchar(25) not null,
    state        varchar(25) not null,
    zip          varchar(30) not null,
    phone_number varchar(45) not null
);

alter table Taco
    add foreign key (taco_order) references Taco_Order (id);

alter table Ingredient_Ref
    add foreign key (ingredient) references Ingredient (id);