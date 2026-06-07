ALTER TABLE app_group
    ADD image_url TEXT;

ALTER TABLE app_user
    ALTER COLUMN picture TYPE TEXT USING convert_from(picture, 'UTF8');