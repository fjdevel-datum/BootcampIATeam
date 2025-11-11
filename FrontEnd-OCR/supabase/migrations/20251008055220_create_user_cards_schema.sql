/*
  # Create User Cards Schema

  1. New Tables
    - `users`
      - `id` (uuid, primary key)
      - `name` (text) - User's full name
      - `balance` (numeric) - User's card balance
      - `avatar_url` (text, optional) - URL to user's avatar image
      - `created_at` (timestamptz) - Record creation timestamp
      - `updated_at` (timestamptz) - Record update timestamp

    - `cards`
      - `id` (uuid, primary key)
      - `user_id` (uuid, foreign key) - References users table
      - `country` (text) - Country name for the card
      - `country_code` (text) - ISO country code (e.g., 'GT', 'BZ', 'SV')
      - `is_active` (boolean) - Whether the card is currently active
      - `created_at` (timestamptz) - Record creation timestamp

  2. Security
    - Enable RLS on both tables
    - Add policies for authenticated users to read their own data
    - Add policies for authenticated users to manage their own cards

  3. Important Notes
    - Balance is stored as numeric(10,2) for precision
    - Cards are linked to users via foreign key with cascade delete
    - RLS ensures users can only access their own data
*/

CREATE TABLE IF NOT EXISTS users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name text NOT NULL,
  balance numeric(10,2) DEFAULT 0.00,
  avatar_url text,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

CREATE TABLE IF NOT EXISTS cards (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  country text NOT NULL,
  country_code text NOT NULL,
  is_active boolean DEFAULT true,
  created_at timestamptz DEFAULT now()
);

ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE cards ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view own profile"
  ON users FOR SELECT
  TO authenticated
  USING (auth.uid() = id);

CREATE POLICY "Users can update own profile"
  ON users FOR UPDATE
  TO authenticated
  USING (auth.uid() = id)
  WITH CHECK (auth.uid() = id);

CREATE POLICY "Users can view own cards"
  ON cards FOR SELECT
  TO authenticated
  USING (
    user_id IN (
      SELECT id FROM users WHERE auth.uid() = id
    )
  );

CREATE POLICY "Users can insert own cards"
  ON cards FOR INSERT
  TO authenticated
  WITH CHECK (
    user_id IN (
      SELECT id FROM users WHERE auth.uid() = id
    )
  );

CREATE POLICY "Users can update own cards"
  ON cards FOR UPDATE
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

CREATE POLICY "Users can delete own cards"
  ON cards FOR DELETE
  TO authenticated
  USING (
    user_id IN (
      SELECT id FROM users WHERE auth.uid() = id
    )
  );

CREATE INDEX IF NOT EXISTS idx_cards_user_id ON cards(user_id);
CREATE INDEX IF NOT EXISTS idx_cards_is_active ON cards(is_active);
