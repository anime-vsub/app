declare namespace NodeJS {
  interface ProcessEnv {
    NODE_ENV: string
    VUE_ROUTER_MODE: "hash" | "history" | "abstract" | undefined
    VUE_ROUTER_BASE: string | undefined


    SUPABASE_URL: string
    SUPABASE_KEY: string

    FIREBASE_CONFIG: string
    API_SK: string
  }
}
