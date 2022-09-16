create table posts(
	id serial primary key,
	name text,
	description text,
	link varchar(255) NOT NULL UNIQUE,
	created date
)