
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE EXTENSION IF NOT EXISTS "pgsodium" WITH SCHEMA "pgsodium";

COMMENT ON SCHEMA "public" IS 'standard public schema';

CREATE EXTENSION IF NOT EXISTS "pg_graphql" WITH SCHEMA "graphql";

CREATE EXTENSION IF NOT EXISTS "pg_stat_statements" WITH SCHEMA "extensions";

CREATE EXTENSION IF NOT EXISTS "pgcrypto" WITH SCHEMA "extensions";

CREATE EXTENSION IF NOT EXISTS "pgjwt" WITH SCHEMA "extensions";

CREATE EXTENSION IF NOT EXISTS "supabase_vault" WITH SCHEMA "vault";

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA "extensions";

CREATE OR REPLACE FUNCTION "public"."add_movie_playlist"("user_uid" "text", "playlist_id" bigint, "p_chap" "text", "p_name" "text", "p_name_chap" "text", "p_name_season" "text", "p_poster" "text", "p_season" "text") RETURNS TABLE("id" bigint, "created_at" timestamp with time zone, "public" boolean, "name" "text", "description" "text", "updated_at" timestamp with time zone, "movies_count" bigint)
    LANGUAGE "plpgsql"
    AS $$
DECLARE
  p_playlist_id int8;
BEGIN

    select playlist.id into p_playlist_id
    from playlist
    where user_id = (select users.id from users where uuid = user_uid limit 1)
    and playlist.id = playlist_id
    limit 1;
    
    if p_playlist_id is null then
      raise EXCEPTION 'Playlist not exists';
    else
      if not exists(select * from movies where movies.playlist_id = p_playlist_id and season = p_season limit 1) then
        insert into movies (add_at, chap, name, name_chap, name_season, playlist_id, poster, season)
        values (now(), p_chap, p_name, p_name_chap, p_name_season, p_playlist_id, p_poster, p_season);
      end if;
    end if;

    
    return query SELECT p.id, p.created_at, p.public, p.name, p.description, p.updated_at, 
           (SELECT COUNT(*) FROM public.movies m WHERE m.playlist_id = p.id) as movies_count
    FROM public.playlist p
    JOIN public.users u ON p.user_id = u.id
    WHERE u.uuid = user_uid
    AND p.id = p_playlist_id
    limit 1;
END;
$$;

ALTER FUNCTION "public"."add_movie_playlist"("user_uid" "text", "playlist_id" bigint, "p_chap" "text", "p_name" "text", "p_name_chap" "text", "p_name_season" "text", "p_poster" "text", "p_season" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."add_movie_playlist"("user_uid" "text", "playlist_name" "text", "p_chap" "text", "p_name" "text", "p_name_chap" "text", "p_name_season" "text", "p_poster" "text", "p_season" "text") RETURNS "void"
    LANGUAGE "plpgsql"
    AS $$
DECLARE
  p_playlist_id int8;
BEGIN
    select id into p_playlist_id
    from playlist
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid limit 1)
    AND name = playlist_name
    limit 1;

    if p_playlist_id is not null then
      raise EXCEPTION 'Playlist not exists';
    else
      insert into movies (add_at, chap, name, name_chap, name_season, playlist_id, poster, season)
      values (now(), p_chap, p_name, p_name_chap, p_name_season, p_playlist_id, p_poster, p_season);
    end if;
END;
$$;

ALTER FUNCTION "public"."add_movie_playlist"("user_uid" "text", "playlist_name" "text", "p_chap" "text", "p_name" "text", "p_name_chap" "text", "p_name_season" "text", "p_poster" "text", "p_season" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."create_playlist"("user_uid" "text", "playlist_name" "text", "is_public" boolean) RETURNS TABLE("id" bigint, "created_at" timestamp with time zone, "public" boolean, "name" "text", "description" "text", "updated_at" timestamp with time zone)
    LANGUAGE "plpgsql"
    AS $$
    DECLARE new_playlist_id bigint;
    BEGIN
        INSERT INTO public.playlist (created_at, public, name, user_id)
        VALUES (now(), is_public, playlist_name, (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1))
        RETURNING id, created_at, public, name, null::text, now(), user_id INTO new_playlist_id;
        
        RETURN QUERY SELECT p.id, p.created_at, p.public, p.name, p.description, p.updated_at
        FROM public.playlist p
        WHERE p.id = new_playlist_id;
    END;
$$;

ALTER FUNCTION "public"."create_playlist"("user_uid" "text", "playlist_name" "text", "is_public" boolean) OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."delete_movie_playlist"("user_uid" "text", "playlist_id" bigint, "p_season" "text") RETURNS TABLE("id" bigint, "created_at" timestamp with time zone, "public" boolean, "name" "text", "description" "text", "updated_at" timestamp with time zone, "movies_count" bigint)
    LANGUAGE "plpgsql"
    AS $$
DECLARE
  p_playlist_id int8;
BEGIN
    p_playlist_id := playlist_id;
    
    DELETE FROM movies
    WHERE movies.playlist_id IN (SELECT playlist.id FROM playlist WHERE user_id = (SELECT users.id FROM users WHERE uuid = user_uid) and playlist.id = p_playlist_id)
    AND season = p_season;

    
    return query SELECT p.id, p.created_at, p.public, p.name, p.description, p.updated_at, 
           (SELECT COUNT(*) FROM public.movies m WHERE m.playlist_id = p.id) as movies_count
    FROM public.playlist p
    JOIN public.users u ON p.user_id = u.id
    WHERE u.uuid = user_uid
    AND p.id = p_playlist_id
    limit 1;
END;
$$;

ALTER FUNCTION "public"."delete_movie_playlist"("user_uid" "text", "playlist_id" bigint, "p_season" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."delete_movie_playlist"("user_uid" "text", "q_name" "text", "p_season" "text") RETURNS "void"
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    DELETE FROM public.movies
    WHERE playlist_id IN (SELECT id FROM public.playlist WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid) and name = q_name)
    AND season = p_season;
END;
$$;

ALTER FUNCTION "public"."delete_movie_playlist"("user_uid" "text", "q_name" "text", "p_season" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."delete_playlist"("user_uid" "text", "playlist_id" bigint) RETURNS "void"
    LANGUAGE "sql"
    AS $$
    DELETE FROM public.playlist
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND id = playlist_id;
$$;

ALTER FUNCTION "public"."delete_playlist"("user_uid" "text", "playlist_id" bigint) OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."delete_playlist"("user_uid" "text", "playlist_name" "text") RETURNS "void"
    LANGUAGE "sql"
    AS $$
    DELETE FROM public.playlist
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND name = playlist_name;
$$;

ALTER FUNCTION "public"."delete_playlist"("user_uid" "text", "playlist_name" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."get_last_chap"("user_uid" "text", "season" "text") RETURNS TABLE("created_at" timestamp with time zone, "cur" double precision, "dur" double precision, "name" "text", "chap_id" "text", "updated_at" timestamp with time zone)
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    RETURN QUERY
    SELECT c.created_at, c.cur, c.dur, c.name, c.updated_at, c.chap_id
    FROM public.chaps c
    JOIN public.history h ON c.history_id = h.id
    JOIN public.users u ON h.user_id = u.id
    WHERE u.uuid = user_uid
    AND h.season = season
    order by updated_at desc
    limit 1;
END;
$$;

ALTER FUNCTION "public"."get_last_chap"("user_uid" "text", "season" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."get_list_playlist"("user_uid" "text") RETURNS TABLE("id" bigint, "created_at" timestamp with time zone, "public" boolean, "name" "text", "description" "text", "updated_at" timestamp with time zone, "movies_count" bigint)
    LANGUAGE "sql"
    AS $$
    SELECT p.id, p.created_at, p.public, p.name, p.description, p.updated_at, 
           (SELECT COUNT(*) FROM public.movies m WHERE m.playlist_id = p.id) as movies_count
    FROM public.playlist p
    JOIN public.users u ON p.user_id = u.id
    WHERE u.uuid = user_uid
    ORDER BY p.created_at DESC;
$$;

ALTER FUNCTION "public"."get_list_playlist"("user_uid" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."get_movies_playlist"("user_uid" "text", "playlist_id" bigint, "sorter" "text", "page" integer, "page_size" integer) RETURNS TABLE("add_at" timestamp with time zone, "name_chap" "text", "name" "text", "chap" "text", "poster" "text", "name_season" "text", "season" "text")
    LANGUAGE "plpgsql"
    AS $$
declare
  i_playlist_id int8;
begin
    i_playlist_id := playlist_id;

    if (sorter = 'desc') then
      return query SELECT m.add_at, m.name_chap, m.name, m.chap, m.poster, m.name_season, m.season
      FROM public.movies m
      JOIN public.playlist p ON m.playlist_id = p.id
      JOIN public.users u ON p.user_id = u.id
      WHERE u.uuid = user_uid
      AND p.id = i_playlist_id
      ORDER BY m.add_at DESC
      LIMIT page_size OFFSET (page - 1) * page_size;
    else
      return query SELECT m.add_at, m.name_chap, m.name, m.chap, m.poster, m.name_season, m.season
      FROM public.movies m
      JOIN public.playlist p ON m.playlist_id = p.id
      JOIN public.users u ON p.user_id = u.id
      WHERE u.uuid = user_uid
      AND p.id = i_playlist_id
      ORDER BY m.add_at ASC
      LIMIT page_size OFFSET (page - 1) * page_size;
    end if;
end;
$$;

ALTER FUNCTION "public"."get_movies_playlist"("user_uid" "text", "playlist_id" bigint, "sorter" "text", "page" integer, "page_size" integer) OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."get_poster_playlist"("user_uid" "text", "playlist_id" bigint) RETURNS TABLE("poster" "text")
    LANGUAGE "plpgsql"
    AS $$
declare
  p_playlist_id int8;
begin
    p_playlist_id := playlist_id;

    return query SELECT m.poster
    FROM public.movies m
    JOIN public.playlist p ON m.playlist_id = p.id
    JOIN public.users u ON p.user_id = u.id
    WHERE u.uuid = user_uid
    AND p.id = p_playlist_id
    ORDER BY m.add_at
    LIMIT 1;
end;
$$;

ALTER FUNCTION "public"."get_poster_playlist"("user_uid" "text", "playlist_id" bigint) OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."get_poster_playlist"("user_uid" "text", "playlist_name" "text") RETURNS TABLE("poster" "text")
    LANGUAGE "sql"
    AS $$
    SELECT m.poster
    FROM public.movies m
    JOIN public.playlist p ON m.playlist_id = p.id
    JOIN public.users u ON p.user_id = u.id
    WHERE u.uuid = user_uid
    AND p.name = playlist_name
    ORDER BY m.add_at
    LIMIT 1;
$$;

ALTER FUNCTION "public"."get_poster_playlist"("user_uid" "text", "playlist_name" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."get_single_progress"("user_uid" "text", "season_id" "text", "p_chap_id" "text") RETURNS TABLE("created_at" timestamp with time zone, "cur" double precision, "dur" double precision, "name" "text", "updated_at" timestamp with time zone)
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    RETURN QUERY
    SELECT c.created_at, c.cur, c.dur, c.name, c.updated_at
    FROM public.chaps c
    JOIN public.history h ON c.history_id = h.id
    JOIN public.users u ON h.user_id = u.id
    WHERE u.uuid = user_uid
    AND h.season = season_id
    AND c.chap_id = p_chap_id
    limit 1;
END;
$$;

ALTER FUNCTION "public"."get_single_progress"("user_uid" "text", "season_id" "text", "p_chap_id" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."get_watch_progress"("user_uid" "text", "season_id" "text") RETURNS TABLE("created_at" timestamp with time zone, "cur" double precision, "dur" double precision, "name" "text", "updated_at" timestamp with time zone, "chap_id" "text")
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    RETURN QUERY
   SELECT 
    chaps.created_at, 
    chaps.cur, 
    chaps.dur, 
    chaps.name, 
    chaps.updated_at, 
    chaps.chap_id
FROM 
    chaps
JOIN 
    public.history ON chaps.history_id = history.id
JOIN 
    public.users ON history.user_id = users.id
WHERE 
    users.uuid = user_uid
    AND history.season = season_id
ORDER BY 
    history.id;

END;
$$;

ALTER FUNCTION "public"."get_watch_progress"("user_uid" "text", "season_id" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."has_movie_playlist"("user_uid" "text", "playlist_id" bigint, "season_id" "text") RETURNS TABLE("has_movie" boolean)
    LANGUAGE "plpgsql"
    AS $$
DECLARE
    i_playlist_id int8;
begin 
    i_playlist_id := playlist_id;
    return query SELECT EXISTS (
        SELECT 1
        FROM public.movies m
        JOIN public.playlist p ON m.playlist_id = p.id
        JOIN public.users u ON p.user_id = u.id
        WHERE u.uuid = user_uid
        AND p.id = i_playlist_id
        AND m.season = season_id
    ) AS has_movie;
end;
$$;

ALTER FUNCTION "public"."has_movie_playlist"("user_uid" "text", "playlist_id" bigint, "season_id" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."has_movie_playlist"("user_uid" "text", "playlist_name" "text", "season_id" "text") RETURNS TABLE("has_movie" boolean)
    LANGUAGE "plpgsql"
    AS $$
begin 
    return query SELECT EXISTS (
        SELECT 1
        FROM public.movies m
        JOIN public.playlist p ON m.playlist_id = p.id
        JOIN public.users u ON p.user_id = u.id
        WHERE u.uuid = user_uid
        AND p.name = playlist_name
        AND m.season = season_id
    ) AS has_movie;
end;
$$;

ALTER FUNCTION "public"."has_movie_playlist"("user_uid" "text", "playlist_name" "text", "season_id" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."has_movie_playlists"("user_uid" "text", "playlist_ids" bigint[], "season_id" "text") RETURNS TABLE("playlist_id" bigint, "has_movie" boolean)
    LANGUAGE "plpgsql"
    AS $$
DECLARE
    i_playlist_id int8;
BEGIN 
    FOREACH i_playlist_id IN ARRAY playlist_ids
    LOOP
        RETURN QUERY SELECT i_playlist_id, EXISTS (
            SELECT 1
            FROM public.movies m
            JOIN public.playlist p ON m.playlist_id = p.id
            JOIN public.users u ON p.user_id = u.id
            WHERE u.uuid = user_uid
            AND p.id = i_playlist_id
            AND m.season = season_id
        ) AS has_movie;
    END LOOP;
END;
$$;

ALTER FUNCTION "public"."has_movie_playlists"("user_uid" "text", "playlist_ids" bigint[], "season_id" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."query_history"("user_uid" "text", "page" integer, "size" integer) RETURNS TABLE("created_at" timestamp with time zone, "season" "text", "name" "text", "poster" "text", "season_name" "text", "watch_updated_at" timestamp with time zone, "watch_name" "text", "watch_id" "text", "watch_cur" double precision, "watch_dur" double precision)
    LANGUAGE "plpgsql"
    AS $$
DECLARE
    query_limit integer;
BEGIN
    -- Set the query limit to the provided limit or maximum of 50
    query_limit := LEAST(size, 50);

    RETURN QUERY
    SELECT h.created_at, h.season, h.name, h.poster, h.season_name,
    c.updated_at as watch_updated_at, c.name as watch_name, c.chap_id as watch_id, c.cur as watch_cur, c.dur as watch_dur
    FROM public.history h
    JOIN public.users u ON h.user_id = u.id
   
    JOIN LATERAL (
        SELECT *
        FROM public.chaps
        WHERE history_id = h.id
        ORDER BY updated_at DESC
        LIMIT 1
    ) c ON true
    WHERE u.uuid = user_uid
    ORDER BY h.created_at DESC
    LIMIT query_limit OFFSET (page - 1) * query_limit;
END;
$$;

ALTER FUNCTION "public"."query_history"("user_uid" "text", "page" integer, "size" integer) OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."rename_playlist"("user_uid" "text", "old_name" "text", "new_name" "text") RETURNS "void"
    LANGUAGE "sql"
    AS $$
    UPDATE public.playlist
    SET name = new_name
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND name = old_name;
$$;

ALTER FUNCTION "public"."rename_playlist"("user_uid" "text", "old_name" "text", "new_name" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."set_description_playlist"("user_uid" "text", "playlist_id" bigint, "playlist_description" "text") RETURNS "void"
    LANGUAGE "sql"
    AS $$
    UPDATE public.playlist
    SET description = playlist_description
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND id = playlist_id;
$$;

ALTER FUNCTION "public"."set_description_playlist"("user_uid" "text", "playlist_id" bigint, "playlist_description" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."set_description_playlist"("user_uid" "text", "playlist_name" "text", "playlist_description" "text") RETURNS "void"
    LANGUAGE "sql"
    AS $$
    UPDATE public.playlist
    SET description = playlist_description
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND name = playlist_name;
$$;

ALTER FUNCTION "public"."set_description_playlist"("user_uid" "text", "playlist_name" "text", "playlist_description" "text") OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."set_public_playlist"("user_uid" "text", "playlist_id" bigint, "is_public" boolean) RETURNS "void"
    LANGUAGE "sql"
    AS $$
    UPDATE public.playlist
    SET public = is_public
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND id = playlist_id;
$$;

ALTER FUNCTION "public"."set_public_playlist"("user_uid" "text", "playlist_id" bigint, "is_public" boolean) OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."set_public_playlist"("user_uid" "text", "playlist_name" "text", "is_public" boolean) RETURNS "void"
    LANGUAGE "sql"
    AS $$
    UPDATE public.playlist
    SET public = is_public
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND name = playlist_name;
$$;

ALTER FUNCTION "public"."set_public_playlist"("user_uid" "text", "playlist_name" "text", "is_public" boolean) OWNER TO "postgres";

CREATE OR REPLACE FUNCTION "public"."set_single_progress"("p_name" "text", "p_poster" "text", "season_id" "text", "p_season_name" "text", "user_uid" "text", "e_cur" double precision, "e_dur" double precision, "e_name" "text", "e_chap" "text") RETURNS "void"
    LANGUAGE "plpgsql"
    AS $$
DECLARE
    latest_history record;
    i_user_id int8;
    id_history_rax int8;
    id_chap int8;
BEGIN
    -- Check if user exists
    SELECT id INTO i_user_id
    FROM users
    WHERE uuid = user_uid
    LIMIT 1;

    IF i_user_id IS NULL THEN
        RAISE EXCEPTION 'User does not exist';
    END IF;

    -- Get the latest history record for the user and season
    SELECT * INTO latest_history
    FROM history
    WHERE user_id = i_user_id AND season = season_id
    ORDER BY created_at DESC
    LIMIT 1;

    -- Insert new history if it does not exist or is not the latest for today
    IF latest_history IS NULL OR latest_history.created_at::DATE <> NOW()::DATE THEN
        INSERT INTO history (created_at, user_id, season, name, poster, season_name, for_to)
        VALUES (NOW(), i_user_id, season_id, p_name, p_poster, p_season_name, NULL);
    END IF;

    -- Get the latest history record ID for the user and season
    SELECT id INTO id_history_rax
    FROM history
    WHERE user_id = i_user_id AND season = season_id AND for_to IS NULL
    ORDER BY created_at DESC
    LIMIT 1;

    IF id_history_rax IS NULL THEN
        RAISE EXCEPTION 'Failed to retrieve or create history record';
    END IF;

    -- Check if the chapter already exists for the history record
    SELECT id INTO id_chap
    FROM chaps
    WHERE history_id = id_history_rax AND chap_id = e_chap
    LIMIT 1;

    -- Insert or update the chapter
    IF id_chap IS NULL THEN
        INSERT INTO chaps (created_at, history_id, cur, dur, name, updated_at, chap_id)
        VALUES (NOW(), id_history_rax, e_cur, e_dur, e_name, NOW(), e_chap);
    ELSE
        UPDATE chaps
        SET cur = e_cur, dur = e_dur, updated_at = NOW()
        WHERE id = id_chap;
    END IF;
END;
$$;

ALTER FUNCTION "public"."set_single_progress"("p_name" "text", "p_poster" "text", "season_id" "text", "p_season_name" "text", "user_uid" "text", "e_cur" double precision, "e_dur" double precision, "e_name" "text", "e_chap" "text") OWNER TO "postgres";

SET default_tablespace = '';

SET default_table_access_method = "heap";

CREATE TABLE IF NOT EXISTS "public"."chaps" (
    "id" bigint NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "history_id" bigint NOT NULL,
    "cur" double precision NOT NULL,
    "dur" double precision NOT NULL,
    "name" "text" NOT NULL,
    "updated_at" timestamp with time zone NOT NULL,
    "chap_id" "text" NOT NULL
);

ALTER TABLE "public"."chaps" OWNER TO "postgres";

ALTER TABLE "public"."chaps" ALTER COLUMN "id" ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME "public"."chaps_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE IF NOT EXISTS "public"."history" (
    "id" bigint NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "user_id" bigint NOT NULL,
    "season" "text" NOT NULL,
    "name" "text" NOT NULL,
    "poster" "text" NOT NULL,
    "season_name" "text" NOT NULL,
    "for_to" bigint
);

ALTER TABLE "public"."history" OWNER TO "postgres";

ALTER TABLE "public"."history" ALTER COLUMN "id" ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME "public"."history_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE IF NOT EXISTS "public"."movies" (
    "id" bigint NOT NULL,
    "add_at" timestamp with time zone NOT NULL,
    "name_chap" "text" NOT NULL,
    "name" "text" NOT NULL,
    "chap" "text" NOT NULL,
    "poster" "text" NOT NULL,
    "name_season" "text" NOT NULL,
    "playlist_id" bigint NOT NULL,
    "season" "text" NOT NULL
);

ALTER TABLE "public"."movies" OWNER TO "postgres";

ALTER TABLE "public"."movies" ALTER COLUMN "id" ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME "public"."movies_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE IF NOT EXISTS "public"."playlist" (
    "id" bigint NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "public" boolean DEFAULT false NOT NULL,
    "name" "text" NOT NULL,
    "description" "text" DEFAULT '""'::"text" NOT NULL,
    "updated_at" timestamp with time zone NOT NULL,
    "user_id" bigint NOT NULL
);

ALTER TABLE "public"."playlist" OWNER TO "postgres";

ALTER TABLE "public"."playlist" ALTER COLUMN "id" ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME "public"."playlist_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE IF NOT EXISTS "public"."users" (
    "id" bigint NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "uuid" "text" NOT NULL,
    "name" "text" DEFAULT ''::"text",
    "email" "text"
);

ALTER TABLE "public"."users" OWNER TO "postgres";

ALTER TABLE "public"."users" ALTER COLUMN "id" ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME "public"."users_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

ALTER TABLE ONLY "public"."chaps"
    ADD CONSTRAINT "chaps_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."history"
    ADD CONSTRAINT "history_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."movies"
    ADD CONSTRAINT "movies_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."playlist"
    ADD CONSTRAINT "playlist_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_uuid_key" UNIQUE ("uuid");

ALTER TABLE ONLY "public"."chaps"
    ADD CONSTRAINT "chaps_history_id_fkey" FOREIGN KEY ("history_id") REFERENCES "public"."history"("id");

ALTER TABLE ONLY "public"."history"
    ADD CONSTRAINT "history_for_to_fkey" FOREIGN KEY ("for_to") REFERENCES "public"."history"("id");

ALTER TABLE ONLY "public"."history"
    ADD CONSTRAINT "history_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."users"("id");

ALTER TABLE ONLY "public"."movies"
    ADD CONSTRAINT "movies_playlist_id_fkey" FOREIGN KEY ("playlist_id") REFERENCES "public"."playlist"("id");

ALTER TABLE ONLY "public"."playlist"
    ADD CONSTRAINT "playlist_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."users"("id");

ALTER PUBLICATION "supabase_realtime" OWNER TO "postgres";

GRANT USAGE ON SCHEMA "public" TO "postgres";
GRANT USAGE ON SCHEMA "public" TO "anon";
GRANT USAGE ON SCHEMA "public" TO "authenticated";
GRANT USAGE ON SCHEMA "public" TO "service_role";

GRANT ALL ON FUNCTION "public"."add_movie_playlist"("user_uid" "text", "playlist_id" bigint, "p_chap" "text", "p_name" "text", "p_name_chap" "text", "p_name_season" "text", "p_poster" "text", "p_season" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."add_movie_playlist"("user_uid" "text", "playlist_id" bigint, "p_chap" "text", "p_name" "text", "p_name_chap" "text", "p_name_season" "text", "p_poster" "text", "p_season" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."add_movie_playlist"("user_uid" "text", "playlist_id" bigint, "p_chap" "text", "p_name" "text", "p_name_chap" "text", "p_name_season" "text", "p_poster" "text", "p_season" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."add_movie_playlist"("user_uid" "text", "playlist_name" "text", "p_chap" "text", "p_name" "text", "p_name_chap" "text", "p_name_season" "text", "p_poster" "text", "p_season" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."add_movie_playlist"("user_uid" "text", "playlist_name" "text", "p_chap" "text", "p_name" "text", "p_name_chap" "text", "p_name_season" "text", "p_poster" "text", "p_season" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."add_movie_playlist"("user_uid" "text", "playlist_name" "text", "p_chap" "text", "p_name" "text", "p_name_chap" "text", "p_name_season" "text", "p_poster" "text", "p_season" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."create_playlist"("user_uid" "text", "playlist_name" "text", "is_public" boolean) TO "anon";
GRANT ALL ON FUNCTION "public"."create_playlist"("user_uid" "text", "playlist_name" "text", "is_public" boolean) TO "authenticated";
GRANT ALL ON FUNCTION "public"."create_playlist"("user_uid" "text", "playlist_name" "text", "is_public" boolean) TO "service_role";

GRANT ALL ON FUNCTION "public"."delete_movie_playlist"("user_uid" "text", "playlist_id" bigint, "p_season" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."delete_movie_playlist"("user_uid" "text", "playlist_id" bigint, "p_season" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."delete_movie_playlist"("user_uid" "text", "playlist_id" bigint, "p_season" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."delete_movie_playlist"("user_uid" "text", "q_name" "text", "p_season" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."delete_movie_playlist"("user_uid" "text", "q_name" "text", "p_season" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."delete_movie_playlist"("user_uid" "text", "q_name" "text", "p_season" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."delete_playlist"("user_uid" "text", "playlist_id" bigint) TO "anon";
GRANT ALL ON FUNCTION "public"."delete_playlist"("user_uid" "text", "playlist_id" bigint) TO "authenticated";
GRANT ALL ON FUNCTION "public"."delete_playlist"("user_uid" "text", "playlist_id" bigint) TO "service_role";

GRANT ALL ON FUNCTION "public"."delete_playlist"("user_uid" "text", "playlist_name" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."delete_playlist"("user_uid" "text", "playlist_name" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."delete_playlist"("user_uid" "text", "playlist_name" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."get_last_chap"("user_uid" "text", "season" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."get_last_chap"("user_uid" "text", "season" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."get_last_chap"("user_uid" "text", "season" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."get_list_playlist"("user_uid" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."get_list_playlist"("user_uid" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."get_list_playlist"("user_uid" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."get_movies_playlist"("user_uid" "text", "playlist_id" bigint, "sorter" "text", "page" integer, "page_size" integer) TO "anon";
GRANT ALL ON FUNCTION "public"."get_movies_playlist"("user_uid" "text", "playlist_id" bigint, "sorter" "text", "page" integer, "page_size" integer) TO "authenticated";
GRANT ALL ON FUNCTION "public"."get_movies_playlist"("user_uid" "text", "playlist_id" bigint, "sorter" "text", "page" integer, "page_size" integer) TO "service_role";

GRANT ALL ON FUNCTION "public"."get_poster_playlist"("user_uid" "text", "playlist_id" bigint) TO "anon";
GRANT ALL ON FUNCTION "public"."get_poster_playlist"("user_uid" "text", "playlist_id" bigint) TO "authenticated";
GRANT ALL ON FUNCTION "public"."get_poster_playlist"("user_uid" "text", "playlist_id" bigint) TO "service_role";

GRANT ALL ON FUNCTION "public"."get_poster_playlist"("user_uid" "text", "playlist_name" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."get_poster_playlist"("user_uid" "text", "playlist_name" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."get_poster_playlist"("user_uid" "text", "playlist_name" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."get_single_progress"("user_uid" "text", "season_id" "text", "p_chap_id" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."get_single_progress"("user_uid" "text", "season_id" "text", "p_chap_id" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."get_single_progress"("user_uid" "text", "season_id" "text", "p_chap_id" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."get_watch_progress"("user_uid" "text", "season_id" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."get_watch_progress"("user_uid" "text", "season_id" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."get_watch_progress"("user_uid" "text", "season_id" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."has_movie_playlist"("user_uid" "text", "playlist_id" bigint, "season_id" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."has_movie_playlist"("user_uid" "text", "playlist_id" bigint, "season_id" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."has_movie_playlist"("user_uid" "text", "playlist_id" bigint, "season_id" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."has_movie_playlist"("user_uid" "text", "playlist_name" "text", "season_id" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."has_movie_playlist"("user_uid" "text", "playlist_name" "text", "season_id" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."has_movie_playlist"("user_uid" "text", "playlist_name" "text", "season_id" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."has_movie_playlists"("user_uid" "text", "playlist_ids" bigint[], "season_id" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."has_movie_playlists"("user_uid" "text", "playlist_ids" bigint[], "season_id" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."has_movie_playlists"("user_uid" "text", "playlist_ids" bigint[], "season_id" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."query_history"("user_uid" "text", "page" integer, "size" integer) TO "anon";
GRANT ALL ON FUNCTION "public"."query_history"("user_uid" "text", "page" integer, "size" integer) TO "authenticated";
GRANT ALL ON FUNCTION "public"."query_history"("user_uid" "text", "page" integer, "size" integer) TO "service_role";

GRANT ALL ON FUNCTION "public"."rename_playlist"("user_uid" "text", "old_name" "text", "new_name" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."rename_playlist"("user_uid" "text", "old_name" "text", "new_name" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."rename_playlist"("user_uid" "text", "old_name" "text", "new_name" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."set_description_playlist"("user_uid" "text", "playlist_id" bigint, "playlist_description" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."set_description_playlist"("user_uid" "text", "playlist_id" bigint, "playlist_description" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."set_description_playlist"("user_uid" "text", "playlist_id" bigint, "playlist_description" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."set_description_playlist"("user_uid" "text", "playlist_name" "text", "playlist_description" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."set_description_playlist"("user_uid" "text", "playlist_name" "text", "playlist_description" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."set_description_playlist"("user_uid" "text", "playlist_name" "text", "playlist_description" "text") TO "service_role";

GRANT ALL ON FUNCTION "public"."set_public_playlist"("user_uid" "text", "playlist_id" bigint, "is_public" boolean) TO "anon";
GRANT ALL ON FUNCTION "public"."set_public_playlist"("user_uid" "text", "playlist_id" bigint, "is_public" boolean) TO "authenticated";
GRANT ALL ON FUNCTION "public"."set_public_playlist"("user_uid" "text", "playlist_id" bigint, "is_public" boolean) TO "service_role";

GRANT ALL ON FUNCTION "public"."set_public_playlist"("user_uid" "text", "playlist_name" "text", "is_public" boolean) TO "anon";
GRANT ALL ON FUNCTION "public"."set_public_playlist"("user_uid" "text", "playlist_name" "text", "is_public" boolean) TO "authenticated";
GRANT ALL ON FUNCTION "public"."set_public_playlist"("user_uid" "text", "playlist_name" "text", "is_public" boolean) TO "service_role";

GRANT ALL ON FUNCTION "public"."set_single_progress"("p_name" "text", "p_poster" "text", "season_id" "text", "p_season_name" "text", "user_uid" "text", "e_cur" double precision, "e_dur" double precision, "e_name" "text", "e_chap" "text") TO "anon";
GRANT ALL ON FUNCTION "public"."set_single_progress"("p_name" "text", "p_poster" "text", "season_id" "text", "p_season_name" "text", "user_uid" "text", "e_cur" double precision, "e_dur" double precision, "e_name" "text", "e_chap" "text") TO "authenticated";
GRANT ALL ON FUNCTION "public"."set_single_progress"("p_name" "text", "p_poster" "text", "season_id" "text", "p_season_name" "text", "user_uid" "text", "e_cur" double precision, "e_dur" double precision, "e_name" "text", "e_chap" "text") TO "service_role";

GRANT ALL ON TABLE "public"."chaps" TO "anon";
GRANT ALL ON TABLE "public"."chaps" TO "authenticated";
GRANT ALL ON TABLE "public"."chaps" TO "service_role";

GRANT ALL ON SEQUENCE "public"."chaps_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."chaps_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."chaps_id_seq" TO "service_role";

GRANT ALL ON TABLE "public"."history" TO "anon";
GRANT ALL ON TABLE "public"."history" TO "authenticated";
GRANT ALL ON TABLE "public"."history" TO "service_role";

GRANT ALL ON SEQUENCE "public"."history_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."history_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."history_id_seq" TO "service_role";

GRANT ALL ON TABLE "public"."movies" TO "anon";
GRANT ALL ON TABLE "public"."movies" TO "authenticated";
GRANT ALL ON TABLE "public"."movies" TO "service_role";

GRANT ALL ON SEQUENCE "public"."movies_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."movies_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."movies_id_seq" TO "service_role";

GRANT ALL ON TABLE "public"."playlist" TO "anon";
GRANT ALL ON TABLE "public"."playlist" TO "authenticated";
GRANT ALL ON TABLE "public"."playlist" TO "service_role";

GRANT ALL ON SEQUENCE "public"."playlist_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."playlist_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."playlist_id_seq" TO "service_role";

GRANT ALL ON TABLE "public"."users" TO "anon";
GRANT ALL ON TABLE "public"."users" TO "authenticated";
GRANT ALL ON TABLE "public"."users" TO "service_role";

GRANT ALL ON SEQUENCE "public"."users_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."users_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."users_id_seq" TO "service_role";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "service_role";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "service_role";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "service_role";

RESET ALL;
