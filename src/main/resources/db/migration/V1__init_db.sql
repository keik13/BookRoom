create table room
(
	id bigint primary key GENERATED ALWAYS AS IDENTITY,
	name varchar(50),
	seat_count integer not null
);

create table book
(
	id bigint primary key GENERATED ALWAYS AS IDENTITY,
	room_id bigint not null,
	employee varchar(50),
	begin_at timestamptz not null,
    end_at timestamptz not null,

    constraint book_to_room
    foreign key (room_id)
    references room(id)
    on delete cascade
);

create index room_id_index on book(room_id);

INSERT INTO room (name,seat_count) VALUES ('first', 1);
INSERT INTO room (name,seat_count) VALUES ('second', 2);
INSERT INTO room (name,seat_count) VALUES ('third', 3);