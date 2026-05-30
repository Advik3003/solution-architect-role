-- Backward-compatible schema evolution for new API version fields.
ALTER TABLE account
ADD COLUMN IF NOT EXISTS account_type VARCHAR(20) DEFAULT 'SAVINGS';
