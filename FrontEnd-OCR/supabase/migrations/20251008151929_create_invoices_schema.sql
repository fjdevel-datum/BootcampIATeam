/*
  # Create Invoices and Expenses Schema

  1. New Tables
    - `invoices`
      - `id` (uuid, primary key)
      - `user_id` (uuid, foreign key) - References users table
      - `image_url` (text) - URL to uploaded invoice image
      - `country` (text) - Country associated with the invoice
      - `card_id` (uuid, foreign key) - References cards table
      - `vendor_name` (text) - Name of the vendor/supplier
      - `invoice_date` (date) - Date of the invoice
      - `total_amount` (numeric) - Total amount of the invoice
      - `currency` (text) - Currency code (e.g., USD, EUR)
      - `concept` (text) - Brief description of the purchase
      - `category` (text) - Expense category
      - `cost_center` (text) - Cost center for accounting
      - `client_visited` (text, optional) - Client visited (if applicable)
      - `notes` (text, optional) - Additional notes
      - `status` (text) - Processing status (pending, approved, rejected)
      - `created_at` (timestamptz) - Record creation timestamp
      - `updated_at` (timestamptz) - Record update timestamp

  2. Security
    - Enable RLS on invoices table
    - Add policies for authenticated users to manage their own invoices
    - Add policies for viewing and creating invoices

  3. Important Notes
    - All monetary amounts stored as numeric(10,2) for precision
    - Invoices are linked to users and cards via foreign keys
    - Status field uses check constraint for valid values
    - RLS ensures users can only access their own invoices
*/

CREATE TABLE IF NOT EXISTS invoices (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  image_url text NOT NULL,
  country text NOT NULL,
  card_id uuid REFERENCES cards(id) ON DELETE SET NULL,
  vendor_name text NOT NULL,
  invoice_date date NOT NULL,
  total_amount numeric(10,2) NOT NULL,
  currency text NOT NULL DEFAULT 'USD',
  concept text NOT NULL,
  category text NOT NULL,
  cost_center text NOT NULL,
  client_visited text,
  notes text,
  status text NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected')),
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

ALTER TABLE invoices ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view own invoices"
  ON invoices FOR SELECT
  TO authenticated
  USING (
    user_id IN (
      SELECT id FROM users WHERE auth.uid() = id
    )
  );

CREATE POLICY "Users can insert own invoices"
  ON invoices FOR INSERT
  TO authenticated
  WITH CHECK (
    user_id IN (
      SELECT id FROM users WHERE auth.uid() = id
    )
  );

CREATE POLICY "Users can update own invoices"
  ON invoices FOR UPDATE
  TO authenticated
  USING (
    user_id IN (
      SELECT id FROM users WHERE auth.uid() = id
    )
  )
  WITH CHECK (
    user_id IN (
      SELECT id FROM users WHERE auth.uid() = id
    )
  );

CREATE POLICY "Users can delete own invoices"
  ON invoices FOR DELETE
  TO authenticated
  USING (
    user_id IN (
      SELECT id FROM users WHERE auth.uid() = id
    )
  );

CREATE INDEX IF NOT EXISTS idx_invoices_user_id ON invoices(user_id);
CREATE INDEX IF NOT EXISTS idx_invoices_status ON invoices(status);
CREATE INDEX IF NOT EXISTS idx_invoices_invoice_date ON invoices(invoice_date);
CREATE INDEX IF NOT EXISTS idx_invoices_category ON invoices(category);
