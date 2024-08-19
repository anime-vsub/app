create extension if not exists "wrappers" with schema "extensions";


alter table "public"."playlist" alter column "description" set default '''''::text'::text;

alter table "public"."users" add column "last_signin" timestamp with time zone;

CREATE INDEX chaps_history_id_idx ON public.chaps USING btree (history_id);

CREATE INDEX history_for_to_idx ON public.history USING btree (for_to);

CREATE INDEX history_user_id_idx ON public.history USING btree (user_id);

CREATE INDEX movies_playlist_id_idx ON public.movies USING btree (playlist_id);

CREATE INDEX playlist_user_id_idx ON public.playlist USING btree (user_id);

CREATE UNIQUE INDEX unique_chap_history_id ON public.chaps USING btree (chap_id, history_id);

set check_function_bodies = off;

CREATE OR REPLACE FUNCTION public.set_single_progress(p_name text, p_poster text, season_id text, p_season_name text, user_uid text, e_cur double precision, e_dur double precision, e_name text, e_chap text, gmt text)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
DECLARE
    latest_history record;
    i_user_id int8;
    id_history_rax int8;
    id_chap int8;
BEGIN
    -- Check if user exists and get user id
    SELECT u.id INTO i_user_id
    FROM users u
    WHERE u.uuid = user_uid
    LIMIT 1;

    IF i_user_id IS NULL THEN
        RAISE EXCEPTION 'User does not exist';
    END IF;

    IF e_dur <= 0 THEN
        RAISE EXCEPTION 'Duration must be greater than 0';
    END IF;

    IF e_dur < e_cur THEN
        RAISE EXCEPTION 'Duration must be current';
    END IF;

    -- Get the latest history record for the user and season
    SELECT h.* INTO latest_history
    FROM history h
    JOIN users u ON h.user_id = u.id
    WHERE u.uuid = user_uid AND h.season = season_id
    ORDER BY h.created_at DESC
    LIMIT 1;

    -- Insert new history if it does not exist or is not the latest for today
    IF latest_history IS NULL OR (latest_history.created_at::timestampz at TIME ZONE gmt)::DATE <> (NOW()::timestampz at TIME ZONE gmt)::DATE THEN
        INSERT INTO history (created_at, user_id, season, name, poster, season_name, for_to)
        VALUES (NOW(), i_user_id, season_id, p_name, p_poster, p_season_name, NULL);
    END IF;

    -- Get the latest history record ID for the user and season
    SELECT h.id INTO id_history_rax
    FROM history h
    JOIN users u ON h.user_id = u.id
    WHERE u.uuid = user_uid AND h.season = season_id AND h.for_to IS NULL
    ORDER BY h.created_at DESC
    LIMIT 1;

    IF id_history_rax IS NULL THEN
        RAISE EXCEPTION 'Failed to retrieve or create history record';
    END IF;

    -- Check if the chapter already exists for the history record
    SELECT c.id INTO id_chap
    FROM chaps c
    JOIN history h ON c.history_id = h.id
    JOIN users u ON h.user_id = u.id
    WHERE u.uuid = user_uid AND h.season = season_id AND c.chap_id = e_chap
    LIMIT 1;

    -- Insert or update the chapter
    IF id_chap IS NULL THEN
        DELETE FROM chaps
        USING history h
        JOIN users u ON h.user_id = u.id
        WHERE chaps.history_id = h.id AND h.season = season_id AND chaps.chap_id = e_chap AND u.uuid = user_uid;
        
        INSERT INTO chaps (created_at, history_id, cur, dur, name, updated_at, chap_id)
        VALUES (NOW(), id_history_rax, e_cur, e_dur, e_name, NOW(), e_chap);
    ELSE
        DELETE FROM chaps
        USING history h
        JOIN users u ON h.user_id = u.id
        WHERE chaps.history_id = h.id AND h.season = season_id AND chaps.chap_id = e_chap AND u.uuid = user_uid
        AND NOT chaps.id = id_chap;

        UPDATE chaps
        SET cur = e_cur, dur = e_dur, updated_at = NOW()
        WHERE id = id_chap;
    END IF;
END;
$function$
;

CREATE OR REPLACE FUNCTION public.add_movie_playlist(user_uid text, playlist_id bigint, p_chap text, p_name text, p_name_chap text, p_name_season text, p_poster text, p_season text)
 RETURNS TABLE(id bigint, created_at timestamp with time zone, public boolean, name text, description text, updated_at timestamp with time zone, movies_count bigint)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$DECLARE
  p_playlist_id int8;
BEGIN

    select p.id into p_playlist_id
    from playlist p
    join users u on p.user_id = u.id
    where u.uuid = user_uid
    and p.id = playlist_id
    limit 1;
    
    if p_playlist_id is null then
      raise EXCEPTION 'Playlist does not exist';
    else
      if not exists(select * from movies m where m.playlist_id = p_playlist_id and m.season = p_season limit 1) then
        insert into movies (add_at, chap, name, name_chap, name_season, playlist_id, poster, season)
        values (now(), p_chap, p_name, p_name_chap, p_name_season, p_playlist_id, p_poster, p_season);
      end if;
    end if;

    return query 
    SELECT p.id, p.created_at, p.public, p.name, p.description, p.updated_at, 
           (SELECT COUNT(*) FROM public.movies m WHERE m.playlist_id = p.id) as movies_count
    FROM public.playlist p
    JOIN public.users u ON p.user_id = u.id
    WHERE u.uuid = user_uid
    AND p.id = p_playlist_id
    limit 1;
END;$function$
;

CREATE OR REPLACE FUNCTION public.create_playlist(user_uid text, playlist_name text, is_public boolean)
 RETURNS TABLE(id bigint, created_at timestamp with time zone, public boolean, name text, description text, updated_at timestamp with time zone)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
    DECLARE new_playlist_id bigint;
    begin

        INSERT INTO public.playlist (created_at, updated_at, public, name, user_id)
        VALUES (now(), now(), is_public, playlist_name, (SELECT public.users.id FROM public.users WHERE uuid = user_uid LIMIT 1))
        RETURNING public.playlist.id, public.playlist.created_at, public.playlist.public, public.playlist.name, null::text, now(), user_id INTO new_playlist_id;
        
        RETURN QUERY SELECT p.id, p.created_at, p.public, p.name, p.description, p.updated_at
        FROM public.playlist p
        WHERE p.id = new_playlist_id;
    
    END;
$function$
;

CREATE OR REPLACE FUNCTION public.delete_movie_playlist(user_uid text, playlist_id bigint, p_season text)
 RETURNS TABLE(id bigint, created_at timestamp with time zone, public boolean, name text, description text, updated_at timestamp with time zone, movies_count bigint)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$DECLARE
  p_playlist_id int8;
BEGIN
    p_playlist_id := playlist_id;
    
    DELETE FROM movies
    USING playlist
    JOIN users ON playlist.user_id = users.id
    WHERE movies.playlist_id = playlist.id
    AND users.uuid = user_uid
    AND playlist.id = p_playlist_id
    AND movies.season = p_season;
    
    RETURN QUERY 
    SELECT p.id, p.created_at, p.public, p.name, p.description, p.updated_at, 
           (SELECT COUNT(*) FROM public.movies m WHERE m.playlist_id = p.id) as movies_count
    FROM public.playlist p
    JOIN public.users u ON p.user_id = u.id
    WHERE u.uuid = user_uid
    AND p.id = p_playlist_id
    LIMIT 1;
END;$function$
;

CREATE OR REPLACE FUNCTION public.get_single_progress(user_uid text, season_id text, p_chap_id text)
 RETURNS TABLE(created_at timestamp with time zone, cur double precision, dur double precision, name text, updated_at timestamp with time zone)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$BEGIN
    RETURN QUERY
    SELECT c.created_at, c.cur, c.dur, c.name, c.updated_at
    FROM public.chaps c
    JOIN public.history h ON c.history_id = h.id
    JOIN public.users u ON h.user_id = u.id
    WHERE u.uuid = user_uid
    AND h.season = season_id
    AND c.chap_id = p_chap_id
    ORDER BY c.updated_at DESC
    limit 1;
END;$function$
;

CREATE OR REPLACE FUNCTION public.set_single_progress(p_name text, p_poster text, season_id text, p_season_name text, user_uid text, e_cur double precision, e_dur double precision, e_name text, e_chap text)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$DECLARE
    latest_history record;
    i_user_id int8;
    id_history_rax int8;
    id_chap int8;
BEGIN
    -- Check if user exists and get user id
    SELECT u.id INTO i_user_id
    FROM users u
    WHERE u.uuid = user_uid
    LIMIT 1;

    IF i_user_id IS NULL THEN
        RAISE EXCEPTION 'User does not exist';
    END IF;

    IF e_dur <= 0 THEN
        RAISE EXCEPTION 'Duration must be greater than 0';
    END IF;

    IF e_dur < e_cur THEN
        RAISE EXCEPTION 'Duration must be current';
    END IF;

    -- Get the latest history record for the user and season
    SELECT h.* INTO latest_history
    FROM history h
    JOIN users u ON h.user_id = u.id
    WHERE u.uuid = user_uid AND h.season = season_id
    ORDER BY h.created_at DESC
    LIMIT 1;

    -- Insert new history if it does not exist or is not the latest for today
    IF latest_history IS NULL OR latest_history.created_at::DATE AT TIME ZONE 'GMT' <> NOW()::DATE AT TIME ZONE 'GMT' THEN
        INSERT INTO history (created_at, user_id, season, name, poster, season_name, for_to)
        VALUES (NOW(), i_user_id, season_id, p_name, p_poster, p_season_name, NULL);
    END IF;

    -- Get the latest history record ID for the user and season
    SELECT h.id INTO id_history_rax
    FROM history h
    JOIN users u ON h.user_id = u.id
    WHERE u.uuid = user_uid AND h.season = season_id AND h.for_to IS NULL
    ORDER BY h.created_at DESC
    LIMIT 1;

    IF id_history_rax IS NULL THEN
        RAISE EXCEPTION 'Failed to retrieve or create history record';
    END IF;

    -- Check if the chapter already exists for the history record
    SELECT c.id INTO id_chap
    FROM chaps c
    JOIN history h ON c.history_id = h.id
    JOIN users u ON h.user_id = u.id
    WHERE u.uuid = user_uid AND h.season = season_id AND c.chap_id = e_chap
    LIMIT 1;

    -- Insert or update the chapter
    IF id_chap IS NULL THEN
        DELETE FROM chaps
        USING history h
        JOIN users u ON h.user_id = u.id
        WHERE chaps.history_id = h.id AND h.season = season_id AND chaps.chap_id = e_chap AND u.uuid = user_uid;
        
        INSERT INTO chaps (created_at, history_id, cur, dur, name, updated_at, chap_id)
        VALUES (NOW(), id_history_rax, e_cur, e_dur, e_name, NOW(), e_chap);
    ELSE
        DELETE FROM chaps
        USING history h
        JOIN users u ON h.user_id = u.id
        WHERE chaps.history_id = h.id AND h.season = season_id AND chaps.chap_id = e_chap AND u.uuid = user_uid
        AND NOT chaps.id = id_chap;

        UPDATE chaps
        SET cur = e_cur, dur = e_dur, updated_at = NOW()
        WHERE id = id_chap;
    END IF;
END;$function$
;

CREATE OR REPLACE FUNCTION public.upsert_user(p_uuid text, p_email text DEFAULT NULL::text, p_name text DEFAULT NULL::text)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$BEGIN
    
    if not exists(select * from users where users.uuid = p_uuid limit 1) then
      INSERT INTO users (uuid, email, name, created_at, last_signin)
      VALUES (p_uuid, p_email, p_name, now(), now());
    else
      update users set email = p_email, name = p_name, last_signin = now() where uuid = p_uuid;
    end if;
END;$function$
;


