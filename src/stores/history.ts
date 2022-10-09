import { defineStore } from "pinia"

export const useHistoryStore = defineStore("history", {
  state: () => ({
    items: <
      Record<
        string,
        {
          first: string
          chaps: Record<string, { cur: number; dur: number }>
        }
      >
    >{},
  }),

  actions: {
    saveViewingProgress(options: {
      first: string
      season: string
      chap: string
      progress: { cur: number; dur: number }
    }) {
      // FLOW GO１１１１
      const seasonInMemory = this.items[options.season]

      if (seasonInMemory) {
        seasonInMemory.chaps[options.chap] = options.progress
        return
      }

      this.items[options.season] = {
        first: options.first,
        chaps: {
          [options.chap]: options.progress,
        },
      }
    },
    getProgressChap(options: {
      season: string;
      chap: string 
    }) {
      return this.items[ options.season ]?.chaps[options.chap] ?? null
    }
  },
})
