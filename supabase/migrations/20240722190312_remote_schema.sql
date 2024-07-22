drop function if exists "public"."add_movie_playlist"(user_uid text, playlist_name text, p_chap text, p_name text, p_name_chap text, p_name_season text, p_poster text, p_season text);

drop function if exists "public"."delete_movie_playlist"(user_uid text, q_name text, p_season text);

drop function if exists "public"."delete_playlist"(user_uid text, playlist_name text);

drop function if exists "public"."get_last_chap"(user_uid text, season text);

drop function if exists "public"."get_poster_playlist"(user_uid text, playlist_name text);

drop function if exists "public"."has_movie_playlist"(user_uid text, playlist_name text, season_id text);

drop function if exists "public"."set_description_playlist"(user_uid text, playlist_name text, playlist_description text);

drop function if exists "public"."set_public_playlist"(user_uid text, playlist_name text, is_public boolean);

drop function if exists "public"."delete_playlist"(user_uid text, playlist_id bigint);

drop function if exists "public"."set_public_playlist"(user_uid text, playlist_id bigint, is_public boolean);

alter table "public"."chaps" enable row level security;

alter table "public"."history" enable row level security;

alter table "public"."movies" enable row level security;

alter table "public"."playlist" enable row level security;

alter table "public"."users" enable row level security;

set check_function_bodies = off;

CREATE OR REPLACE FUNCTION public.get_last_chap(user_uid text, season_id text)
 RETURNS TABLE(created_at timestamp with time zone, cur double precision, dur double precision, name text, updated_at timestamp with time zone, chap_id text)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
BEGIN
    RETURN QUERY
    SELECT c.created_at, c.cur, c.dur, c.name, c.updated_at, c.chap_id
    FROM public.chaps c
    JOIN public.history h ON c.history_id = h.id
    JOIN public.users u ON h.user_id = u.id
    WHERE u.uuid = user_uid
    AND h.season = season_id
    order by updated_at desc
    limit 1;
END;
$function$
;

CREATE OR REPLACE FUNCTION public.upsert_user(p_uuid text, p_email text DEFAULT NULL::text, p_name text DEFAULT NULL::text)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
BEGIN
    
    if not exists(select * from users where users.uuid = p_uuid limit 1) then
      INSERT INTO users (uuid, email, name, created_at)
      VALUES (p_uuid, p_email, p_name, now());
    else
      update users set email = p_email, name = p_name where uuid = p_uuid;
    end if;
END;
$function$
;

CREATE OR REPLACE FUNCTION public.add_movie_playlist(user_uid text, playlist_id bigint, p_chap text, p_name text, p_name_chap text, p_name_season text, p_poster text, p_season text)
 RETURNS TABLE(id bigint, created_at timestamp with time zone, public boolean, name text, description text, updated_at timestamp with time zone, movies_count bigint)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
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
$function$
;

CREATE OR REPLACE FUNCTION public.create_playlist(user_uid text, playlist_name text, is_public boolean)
 RETURNS TABLE(id bigint, created_at timestamp with time zone, public boolean, name text, description text, updated_at timestamp with time zone)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
    DECLARE new_playlist_id bigint;
    BEGIN
        INSERT INTO public.playlist (created_at, public, name, user_id)
        VALUES (now(), is_public, playlist_name, (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1))
        RETURNING id, created_at, public, name, null::text, now(), user_id INTO new_playlist_id;
        
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
AS $function$
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
$function$
;

CREATE OR REPLACE FUNCTION public.delete_playlist(user_uid text, playlist_id bigint)
 RETURNS void
 LANGUAGE sql
 SECURITY DEFINER
AS $function$
    DELETE FROM public.playlist
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND id = playlist_id;
$function$
;

CREATE OR REPLACE FUNCTION public.get_list_playlist(user_uid text)
 RETURNS TABLE(id bigint, created_at timestamp with time zone, public boolean, name text, description text, updated_at timestamp with time zone, movies_count bigint)
 LANGUAGE sql
 SECURITY DEFINER
AS $function$
    SELECT p.id, p.created_at, p.public, p.name, p.description, p.updated_at, 
           (SELECT COUNT(*) FROM public.movies m WHERE m.playlist_id = p.id) as movies_count
    FROM public.playlist p
    JOIN public.users u ON p.user_id = u.id
    WHERE u.uuid = user_uid
    ORDER BY p.created_at DESC;
$function$
;

CREATE OR REPLACE FUNCTION public.get_movies_playlist(user_uid text, playlist_id bigint, sorter text, page integer, page_size integer)
 RETURNS TABLE(add_at timestamp with time zone, name_chap text, name text, chap text, poster text, name_season text, season text)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
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
$function$
;

CREATE OR REPLACE FUNCTION public.get_poster_playlist(user_uid text, playlist_id bigint)
 RETURNS TABLE(poster text)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
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
$function$
;

CREATE OR REPLACE FUNCTION public.get_single_progress(user_uid text, season_id text, p_chap_id text)
 RETURNS TABLE(created_at timestamp with time zone, cur double precision, dur double precision, name text, updated_at timestamp with time zone)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
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
$function$
;

CREATE OR REPLACE FUNCTION public.get_watch_progress(user_uid text, season_id text)
 RETURNS TABLE(created_at timestamp with time zone, cur double precision, dur double precision, name text, updated_at timestamp with time zone, chap_id text)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
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
$function$
;

CREATE OR REPLACE FUNCTION public.has_movie_playlist(user_uid text, playlist_id bigint, season_id text)
 RETURNS TABLE(has_movie boolean)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
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
$function$
;

CREATE OR REPLACE FUNCTION public.has_movie_playlists(user_uid text, playlist_ids bigint[], season_id text)
 RETURNS TABLE(playlist_id bigint, has_movie boolean)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
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
$function$
;

CREATE OR REPLACE FUNCTION public.query_history(user_uid text, page integer, size integer)
 RETURNS TABLE(created_at timestamp with time zone, season text, name text, poster text, season_name text, watch_updated_at timestamp with time zone, watch_name text, watch_id text, watch_cur double precision, watch_dur double precision)
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
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
$function$
;

CREATE OR REPLACE FUNCTION public.rename_playlist(user_uid text, old_name text, new_name text)
 RETURNS void
 LANGUAGE sql
 SECURITY DEFINER
AS $function$
    UPDATE public.playlist
    SET name = new_name
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND name = old_name;
$function$
;

CREATE OR REPLACE FUNCTION public.set_description_playlist(user_uid text, playlist_id bigint, playlist_description text)
 RETURNS void
 LANGUAGE sql
 SECURITY DEFINER
AS $function$
    UPDATE public.playlist
    SET description = playlist_description
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND id = playlist_id;
$function$
;

CREATE OR REPLACE FUNCTION public.set_public_playlist(user_uid text, playlist_id bigint, is_public boolean)
 RETURNS void
 LANGUAGE sql
 SECURITY DEFINER
AS $function$
    UPDATE public.playlist
    SET public = is_public
    WHERE user_id = (SELECT id FROM public.users WHERE uuid = user_uid LIMIT 1)
    AND id = playlist_id;
$function$
;

CREATE OR REPLACE FUNCTION public.set_single_progress(p_name text, p_poster text, season_id text, p_season_name text, user_uid text, e_cur double precision, e_dur double precision, e_name text, e_chap text)
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
$function$
;

create policy "Disable all"
on "public"."chaps"
as permissive
for all
to public
using (false);


create policy "Disable all"
on "public"."history"
as permissive
for all
to public
using (false);


create policy "Disable all"
on "public"."movies"
as permissive
for all
to public
using (false);


create policy "Disable all"
on "public"."playlist"
as permissive
for all
to public
using (false);


create policy "Disable all"
on "public"."users"
as permissive
for all
to public
using (false);



