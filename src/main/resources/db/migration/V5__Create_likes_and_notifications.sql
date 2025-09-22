-- Store likes
CREATE TABLE posts_likes (
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    post_id INT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NULL,
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    PRIMARY KEY (user_id, post_id) -- prevent duplicate likes
);

-- Store notifications
CREATE TABLE notifications (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipient_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE, -- recipient
    sender_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE, -- who triggered
    post_id INT REFERENCES posts(id) ON DELETE CASCADE, -- related post
    type VARCHAR(50) NOT NULL, -- e.g. "LIKE", "COMMENT"
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NULL,
    deleted_at TIMESTAMPTZ DEFAULT NULL
);