import { createClient } from "@supabase/supabase-js"
import type { Database } from "app/database"

export const supabase = createClient<Database>(
  process.env.SUPABASE_URL,
  process.env.SUPABASE_KEY
)
