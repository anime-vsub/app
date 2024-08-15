import type { Database } from "app/database.d.ts"
import { i18n } from "boot/i18n"
import { defineStore } from "pinia"
import { supabase } from "src/boot/supabase"
import dayjs from "src/logic/dayjs"
import { addHostUrlImage, removeHostUrlImage } from "src/logic/urlImage"

import { useAuthStore } from "./auth"

export const usePlaylistStore = defineStore("playlist", () => {
  const authStore = useAuthStore()

  const playlistsError = ref<unknown | null>(null)

  const playlists = computedAsync(
    async () => {
      playlistsError.value = null
      if (!authStore.uid) return null

      const { data } = await supabase
        .rpc("get_list_playlist", {
          user_uid: authStore.uid
        })
        .throwOnError()

      return data
    },
    undefined,
    {
      lazy: true,
      shallow: true,
      onError(e) {
        playlistsError.value = e
      }
    }
  )

  async function createPlaylist(name: string, isPublic: boolean) {
    if (!authStore.uid)
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("tao-danh-sach-phat")
        ])
      )

    const { data } = await supabase
      .rpc("create_playlist", {
        user_uid: authStore.uid,
        playlist_name: name,
        is_public: isPublic
      })
      .single()
      .throwOnError()

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return data!
  }

  async function deletePlaylist(id: number) {
    if (!authStore.uid)
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xoa-danh-sach-phat")
        ])
      )

    await supabase
      .rpc("delete_playlist", {
        user_uid: authStore.uid,
        playlist_id: id
      })
      .throwOnError()
  }

  async function renamePlaylist(oldName: string, newName: string) {
    if (!authStore.uid)
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xoa-danh-sach-phat")
        ])
      )

    await supabase
      .rpc("rename_playlist", {
        user_uid: authStore.uid,
        old_name: oldName,
        new_name: newName
      })
      .throwOnError()
  }

  async function setDescriptionPlaylist(id: number, description: string) {
    if (!authStore.uid)
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xoa-danh-sach-phat")
        ])
      )

    await supabase
      .rpc("set_description_playlist", {
        user_uid: authStore.uid,
        playlist_id: id,
        playlist_description: description
      })
      .throwOnError()
  }

  async function publicPlaylist(id: number, isPublic: boolean) {
    if (!authStore.uid)
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xoa-danh-sach-phat")
        ])
      )

    await supabase
      .rpc("set_public_playlist", {
        user_uid: authStore.uid,
        playlist_id: id,
        is_public: isPublic
      })
      .throwOnError()
  }

  // prv
  async function addAnimeToPlaylist(
    id: number,
    anime: Omit<
      Database["public"]["Tables"]["movies"]["Insert"],
      "playlist_id" | "add_at"
    >
  ) {
    if (!authStore.uid)
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat")
        ])
      )

    const { data } = await supabase
      .rpc("add_movie_playlist", {
        user_uid: authStore.uid,
        playlist_id: id,

        p_chap: anime.chap,
        p_name: anime.name,
        p_name_chap: anime.name_chap,
        p_name_season: anime.name_season,
        p_poster: removeHostUrlImage(anime.poster),
        p_season: anime.season
      })
      .single()
      .throwOnError()

    const index = playlists.value?.findIndex((playlist) => playlist.id === id)
    if (index !== undefined && index > -1) {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      playlists.value![index] = {
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        ...playlists.value![index],
        ...(data ?? {})
      }
    }
  }

  async function deleteAnimeFromPlaylist(id: number, season: string) {
    if (!authStore.uid)
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat")
        ])
      )

    const { data } = await supabase
      .rpc("delete_movie_playlist", {
        user_uid: authStore.uid,
        playlist_id: id,
        p_season: season
      })
      .throwOnError()

    const index = playlists.value?.findIndex((playlist) => playlist.id === id)
    if (index !== undefined && index > -1) {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      playlists.value![index] = {
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        ...playlists.value![index],
        ...(data ?? {})
      }
    }
  }

  async function hasAnimeOfPlaylists(ids: number[], season: string) {
    if (!authStore.uid)
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat")
        ])
      )

    const { data } = await supabase
      .rpc("has_movie_playlists", {
        user_uid: authStore.uid,
        playlist_ids: ids,
        season_id: season
      })
      .throwOnError()

    const map = new Map<number, boolean>()
    data?.forEach((item) => {
      map.set(item.playlist_id, item.has_movie)
    })

    return ids.map((item) => map.get(item) ?? false)
  }

  async function getAnimesFromPlaylist(
    id: number,
    page: number,
    sorter: "asc" | "desc" = "desc"
  ) {
    if (!authStore.uid)
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat")
        ])
      )

    const { data } = await supabase
      .rpc("get_movies_playlist", {
        user_uid: authStore.uid,
        playlist_id: id,
        sorter,
        page,
        page_size: 30
      })
      .throwOnError()

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return data!.map((item) => {
      return {
        ...item,
        poster: addHostUrlImage(item.poster),
        add_at: dayjs(item.add_at)
      }
    })
  }

  async function getPosterPlaylist(id: number) {
    if (!authStore.uid)
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat")
        ])
      )

    const { data } = await supabase
      .rpc("get_poster_playlist", {
        user_uid: authStore.uid,
        playlist_id: id
      })
      .single()
      .throwOnError()

    if (data) return addHostUrlImage(data?.poster)
  }

  return {
    playlists,
    playlistsError,
    // refreshPlaylists,
    // getMetaPlaylist,
    createPlaylist,
    deletePlaylist,
    renamePlaylist,
    setDescriptionPlaylist,
    publicPlaylist,
    getPosterPlaylist,

    addAnimeToPlaylist,
    deleteAnimeFromPlaylist,
    hasAnimeOfPlaylist: hasAnimeOfPlaylists,

    getAnimesFromPlaylist
  }
})