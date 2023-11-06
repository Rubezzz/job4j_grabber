CREATE SCHEMA grabber;

create table grabber.post (
    id serial primary key,
    name text,
	text text,
	link text unique,
    created timestamp
);