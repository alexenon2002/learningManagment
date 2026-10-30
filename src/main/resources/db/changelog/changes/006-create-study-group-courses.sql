--liquibase formatted SQL
--changeset student:006-create-study-group-courses

CREATE TABLE public.study_group_courses
(
    study_group_id BIGINT NOT NULL REFERENCES public.study_groups (id),
    course_id      BIGINT NOT NULL REFERENCES public.courses (id),
    PRIMARY KEY (study_group_id, course_id)
);