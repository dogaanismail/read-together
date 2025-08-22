-- Create feed_items table
CREATE TABLE IF NOT EXISTS feed_items (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    reference_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    media_url VARCHAR(500),
    thumbnail_url VARCHAR(500),
    is_public BOOLEAN DEFAULT TRUE,
    view_count BIGINT DEFAULT 0,
    like_count BIGINT DEFAULT 0,
    comment_count BIGINT DEFAULT 0,
    metadata TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0
);

-- Create feed_likes table
CREATE TABLE IF NOT EXISTS feed_likes (
    id UUID NOT NULL PRIMARY KEY,
    feed_item_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_feed_likes_feed_item_user UNIQUE (feed_item_id, user_id)
);

-- Create indexes for feed_likes table
CREATE INDEX IF NOT EXISTS idx_feed_likes_feed_item_id ON feed_likes (feed_item_id);