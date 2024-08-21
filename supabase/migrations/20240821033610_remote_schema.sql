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
    IF latest_history IS NULL OR (latest_history.created_at::timestamptz at TIME ZONE gmt)::DATE <> (NOW()::timestamptz at TIME ZONE gmt)::DATE THEN
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


