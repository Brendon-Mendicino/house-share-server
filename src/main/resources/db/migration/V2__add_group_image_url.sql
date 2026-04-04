ALTER TABLE app_group
    ADD image_url TEXT;

ALTER TABLE app_user
    ALTER COLUMN picture TYPE TEXT USING (picture::TEXT);

CREATE INDEX idx_57bf796ac4e421512a354482e ON app_user (sub);