-- Create books table
CREATE TABLE IF NOT EXISTS books (
    id UUID NOT NULL PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    author VARCHAR(200) NOT NULL,
    isbn VARCHAR(20),
    category VARCHAR(50) NOT NULL,
    description TEXT,
    cover_image_url VARCHAR(500),
    total_pages INTEGER,
    estimated_reading_minutes INTEGER,
    language VARCHAR(10) NOT NULL DEFAULT 'en',
    publisher VARCHAR(200),
    publication_year INTEGER,
    added_by_user_id UUID NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    difficulty_level INTEGER,
    external_id VARCHAR(100),
    external_source VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0
);

-- Create book_progress table
CREATE TABLE IF NOT EXISTS book_progress (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL,
    book_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    current_page INTEGER NOT NULL DEFAULT 0,
    progress_percentage INTEGER NOT NULL DEFAULT 0,
    total_sessions_completed INTEGER NOT NULL DEFAULT 0,
    total_reading_time_seconds BIGINT NOT NULL DEFAULT 0,
    started_reading_at TIMESTAMPTZ,
    last_read_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    notes TEXT,
    favorite_quotes TEXT,
    personal_rating INTEGER,
    reading_goal_pages_per_day INTEGER,
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT unique_user_book_progress UNIQUE (user_id, book_id)
);

-- Create book_sessions table
CREATE TABLE IF NOT EXISTS book_sessions (
    id UUID NOT NULL PRIMARY KEY,
    book_id UUID NOT NULL,
    session_id UUID NOT NULL,
    user_id UUID NOT NULL,
    pages_read INTEGER,
    reading_time_seconds INTEGER,
    start_page INTEGER,
    end_page INTEGER,
    session_notes TEXT,
    difficulty_rating INTEGER,
    comprehension_rating INTEGER,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0
);

-- Create indexes for foreign key relationships and frequently queried columns
CREATE INDEX IF NOT EXISTS idx_books_added_by_user_id ON books(added_by_user_id);
CREATE INDEX IF NOT EXISTS idx_books_category ON books(category);
CREATE INDEX IF NOT EXISTS idx_books_is_public ON books(is_public);

CREATE INDEX IF NOT EXISTS idx_book_progress_user_id ON book_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_book_progress_book_id ON book_progress(book_id);
CREATE INDEX IF NOT EXISTS idx_book_progress_status ON book_progress(status);
CREATE INDEX IF NOT EXISTS idx_book_progress_last_read_at ON book_progress(last_read_at);

CREATE INDEX IF NOT EXISTS idx_book_sessions_book_id ON book_sessions(book_id);
CREATE INDEX IF NOT EXISTS idx_book_sessions_session_id ON book_sessions(session_id);
CREATE INDEX IF NOT EXISTS idx_book_sessions_user_id ON book_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_book_sessions_user_book ON book_sessions(user_id, book_id);