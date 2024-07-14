insert into brief_event (id, publisher_full_id, publish_datetime, name, detailed_data_location)
VALUES (1, '3976113110000000', '2024-07-08 22:43:51', 'abc', 0);

insert into seat_map_template(id, name, data)
VALUES (1, 'aaa', '{
  "size": {
    "width": 80,
    "height": 60
  },
  "seats": [{"id": "A1", "x": 0, "y": 0, "color": "#0059de"},
    {"id": "A2", "x": 10, "y": 0, "color": "#0059de"},
    {"id": "A3", "x": 20, "y": 0, "color": "#0059de"}
  ]
}');

insert into seat_template(seat_map_id, seat_id, type, price)
VALUES (1, 'A1', 'a', 1);
insert into seat_template(seat_map_id, seat_id, type, price)
VALUES (1, 'A2', 'b', 2);
insert into seat_template(seat_map_id, seat_id, type, price)
VALUES (1, 'A3', 'c', 3);

insert into event(id, publisher_full_id, publish_datetime, name, content)
VALUES (1, '3976113110000000', '2024-07-08 22:43:51', 'abc', 'aaabbbccc');

insert into event_session(event_session_id, event_id, registration_start_time, registration_end_time, start_time,
                          end_time, seat_map_id, venue)
VALUES (1, 1, '2024-07-01 16:38:58', '2024-08-21 16:38:58', '2024-07-01 16:38:58', '2024-09-01 16:38:58', 1, 'aaa');

insert into seat_map(id, name, data)
VALUES (1, 'aaa', '{
  "size": {
    "width": 80,
    "height": 60
  },
  "seats": [{"id": "A1", "x": 0, "y": 0, "color": "#0059de"},
    {"id": "A2", "x": 10, "y": 0, "color": "#0059de"},
    {"id": "A3", "x": 20, "y": 0, "color": "#0059de"}
  ]
}');

insert into seat(seat_map_id, seat_id, type, availability, price)
VALUES (1, 'A1', 'a', true, 1);
insert into seat(seat_map_id, seat_id, type, availability, price)
VALUES (1, 'A2', 'b', true, 2);
insert into seat(seat_map_id, seat_id, type, availability, price)
VALUES (1, 'A3', 'c', true, 3);