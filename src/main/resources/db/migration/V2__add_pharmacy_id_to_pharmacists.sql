ALTER TABLE pharmacists
    ADD COLUMN pharmacy_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_pharmacists_pharmacy FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id);