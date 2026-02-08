create table processed_event_entity (
    event_id uuid primary key,
    processed_at timestamp not null,
    status varchar(32) not null
);

create index idx_processed_event_status
    on processed_event_entity (status);
