-- Create reading_room table
CREATE TABLE IF NOT EXISTS reading_room (
    id UUID NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    max_participants INTEGER DEFAULT 12,
    is_public BOOLEAN DEFAULT TRUE,
    room_code VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) DEFAULT 'WAITING',
    scheduled_start_time TIMESTAMP,
    actual_start_time TIMESTAMP,
    end_time TIMESTAMP,
    host_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0
);

-- Create reading_room_participant table
CREATE TABLE IF NOT EXISTS reading_room_participant (
    id UUID NOT NULL PRIMARY KEY,
    reading_room_id UUID NOT NULL,
    user_id UUID NOT NULL,
    status VARCHAR(50) DEFAULT 'JOINED',
    joined_at TIMESTAMP,
    left_at TIMESTAMP,
    is_muted BOOLEAN DEFAULT FALSE,
    is_video_enabled BOOLEAN DEFAULT TRUE,
    is_speaking BOOLEAN DEFAULT FALSE,
    approval_status VARCHAR(50) DEFAULT 'APPROVED',
    approved_by UUID,
    approved_at TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0
);

-- Create reading_room_settings table
CREATE TABLE IF NOT EXISTS reading_room_settings (
    id UUID NOT NULL PRIMARY KEY,
    reading_room_id UUID NOT NULL,
    is_public BOOLEAN DEFAULT TRUE,
    password VARCHAR(255),
    require_host_approval BOOLEAN DEFAULT FALSE,
    enable_video BOOLEAN DEFAULT TRUE,
    enable_audio BOOLEAN DEFAULT TRUE,
    enable_chat BOOLEAN DEFAULT TRUE,
    allow_recording BOOLEAN DEFAULT FALSE,
    auto_mute_new_joiners BOOLEAN DEFAULT TRUE,
    room_volume INTEGER DEFAULT 80,
    enable_live_transcription BOOLEAN DEFAULT FALSE,
    transcription_language VARCHAR(50) DEFAULT 'ENGLISH',
    enable_speaker_identification BOOLEAN DEFAULT TRUE,
    downloadable_transcripts BOOLEAN DEFAULT TRUE,
    pronunciation_help BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0
);

-- Create reading_room_invitation table
CREATE TABLE IF NOT EXISTS reading_room_invitation (
    id UUID NOT NULL PRIMARY KEY,
    reading_room_id UUID NOT NULL,
    invited_by UUID NOT NULL,
    invited_user_id UUID,
    invited_email VARCHAR(255),
    invitation_token VARCHAR(255) NOT NULL UNIQUE,
    invitation_type VARCHAR(50),
    status VARCHAR(50) DEFAULT 'PENDING',
    expires_at TIMESTAMP,
    accepted_at TIMESTAMP,
    declined_at TIMESTAMP,
    message TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255) NOT NULL DEFAULT 'anonymousUser',
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(255),
    deleted_reason VARCHAR(255),
    version SMALLINT NOT NULL DEFAULT 0
);