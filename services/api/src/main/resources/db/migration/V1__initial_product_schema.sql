create extension if not exists citext;

create function set_updated_at()
returns trigger
language plpgsql
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

create table users (
    id uuid primary key,
    email citext not null unique check (char_length(email) <= 254),
    password_hash text not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table auth_sessions (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    token_hash bytea not null unique,
    expires_at timestamptz not null,
    revoked_at timestamptz,
    created_at timestamptz not null default now(),
    last_used_at timestamptz
);

create index auth_sessions_user_expires_idx on auth_sessions (user_id, expires_at);

create table problems (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    title varchar(200) not null check (char_length(btrim(title)) between 1 and 200),
    platform varchar(20) not null check (platform in ('leetcode', 'hacker_rank', 'other')),
    external_problem_id varchar(120),
    external_url text,
    difficulty varchar(10) not null check (difficulty in ('easy', 'medium', 'hard')),
    description text check (char_length(description) <= 20000),
    notes text check (char_length(notes) <= 20000),
    key_insight text check (char_length(key_insight) <= 10000),
    time_complexity varchar(200),
    space_complexity varchar(200),
    mistakes text check (char_length(mistakes) <= 20000),
    interview_notes text check (char_length(interview_notes) <= 20000),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    check (external_problem_id is null or char_length(btrim(external_problem_id)) between 1 and 120)
);

create index problems_user_updated_idx on problems (user_id, updated_at desc, id desc);
create index problems_user_difficulty_idx on problems (user_id, difficulty);
create index problems_user_platform_idx on problems (user_id, platform);
create unique index problems_user_platform_external_id_uq
    on problems (user_id, platform, external_problem_id)
    where external_problem_id is not null;
create function problem_search_vector(
    title varchar,
    description text,
    notes text,
    key_insight text,
    mistakes text,
    interview_notes text
)
returns tsvector
language sql
immutable
as $$
    select to_tsvector(
        'simple',
        title || ' ' || coalesce(description, '') || ' ' || coalesce(notes, '') || ' '
            || coalesce(key_insight, '') || ' ' || coalesce(mistakes, '') || ' '
            || coalesce(interview_notes, '')
    );
$$;

create index problems_search_idx on problems using gin (
    problem_search_vector(title, description, notes, key_insight, mistakes, interview_notes)
);

create table solutions (
    id uuid primary key,
    problem_id uuid not null references problems(id) on delete cascade,
    language varchar(20) not null check (language in ('kotlin', 'java', 'python', 'dart')),
    code text not null check (char_length(code) <= 100000),
    explanation text check (char_length(explanation) <= 20000),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    check (char_length(btrim(code)) > 0 or char_length(btrim(coalesce(explanation, ''))) > 0)
);

create index solutions_problem_created_idx on solutions (problem_id, created_at, id);

create table tags (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    name varchar(80) not null check (char_length(btrim(name)) between 1 and 80),
    normalized_name varchar(80) not null check (char_length(normalized_name) between 1 and 80),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (user_id, normalized_name)
);

create index tags_user_name_idx on tags (user_id, name);

create table problem_tags (
    problem_id uuid not null references problems(id) on delete cascade,
    tag_id uuid not null references tags(id) on delete cascade,
    primary key (problem_id, tag_id)
);

create table reviews (
    id uuid primary key,
    problem_id uuid not null references problems(id) on delete cascade,
    confidence smallint not null check (confidence between 0 and 4),
    reviewed_at timestamptz not null,
    next_review_at timestamptz not null,
    notes text check (char_length(notes) <= 10000),
    policy_version varchar(40) not null check (policy_version = 'fixed-v1')
);

create index reviews_problem_reviewed_idx on reviews (problem_id, reviewed_at desc, id desc);
create index reviews_next_problem_idx on reviews (next_review_at, problem_id);

create trigger users_set_updated_at before update on users
for each row execute function set_updated_at();
create trigger problems_set_updated_at before update on problems
for each row execute function set_updated_at();
create trigger solutions_set_updated_at before update on solutions
for each row execute function set_updated_at();
create trigger tags_set_updated_at before update on tags
for each row execute function set_updated_at();
