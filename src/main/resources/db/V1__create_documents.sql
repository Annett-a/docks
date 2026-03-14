CREATE TABLE IF NOT EXISTS documents (
 id UUID PRIMARY KEY,
 doc_type VARCHAR(32) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
 user_id UUID NOT NULL,
 user_fio TEXT NOT NULL,
 card_number CHAR(16) NOT NULL,
 CONSTRAINT documents_doc_type_chk
     CHECK (doc_type IN ('CARD_OPENED','CARD_CLOSED','TRANSFER_RECEIPT')),
 CONSTRAINT documents_card_number_digits
     CHECK (card_number ~ '^[0-9]{16}$')
);

CREATE INDEX IF NOT EXISTS idx_documents_user_id ON documents(user_id);
CREATE INDEX IF NOT EXISTS idx_documents_card_number ON documents(card_number);
CREATE INDEX IF NOT EXISTS idx_documents_type_date ON documents(doc_type, created_at DESC);
