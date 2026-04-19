# AI Skill Definitions

This document defines the atomic capabilities (Skills) available to the AI models within the AnimeVsub project.

## 1. PlaybackControlSkill
**ID**: `playback_control`
**Description**: Controls the active video player.
**Parameters**:
- `action` (String): "play", "pause", "seek", "skip_intro", "skip_outro", "set_speed".
- `value` (Any, optional): The value for the action (e.g., speed multiplier `1.5` or seek time in seconds).

## 2. ContentDiscoverySkill
**ID**: `content_discovery`
**Description**: Accesses the anime database and scrapers.
**Parameters**:
- `action` (String): "search", "get_detail", "get_chapters", "get_trending".
- `query` (String, optional): Search keywords.
- `anime_id` (String, optional): Unique ID for fetching details.

## 3. LibraryManagerSkill
**ID**: `library_manager`
**Description**: Manages user-specific data (Playlists, History).
**Parameters**:
- `action` (String): "get_playlists", "add_to_playlist", "remove_from_playlist", "get_history".
- `anime_id` (String, optional): Target anime ID.
- `playlist_id` (Int, optional): Target playlist ID.

## 4. TechnicalSupportSkill
**ID**: `tech_support`
**Description**: Diagnoses and fixes technical issues.
**Parameters**:
- `action` (String): "bypass_cloudflare", "change_server", "clear_cache".
