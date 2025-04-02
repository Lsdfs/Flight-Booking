create table user(
    user_id BIGINT primary key,
    last_name varchar(60) CHARACTER SET utf8mb4 NOT NULL,
    first_name varchar(60) CHARACTER SET utf8mb4 NOT NULL,
    gender varchar(6) NOT NULL,
    email varchar(80) NOT NULL,
    date_of_birth date NOT NULL,
    phone_number numeric(12) NOT NULL,
    address varchar(100) CHARACTER SET utf8mb4,
    passport_number varchar(20),
    citizen_id varchar(15),
    username varchar(50),
    password varchar(500),
    enabled boolean
 );
 create table coflight_member(
	user_id BIGINT primary key,
    last_name varchar(60) CHARACTER SET utf8mb4 NOT NULL,
    first_name varchar(60) CHARACTER SET utf8mb4 NOT NULL,
    gender varchar(6) NOT NULL,
    email varchar(80) NOT NULL,
    date_of_birth date NOT NULL,
    phone_number numeric(12) NOT NULL,
    passport_number varchar(20),
    citizen_id varchar(15),
    constraint foreign key(user_id) references user(user_id)
 );