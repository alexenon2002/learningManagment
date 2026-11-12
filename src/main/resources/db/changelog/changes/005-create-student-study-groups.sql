--liquibase formatted SQL
--changeset student:005-create-student-study-groups

CREATE TABLE public.student_study_groups
(
    student_id     BIGINT NOT NULL REFERENCES public.students (id),
    study_group_id BIGINT NOT NULL REFERENCES public.study_groups (id),
    PRIMARY KEY (student_id, study_group_id)
);