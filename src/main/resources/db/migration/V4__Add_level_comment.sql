ALTER TABLE comments
ADD COLUMN parent_id int DEFAULT NULL,
ADD COLUMN level int DEFAULT 1,
ADD CONSTRAINT fk_comments_parent
    FOREIGN KEY (parent_id)
    REFERENCES comments(id)
    ON DELETE CASCADE;