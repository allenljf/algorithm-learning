alter table reviews drop constraint reviews_policy_version_check;

alter table reviews add constraint reviews_policy_version_check
    check (policy_version in ('fixed-v1', 'adaptive-v1'));

alter table reviews
    add column previous_interval_days integer,
    add column previous_ease_factor numeric(4, 2),
    add column interval_days integer,
    add column ease_factor numeric(4, 2),
    add column repetitions integer;

alter table reviews add constraint reviews_adaptive_snapshot_check
    check (policy_version <> 'adaptive-v1' or (
        interval_days is not null and interval_days >= 1
        and ease_factor is not null and ease_factor between 1.30 and 3.00
        and repetitions is not null and repetitions >= 0));
