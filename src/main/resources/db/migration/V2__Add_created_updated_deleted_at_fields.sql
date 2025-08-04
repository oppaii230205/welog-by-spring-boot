ALTER TABLE users
DROP COLUMN active;

ALTER TABLE users
ADD COLUMN created_at timestamptz DEFAULT NOW(),
ADD COLUMN updated_at timestamptz DEFAULT NULL,
ADD COLUMN deleted_at timestamptz DEFAULT NULL;

ALTER TABLE posts
ALTER COLUMN created_at SET DEFAULT NOW(),
ADD COLUMN updated_at timestamptz DEFAULT NULL,
ADD COLUMN deleted_at timestamptz DEFAULT NULL;

ALTER TABLE tags
ADD COLUMN created_at timestamptz DEFAULT NOW(),
ADD COLUMN updated_at timestamptz DEFAULT NULL,
ADD COLUMN deleted_at timestamptz DEFAULT NULL;

ALTER TABLE comments
ALTER COLUMN created_at SET DEFAULT NOW(),
ADD COLUMN updated_at timestamptz DEFAULT NULL,
ADD COLUMN deleted_at timestamptz DEFAULT NULL;

ALTER TABLE roles
ADD COLUMN created_at timestamptz DEFAULT NOW(),
ADD COLUMN updated_at timestamptz DEFAULT NULL,
ADD COLUMN deleted_at timestamptz DEFAULT NULL;