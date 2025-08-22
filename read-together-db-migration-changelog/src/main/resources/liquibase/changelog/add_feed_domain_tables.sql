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