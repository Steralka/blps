alter table user_account
    add column if not exists active boolean not null default true;
