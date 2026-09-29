create table course_categories (
    id uuid primary key,
    slug varchar(100) not null unique check (char_length(btrim(slug)) between 1 and 100),
    display_name varchar(160) not null check (char_length(btrim(display_name)) between 1 and 160),
    sort_order integer not null
);

create table course_lessons (
    id uuid primary key,
    category_id uuid not null references course_categories(id) on delete cascade,
    source_identity varchar(240) not null unique check (char_length(btrim(source_identity)) between 1 and 240),
    source_markdown_path varchar(500) not null,
    english_title varchar(300) not null,
    tags text not null,
    detail text not null check (char_length(detail) > 0),
    source_url varchar(2000) not null,
    source_type varchar(120) not null,
    sort_order integer not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create index course_lessons_category_sort_idx on course_lessons(category_id, sort_order, id);
create index course_lessons_tags_idx on course_lessons using gin (to_tsvector('simple', tags));
create index course_lessons_search_idx on course_lessons using gin (to_tsvector('simple', english_title || ' ' || tags));
create trigger course_lessons_set_updated_at before update on course_lessons for each row execute function set_updated_at();
