export type Json =
  | string
  | number
  | boolean
  | null
  | { [key: string]: Json | undefined }
  | Json[]

export type Database = {
  public: {
    Tables: {
      chaps: {
        Row: {
          chap_id: string
          created_at: string
          cur: number
          dur: number
          history_id: number
          id: number
          name: string
          updated_at: string
        }
        Insert: {
          chap_id: string
          created_at?: string
          cur: number
          dur: number
          history_id: number
          id?: number
          name: string
          updated_at: string
        }
        Update: {
          chap_id?: string
          created_at?: string
          cur?: number
          dur?: number
          history_id?: number
          id?: number
          name?: string
          updated_at?: string
        }
        Relationships: [
          {
            foreignKeyName: "chaps_history_id_fkey"
            columns: ["history_id"]
            isOneToOne: false
            referencedRelation: "history"
            referencedColumns: ["id"]
          }
        ]
      }
      history: {
        Row: {
          created_at: string
          for_to: number | null
          id: number
          name: string
          poster: string
          season: string
          season_name: string
          user_id: number
        }
        Insert: {
          created_at?: string
          for_to?: number | null
          id?: number
          name: string
          poster: string
          season: string
          season_name: string
          user_id: number
        }
        Update: {
          created_at?: string
          for_to?: number | null
          id?: number
          name?: string
          poster?: string
          season?: string
          season_name?: string
          user_id?: number
        }
        Relationships: [
          {
            foreignKeyName: "history_for_to_fkey"
            columns: ["for_to"]
            isOneToOne: false
            referencedRelation: "history"
            referencedColumns: ["id"]
          },
          {
            foreignKeyName: "history_user_id_fkey"
            columns: ["user_id"]
            isOneToOne: false
            referencedRelation: "users"
            referencedColumns: ["id"]
          }
        ]
      }
      movies: {
        Row: {
          add_at: string
          chap: string
          id: number
          name: string
          name_chap: string
          name_season: string
          playlist_id: number
          poster: string
          season: string
        }
        Insert: {
          add_at: string
          chap: string
          id?: number
          name: string
          name_chap: string
          name_season: string
          playlist_id: number
          poster: string
          season: string
        }
        Update: {
          add_at?: string
          chap?: string
          id?: number
          name?: string
          name_chap?: string
          name_season?: string
          playlist_id?: number
          poster?: string
          season?: string
        }
        Relationships: [
          {
            foreignKeyName: "movies_playlist_id_fkey"
            columns: ["playlist_id"]
            isOneToOne: false
            referencedRelation: "playlist"
            referencedColumns: ["id"]
          }
        ]
      }
      playlist: {
        Row: {
          created_at: string
          description: string
          id: number
          name: string
          public: boolean
          updated_at: string
          user_id: number
        }
        Insert: {
          created_at?: string
          description?: string
          id?: number
          name: string
          public?: boolean
          updated_at: string
          user_id: number
        }
        Update: {
          created_at?: string
          description?: string
          id?: number
          name?: string
          public?: boolean
          updated_at?: string
          user_id?: number
        }
        Relationships: [
          {
            foreignKeyName: "playlist_user_id_fkey"
            columns: ["user_id"]
            isOneToOne: false
            referencedRelation: "users"
            referencedColumns: ["id"]
          }
        ]
      }
      users: {
        Row: {
          created_at: string
          email: string | null
          id: number
          last_signin: string | null
          name: string | null
          uuid: string
        }
        Insert: {
          created_at?: string
          email?: string | null
          id?: number
          last_signin?: string | null
          name?: string | null
          uuid: string
        }
        Update: {
          created_at?: string
          email?: string | null
          id?: number
          last_signin?: string | null
          name?: string | null
          uuid?: string
        }
        Relationships: []
      }
    }
    Views: {
      [_ in never]: never
    }
    Functions: {
      add_movie_playlist: {
        Args: {
          user_uid: string
          playlist_id: number
          p_chap: string
          p_name: string
          p_name_chap: string
          p_name_season: string
          p_poster: string
          p_season: string
        }
        Returns: {
          id: number
          created_at: string
          public: boolean
          name: string
          description: string
          updated_at: string
          movies_count: number
        }[]
      }
      create_playlist: {
        Args: {
          user_uid: string
          playlist_name: string
          is_public: boolean
        }
        Returns: {
          id: number
          created_at: string
          public: boolean
          name: string
          description: string
          updated_at: string
        }[]
      }
      delete_movie_playlist: {
        Args: {
          user_uid: string
          playlist_id: number
          p_season: string
        }
        Returns: {
          id: number
          created_at: string
          public: boolean
          name: string
          description: string
          updated_at: string
          movies_count: number
        }[]
      }
      delete_playlist: {
        Args: {
          user_uid: string
          playlist_id: number
        }
        Returns: undefined
      }
      get_last_chap: {
        Args: {
          user_uid: string
          season_id: string
        }
        Returns: {
          created_at: string
          cur: number
          dur: number
          name: string
          updated_at: string
          chap_id: string
        }[]
      }
      get_list_playlist: {
        Args: {
          user_uid: string
        }
        Returns: {
          id: number
          created_at: string
          public: boolean
          name: string
          description: string
          updated_at: string
          movies_count: number
        }[]
      }
      get_movies_playlist: {
        Args: {
          user_uid: string
          playlist_id: number
          sorter: string
          page: number
          page_size: number
        }
        Returns: {
          add_at: string
          name_chap: string
          name: string
          chap: string
          poster: string
          name_season: string
          season: string
        }[]
      }
      get_poster_playlist: {
        Args: {
          user_uid: string
          playlist_id: number
        }
        Returns: {
          poster: string
        }[]
      }
      get_single_progress: {
        Args: {
          user_uid: string
          season_id: string
          p_chap_id: string
        }
        Returns: {
          created_at: string
          cur: number
          dur: number
          name: string
          updated_at: string
        }[]
      }
      get_watch_progress: {
        Args: {
          user_uid: string
          season_id: string
        }
        Returns: {
          created_at: string
          cur: number
          dur: number
          name: string
          updated_at: string
          chap_id: string
        }[]
      }
      has_movie_playlist: {
        Args: {
          user_uid: string
          playlist_id: number
          season_id: string
        }
        Returns: {
          has_movie: boolean
        }[]
      }
      has_movie_playlists: {
        Args: {
          user_uid: string
          playlist_ids: number[]
          season_id: string
        }
        Returns: {
          playlist_id: number
          has_movie: boolean
        }[]
      }
      query_history: {
        Args: {
          user_uid: string
          page: number
          size: number
        }
        Returns: {
          created_at: string
          season: string
          name: string
          poster: string
          season_name: string
          watch_updated_at: string
          watch_name: string
          watch_id: string
          watch_cur: number
          watch_dur: number
        }[]
      }
      rename_playlist: {
        Args: {
          user_uid: string
          old_name: string
          new_name: string
        }
        Returns: undefined
      }
      set_description_playlist: {
        Args: {
          user_uid: string
          playlist_id: number
          playlist_description: string
        }
        Returns: undefined
      }
      set_public_playlist: {
        Args: {
          user_uid: string
          playlist_id: number
          is_public: boolean
        }
        Returns: undefined
      }
      set_single_progress:
        | {
            Args: {
              p_name: string
              p_poster: string
              season_id: string
              p_season_name: string
              user_uid: string
              e_cur: number
              e_dur: number
              e_name: string
              e_chap: string
            }
            Returns: undefined
          }
        | {
            Args: {
              p_name: string
              p_poster: string
              season_id: string
              p_season_name: string
              user_uid: string
              e_cur: number
              e_dur: number
              e_name: string
              e_chap: string
              gmt: string
            }
            Returns: undefined
          }
      upsert_user: {
        Args: {
          p_uuid: string
          p_email?: string
          p_name?: string
        }
        Returns: undefined
      }
    }
    Enums: {
      [_ in never]: never
    }
    CompositeTypes: {
      [_ in never]: never
    }
  }
}

type PublicSchema = Database[Extract<keyof Database, "public">]

export type Tables<
  PublicTableNameOrOptions extends
    | keyof (PublicSchema["Tables"] & PublicSchema["Views"])
    | { schema: keyof Database },
  TableName extends PublicTableNameOrOptions extends { schema: keyof Database }
    ? keyof (Database[PublicTableNameOrOptions["schema"]]["Tables"] &
        Database[PublicTableNameOrOptions["schema"]]["Views"])
    : never = never
> = PublicTableNameOrOptions extends { schema: keyof Database }
  ? (Database[PublicTableNameOrOptions["schema"]]["Tables"] &
      Database[PublicTableNameOrOptions["schema"]]["Views"])[TableName] extends {
      Row: infer R
    }
    ? R
    : never
  : PublicTableNameOrOptions extends keyof (PublicSchema["Tables"] &
      PublicSchema["Views"])
  ? (PublicSchema["Tables"] &
      PublicSchema["Views"])[PublicTableNameOrOptions] extends {
      Row: infer R
    }
    ? R
    : never
  : never

export type TablesInsert<
  PublicTableNameOrOptions extends
    | keyof PublicSchema["Tables"]
    | { schema: keyof Database },
  TableName extends PublicTableNameOrOptions extends { schema: keyof Database }
    ? keyof Database[PublicTableNameOrOptions["schema"]]["Tables"]
    : never = never
> = PublicTableNameOrOptions extends { schema: keyof Database }
  ? Database[PublicTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Insert: infer I
    }
    ? I
    : never
  : PublicTableNameOrOptions extends keyof PublicSchema["Tables"]
  ? PublicSchema["Tables"][PublicTableNameOrOptions] extends {
      Insert: infer I
    }
    ? I
    : never
  : never

export type TablesUpdate<
  PublicTableNameOrOptions extends
    | keyof PublicSchema["Tables"]
    | { schema: keyof Database },
  TableName extends PublicTableNameOrOptions extends { schema: keyof Database }
    ? keyof Database[PublicTableNameOrOptions["schema"]]["Tables"]
    : never = never
> = PublicTableNameOrOptions extends { schema: keyof Database }
  ? Database[PublicTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Update: infer U
    }
    ? U
    : never
  : PublicTableNameOrOptions extends keyof PublicSchema["Tables"]
  ? PublicSchema["Tables"][PublicTableNameOrOptions] extends {
      Update: infer U
    }
    ? U
    : never
  : never

export type Enums<
  PublicEnumNameOrOptions extends
    | keyof PublicSchema["Enums"]
    | { schema: keyof Database },
  EnumName extends PublicEnumNameOrOptions extends { schema: keyof Database }
    ? keyof Database[PublicEnumNameOrOptions["schema"]]["Enums"]
    : never = never
> = PublicEnumNameOrOptions extends { schema: keyof Database }
  ? Database[PublicEnumNameOrOptions["schema"]]["Enums"][EnumName]
  : PublicEnumNameOrOptions extends keyof PublicSchema["Enums"]
  ? PublicSchema["Enums"][PublicEnumNameOrOptions]
  : never
