create table if not exists user_account (
    id bigserial primary key,
    email varchar(255) not null unique,
    display_name varchar(255) not null,
    balance numeric(12,2) not null default 0
);

create table if not exists app_item (
    id bigserial primary key,
    package_name varchar(255) not null unique,
    title varchar(255) not null,
    description text not null,
    price numeric(12,2) not null,
    active boolean not null default true
);

create table if not exists payment_card (
    id bigserial primary key,
    user_id bigint not null references user_account(id),
    masked_number varchar(32) not null,
    holder_name varchar(255) not null,
    expiry_month int not null,
    expiry_year int not null,
    card_token varchar(128) not null,
    active boolean not null default true
);

create table if not exists purchase (
    id bigserial primary key,
    user_id bigint not null references user_account(id),
    app_id bigint not null references app_item(id),
    amount numeric(12,2) not null,
    status varchar(32) not null,
    created_at timestamptz not null
);

create table if not exists installation (
    id bigserial primary key,
    user_id bigint not null references user_account(id),
    app_id bigint not null references app_item(id),
    purchase_id bigint references purchase(id),
    status varchar(32) not null,
    installed_at timestamptz not null
);

insert into app_item (package_name, title, description, price, active)
values
('com.example.notes', 'Notes Pro', 'Приложение для заметок с синхронизацией', 0.00, true),
('com.example.mathkids', 'Math Kids', 'Обучающая игра по математике', 1.99, true),
('com.example.runner', 'Sky Runner', 'Аркадная игра с внутриигровыми уровнями', 3.49, true)
on conflict (package_name) do nothing;
