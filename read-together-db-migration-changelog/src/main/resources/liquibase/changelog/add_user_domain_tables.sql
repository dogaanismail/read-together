-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(50) NOT NULL,
    user_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    username VARCHAR(50) UNIQUE,
    profile_picture_url VARCHAR(500),
    bio TEXT,
    reading_streak INTEGER NOT NULL DEFAULT 0,
    total_sessions BIGINT NOT NULL DEFAULT 0,
    total_reading_time_seconds BIGINT NOT NULL DEFAULT 0,
    longest_streak INTEGER NOT NULL DEFAULT 0,
    total_active_days BIGINT NOT NULL DEFAULT 0,
    last_activity_date TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0
);

-- Create reading_preferences table
CREATE TABLE IF NOT EXISTS reading_preferences (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    default_language VARCHAR(50) NOT NULL DEFAULT 'ENGLISH',
    reading_speed VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
    subtitles_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    autoplay BOOLEAN NOT NULL DEFAULT FALSE,
    video_quality VARCHAR(50) NOT NULL DEFAULT 'HIGH',
    font_size VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    theme VARCHAR(50) NOT NULL DEFAULT 'LIGHT',
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0
);

-- Create privacy_settings table
CREATE TABLE IF NOT EXISTS privacy_settings (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    profile_visibility VARCHAR(50) NOT NULL DEFAULT 'PUBLIC',
    show_email BOOLEAN NOT NULL DEFAULT FALSE,
    show_online_status BOOLEAN NOT NULL DEFAULT TRUE,
    allow_messages BOOLEAN NOT NULL DEFAULT TRUE,
    show_reading_sessions BOOLEAN NOT NULL DEFAULT TRUE,
    searchable BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0
);