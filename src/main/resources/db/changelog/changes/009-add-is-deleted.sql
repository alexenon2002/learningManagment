--Liquibase formatted SQL
--changeset student:009-add-is-deleted

ALTER TABLE public.courses
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE public.courses
SET is_deleted = TRUE
WHERE deleted_at IS NOT NULL;

ALTER TABLE public.study_groups
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE public.study_groups
SET is_deleted = TRUE
WHERE deleted_at IS NOT NULL;

ALTER TABLE public.teachers
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE public.teachers
SET is_deleted = TRUE
WHERE deleted_at IS NOT NULL;

ALTER TABLE public.students
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE public.students
SET is_deleted = TRUE
WHERE deleted_at IS NOT NULL;

ALTER TABLE public.schedules
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE public.schedules
SET is_deleted = TRUE
WHERE deleted_at IS NOT NULL;
