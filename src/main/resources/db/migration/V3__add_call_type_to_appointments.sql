ALTER TABLE appointments
    ADD COLUMN call_type varchar(20) NOT NULL DEFAULT 'VOICE';