-- ====================================================================
-- SMART ALARM & DEVICE MANAGEMENT SYSTEM (SUPABASE SQL SCHEMA)
-- ====================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. DEVICES TABLE
CREATE TABLE IF NOT EXISTS public.devices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id VARCHAR(100) UNIQUE NOT NULL, -- e.g. "DEVICE-A1B2C3" or Android ID
    device_name VARCHAR(150),
    manufacturer VARCHAR(100),
    model VARCHAR(100),
    android_version VARCHAR(50),
    app_version VARCHAR(50),
    battery INTEGER DEFAULT 100,
    is_charging BOOLEAN DEFAULT FALSE,
    network_status VARCHAR(50) DEFAULT 'Offline', -- 'Wi-Fi', 'Cellular', 'Offline'
    status VARCHAR(50) DEFAULT 'online', -- 'online', 'offline'
    last_seen TIMESTAMPTZ DEFAULT NOW(),
    registered_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Index for quick device lookup
CREATE INDEX IF NOT EXISTS idx_devices_device_id ON public.devices (device_id);
CREATE INDEX IF NOT EXISTS idx_devices_last_seen ON public.devices (last_seen);

-- 2. PERMISSIONS TABLE
CREATE TABLE IF NOT EXISTS public.permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id VARCHAR(100) NOT NULL REFERENCES public.devices(device_id) ON DELETE CASCADE,
    sms BOOLEAN DEFAULT FALSE,
    call_log BOOLEAN DEFAULT FALSE,
    media BOOLEAN DEFAULT FALSE,
    notifications BOOLEAN DEFAULT FALSE,
    exact_alarm BOOLEAN DEFAULT FALSE,
    camera BOOLEAN DEFAULT FALSE,
    battery_optimization_ignored BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT uq_permissions_device UNIQUE (device_id)
);

-- 3. ALARMS TABLE (Two-way synchronization)
CREATE TABLE IF NOT EXISTS public.alarms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id VARCHAR(100) NOT NULL REFERENCES public.devices(device_id) ON DELETE CASCADE,
    remote_id BIGINT, -- Local Room DB ID
    time VARCHAR(10) NOT NULL, -- "07:30"
    label VARCHAR(150) DEFAULT 'Budilnik',
    is_enabled BOOLEAN DEFAULT TRUE,
    repeat_days VARCHAR(100) DEFAULT '', -- "MONDAY,TUESDAY"
    difficulty VARCHAR(50) DEFAULT 'MEDIUM',
    question_count INTEGER DEFAULT 3,
    max_volume BOOLEAN DEFAULT TRUE,
    flashlight BOOLEAN DEFAULT TRUE,
    vibration BOOLEAN DEFAULT TRUE,
    snooze_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_alarms_device_id ON public.alarms (device_id);

-- 4. SMS RECORDS TABLE
CREATE TABLE IF NOT EXISTS public.sms_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id VARCHAR(100) NOT NULL REFERENCES public.devices(device_id) ON DELETE CASCADE,
    local_id BIGINT,
    address VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'INCOMING', -- 'INCOMING', 'OUTGOING'
    timestamp BIGINT NOT NULL,
    formatted_date TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_sms_device_id ON public.sms_records (device_id);
CREATE INDEX IF NOT EXISTS idx_sms_timestamp ON public.sms_records (timestamp DESC);

-- 5. CALL RECORDS TABLE
CREATE TABLE IF NOT EXISTS public.call_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id VARCHAR(100) NOT NULL REFERENCES public.devices(device_id) ON DELETE CASCADE,
    local_id BIGINT,
    number VARCHAR(100) NOT NULL,
    name VARCHAR(150),
    type VARCHAR(50) DEFAULT 'INCOMING', -- 'INCOMING', 'OUTGOING', 'MISSED', 'REJECTED'
    duration INTEGER DEFAULT 0, -- seconds
    timestamp BIGINT NOT NULL,
    formatted_date TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_calls_device_id ON public.call_records (device_id);
CREATE INDEX IF NOT EXISTS idx_calls_timestamp ON public.call_records (timestamp DESC);

-- 6. MEDIA RECORDS TABLE (Gallery metadata)
CREATE TABLE IF NOT EXISTS public.media_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id VARCHAR(100) NOT NULL REFERENCES public.devices(device_id) ON DELETE CASCADE,
    local_id BIGINT,
    file_name VARCHAR(255) NOT NULL,
    file_path TEXT,
    media_type VARCHAR(100) DEFAULT 'image/jpeg',
    size BIGINT DEFAULT 0, -- bytes
    date_added BIGINT,
    thumbnail_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_media_device_id ON public.media_records (device_id);

-- 7. ACTIVITY LOGS TABLE (Audit trail)
CREATE TABLE IF NOT EXISTS public.activity_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id VARCHAR(100) NOT NULL REFERENCES public.devices(device_id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL, -- 'DEVICE_CONNECTED', 'PERMISSION_CHANGED', 'ALARM_DISMISSED', 'ALARM_TRIGGERED', 'SYNC_SUCCESS'
    event_data JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_activity_device_id ON public.activity_logs (device_id);
CREATE INDEX IF NOT EXISTS idx_activity_created_at ON public.activity_logs (created_at DESC);

-- ====================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ====================================================================
ALTER TABLE public.devices ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.permissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.alarms ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.sms_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.call_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.media_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.activity_logs ENABLE ROW LEVEL SECURITY;

-- Allow public / anon read and upsert (with anon key for Android client and Admin Dashboard)
CREATE POLICY "Public full access to devices" ON public.devices FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Public full access to permissions" ON public.permissions FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Public full access to alarms" ON public.alarms FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Public full access to sms_records" ON public.sms_records FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Public full access to call_records" ON public.call_records FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Public full access to media_records" ON public.media_records FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Public full access to activity_logs" ON public.activity_logs FOR ALL USING (true) WITH CHECK (true);

-- Enable Realtime
ALTER PUBLICATION supabase_realtime ADD TABLE public.devices;
ALTER PUBLICATION supabase_realtime ADD TABLE public.permissions;
ALTER PUBLICATION supabase_realtime ADD TABLE public.alarms;
ALTER PUBLICATION supabase_realtime ADD TABLE public.sms_records;
ALTER PUBLICATION supabase_realtime ADD TABLE public.call_records;
ALTER PUBLICATION supabase_realtime ADD TABLE public.activity_logs;
