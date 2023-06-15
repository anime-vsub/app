<template>
  <q-layout view="hHh Lpr lFf">
    <q-header
      class="bg-dark-page py-1 px-2"
      :class="{
        '!bg-transparent': route.meta?.transparentHeader,
      }"
    >
      <q-toolbar>
        <q-btn
          dense
          flat
          round
          icon="menu"
          class="mr-5"
          @click="showDrawer = !showDrawer"
        />

        <router-link to="/" class="flex flex-nowrap items-end">
          <img src="~assets/app_icon.svg" width="35" height="35" />
          <span style="font-family: Caveat" class="text-[25px]">nimeVsub</span>
        </router-link>

        <q-space />

        <form
          @submit.prevent="router.push(`/tim-kiem/${query}`)"
          class="relative md:min-w-[164px] md:w-full max-w-[598px]"
        >
          <q-input
            v-model="query"
            dense
            rounded
            outlined
            clearable
            class="font-weight-normal input-search bg-[rgba(255,255,255,0)] w-full"
            input-style="background-color: transparent"
            :placeholder="t('tim-kiem')"
            @focus="focusing = true"
            @blur="focusing = false"
            @keydown.stop
            ref="inputSearchRef"
          >
            <template v-slot:append>
              <q-separator vertical inset class="bg-[rgba(153,153,153,0.3)]" />
              <button
                type="submit"
                class="flex items-center"
                @click.stop.prevent="router.push(`/tim-kiem/${query}`)"
                @mousedown.stop.prevent
              >
                <q-icon name="search" class="pl-6 pr-4 cursor-pointer" />
              </button>
            </template>
          </q-input>

          <transition name="q-transition--fade">
            <ul
              class="absolute w-full bg-dark-page left-0 max-h-[80vh] overflow-y-auto scrollbar-custom pb-4 top-[calc(100%+8px)] !shadow-8"
              v-show="focusing"
              @click.stop.prevent
              @mousedown="
                (event) => {
                  if (event.button === 2) event.preventDefault()
                }
              "
            >
              <li
                v-if="query"
                class="px-4 mt-1 py-[0.5rem] flex items-center w-full justify-between"
              >
                <div>
                  <span class="text-gray-400 mr-1"
                    >{{ t("tim-kiem-_keyword") }}
                  </span>
                  <span class="font-bold truncate">{{ query }}</span>
                </div>

                <button class="key-enter" type="submit">
                  <span>{{ t("enter") }}</span>
                </button>
              </li>
              <li
                v-if="searchLoading"
                v-for="i in 12"
                :key="i"
                class="flex mt-5 mx-4"
              >
                <q-responsive :ratio="267 / 400" class="w-[90px] rounded">
                  <q-skeleton type="rect" class="absolute w-full h-full" />
                </q-responsive>

                <div class="ml-2 flex-1">
                  <q-skeleton type="text" width="60%" />
                  <q-skeleton type="text" width="100px" height="15px" />
                </div>
              </li>
              <li
                v-else-if="searchResult?.length"
                v-for="item in searchResult"
                :key="item.path"
                class="relative"
                v-ripple
              >
                <router-link
                  :to="item.path"
                  class="flex flex-nowrap mt-5 mx-4"
                  @click="saveAnalytics"
                >
                  <div>
                    <q-img-custom
                      :ratio="267 / 400"
                      :src="forceHttp2(item.image)"
                      referrerpolicy="no-referrer"
                      width="90px"
                      class="rounded"
                    />
                  </div>

                  <div class="ml-2">
                    <div class="text-subtitle1 text-weight-medium">
                      {{ item.name }}
                    </div>
                    <div class="text-gray-500">{{ item.status }}</div>
                  </div>
                </router-link>
              </li>
              <li v-else class="px-4 py-5 text-center text-gray-400 w-full">
                {{ query ? "Không tìm thấy" : "Nhập để tìm kiếm" }}
              </li>
            </ul>
          </transition>
        </form>

        <q-space />

        <q-btn round unelevated class="mr-2">
          <q-circular-progress
            v-if="updatingCache && installedSW"
            indeterminate
            rounded
            show-value
            size="35px"
            color="main"
          >
            <Icon icon="codicon:github-inverted" width="24" height="24" />
          </q-circular-progress>
          <Icon v-else icon="codicon:github-inverted" width="24" height="24" />

          <q-menu
            anchor="bottom right"
            self="top right"
            class="rounded-xl bg-dark-page shadow-xl"
          >
            <q-card class="transparent w-[280px] px-2 pb-3">
              <q-list>
                <q-item class="rounded-xl">
                  <q-item-section class="text-[15px]">
                    {{ t("ve-ung-dung") }}
                  </q-item-section>
                </q-item>

                <!-- <q-separator class="bg-[rgba(255,255,255,0.1)]" /> -->

                <q-item
                  clickable
                  v-ripple
                  class="rounded-xl"
                  target="_blank"
                  href="https://github.com/anime-vsub/desktop-web"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      icon="carbon:repo-source-code"
                      width="20"
                      height="20"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{
                      t("ma-nguon-mo-tren-github")
                    }}</q-item-label>
                  </q-item-section>
                </q-item>
                <q-item
                  clickable
                  v-ripple
                  class="rounded-xl"
                  target="_blank"
                  href="https://github.com/anime-vsub/desktop-web/issues"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      icon="fluent:person-feedback-24-regular"
                      width="20"
                      height="20"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{
                      t("phan-hoi-hoac-bao-loi")
                    }}</q-item-label>
                  </q-item-section>
                </q-item>
                <q-item
                  clickable
                  v-ripple
                  class="rounded-xl"
                  target="_blank"
                  href="https://github.com/anime-vsub/desktop-web/discussions"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      icon="fluent:plug-disconnected-24-regular"
                      width="20"
                      height="20"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ t("thao-luan") }}</q-item-label>
                  </q-item-section>
                </q-item>
                <q-item
                  clickable
                  v-ripple
                  class="rounded-xl"
                  target="_blank"
                  href="https://anime-vsub.github.io/about/sponsors"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      icon="octicon:sponsor-tiers-24"
                      width="20"
                      height="20"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ t("tai-tro-ung-ho") }}</q-item-label>
                  </q-item-section>
                </q-item>
                <q-item
                  clickable
                  v-ripple
                  class="rounded-xl"
                  @click="checkForUpdate"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      icon="charm:refresh"
                      width="20"
                      height="20"
                      :class="{
                        'animate-spin': checkingForUpdate,
                      }"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ t("kiem-tra-cap-nhat") }}</q-item-label>
                    <q-item-label caption>
                      {{ version }}
                      <template v-if="newVersionAble">
                        ({{
                          t("da-co-ban-cap-nhat-moi-_newVersion", [
                            newVersionAble,
                          ])
                        }}
                        &bull;
                        <q-btn flat rounded no-caps @click="updateApp">{{
                          t("cap-nhat")
                        }}</q-btn
                        >)
                      </template>
                    </q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-card>
          </q-menu>
        </q-btn>

        <q-btn v-if="authStore.isLogged" round unelevated class="mr-2">
          <Icon
            :icon="
              showMenuFollow
                ? 'majesticons:bookmark-plus'
                : 'majesticons:bookmark-plus-line'
            "
            width="24"
            height="24"
          />

          <q-menu
            v-model="showMenuFollow"
            class="flex flex-nowrap column bg-dark-page shadow-xl"
          >
            <q-card
              class="transparent shadow-none w-[415px] scrollbar-custom overflow-y-auto"
            >
              <q-card-section>
                <SkeletonCardVertical
                  v-if="loadingFavorites"
                  v-for="i in 12"
                  :key="i"
                  class="mb-4"
                />
                <CardVertical
                  v-else-if="favorites"
                  v-for="item in favorites?.items"
                  :key="item.path"
                  :data="item"
                  class="mb-4"
                />
                <div v-else class="text-center">
                  <div class="text-subtitle1 font-weight-medium">
                    {{ t("loi-khong-xac-dinh") }}
                  </div>
                  <q-btn
                    outline
                    rounded
                    color="main"
                    @click="refreshFavorites"
                    >{{ t("thu-lai") }}</q-btn
                  >
                </div>
              </q-card-section>
            </q-card>

            <router-link
              to="/tai-khoan/follow"
              class="block py-2 text-center"
              >{{ t("xem-tat-ca") }}</router-link
            >
          </q-menu>
        </q-btn>

        <q-btn v-if="authStore.isLogged" round unelevated class="mr-2">
          <Icon
            :icon="
              showMenuHistory
                ? 'fluent:clock-24-filled'
                : 'fluent:clock-24-regular'
            "
            width="24"
            height="24"
          />

          <q-menu
            v-model="showMenuHistory"
            class="flex flex-nowrap column bg-dark-page shadow-xl"
          >
            <q-card
              class="transparent shadow-none w-[415px] scrollbar-custom overflow-y-auto"
            >
              <q-card-section>
                <div
                  v-if="loadingHistories"
                  v-for="i in 12"
                  :key="i"
                  class="flex mt-1 mb-4 flex-nowrap"
                >
                  <q-responsive :ratio="1920 / 1080" class="w-[149px]">
                    <q-skeleton class="!rounded-[4px] absolute w-full h-full" />
                  </q-responsive>

                  <div class="pl-2 flex-1">
                    <q-skeleton type="text" class="mt-1" width="60%" />
                    <q-skeleton
                      type="text"
                      class="mt-1"
                      width="40px"
                      height="15px"
                    />

                    <div class="text-grey mt-1">
                      <q-skeleton
                        type="text"
                        class="mt-1"
                        width="60px"
                        height="15px"
                      />
                    </div>
                    <div class="text-grey mt-2">
                      <q-skeleton
                        type="text"
                        class="mt-1"
                        width="120px"
                        height="15px"
                      />
                    </div>
                  </div>
                </div>

                <template v-else-if="histories">
                  <div v-if="histories.length === 0" class="text-center">
                    <div class="text-gray-400 text-subtitle1 py-2">
                      {{ t("chua-co-lich-su-xem") }}
                    </div>
                  </div>

                  <template
                    v-else
                    v-for="(item, index) in histories.map((item) => {
                      return {
                        ...item,
                        timestamp: dayjs(item.timestamp.toDate()),
                      }
                    })"
                    :key="item.id"
                  >
                    <div
                      v-if="
                        !histories[index - 1] ||
                        !dayjs(histories[index - 1].timestamp.toDate()).isSame(
                          item.timestamp,
                          'day'
                        )
                      "
                      class="text-subtitle2 text-weight-normal"
                    >
                      {{
                        item.timestamp.isToday()
                          ? "Hôm nay"
                          : item.timestamp.isYesterday()
                          ? "Hôm qua"
                          : item.timestamp.get("date") +
                            " thg " +
                            (item.timestamp.get("month") + 1)
                      }}
                    </div>
                    <router-link
                      class="bg-transparent flex mt-1 mb-4 flex-nowrap"
                      style="white-space: initial"
                      :to="`/phim/${item.season ?? item.id}/${parseChapName(
                        item.last.name
                      )}-${item.last.chap}`"
                    >
                      <div class="w-[149px]">
                        <q-img-custom
                          no-spinner
                          :src="forceHttp2(item.poster)"
                          referrerpolicy="no-referrer"
                          :ratio="1920 / 1080"
                          class="!rounded-[4px]"
                        >
                          <BottomBlur class="px-0 h-[40%]">
                            <div
                              class="absolute bottom-0 left-0 z-10 w-full min-h-0 !py-0 !px-0"
                            >
                              <q-linear-progress
                                :value="item.last.cur / item.last.dur"
                                rounded
                                color="main"
                                class="!h-[3px]"
                              />
                            </div>
                          </BottomBlur>
                          <span
                            class="absolute text-white z-10 text-[12px] bottom-2 right-2"
                            >{{ parseTime(item.last.cur) }}</span
                          >
                        </q-img-custom>
                      </div>

                      <div class="pl-2 flex-1">
                        <span class="line-clamp-3 mt-1">{{ item.name }}</span>
                        <div class="text-grey mt-1">
                          <template v-if="item.seasonName"
                            >{{ t("_season-tap", [item.seasonName]) }}
                          </template>
                          <template v-else>{{ t("Tap") }}</template>
                          {{ item.last.name }}
                        </div>
                        <div class="text-grey mt-2">
                          {{
                            t("xem-luc-_value", [
                              item.timestamp.format(
                                item.timestamp.isToday()
                                  ? "HH:mm"
                                  : "DD/MM/YYYY"
                              ),
                            ])
                          }}
                        </div>
                      </div>
                    </router-link>
                  </template>
                </template>

                <div v-else class="text-center">
                  <div class="text-subtitle1 font-weight-medium">
                    {{ t("loi-khong-xac-dinh") }}
                  </div>
                  <q-btn
                    outline
                    rounded
                    color="main"
                    @click="refreshHistories"
                    >{{ t("thu-lai") }}</q-btn
                  >
                </div>
              </q-card-section>
            </q-card>

            <router-link
              to="/tai-khoan/history"
              class="block py-2 text-center"
              >{{ t("xem-tat-ca") }}</router-link
            >
          </q-menu>
        </q-btn>

        <q-btn v-if="authStore.isLogged" round unelevated class="mr-2">
          <Icon
            :icon="
              showMenuNotify
                ? 'clarity:notification-solid'
                : 'clarity:notification-line'
            "
            width="24"
            height="24"
          />

          <q-badge
            floating
            rounded
            transparent
            class="top-0"
            :label="notificationStore.max"
          />

          <q-menu
            v-model="showMenuNotify"
            class="bg-dark-page scrollbar-custom shadow-xl"
          >
            <q-card class="bg-transparent max-w-[435px]">
              <q-card-section>
                <q-list v-if="notificationStore.loading" class="bg-transparent">
                  <q-item v-for="item in 12" :key="item" class="rounded-xl">
                    <q-item-section>
                      <q-item-label class="text-subtitle1 text-weight-normal">
                        <q-skeleton type="text" width="40%" />
                        <q-skeleton type="text" width="60%" />
                      </q-item-label>
                      <q-item-label>
                        <q-skeleton type="text" width="100" height="15px" />
                      </q-item-label>
                    </q-item-section>
                    <q-item-section side>
                      <q-responsive
                        :ratio="120 / 81"
                        class="w-[120px] rounded-sm"
                      >
                        <q-skeleton
                          type="rect"
                          class="absolute w-full h-full"
                        />
                      </q-responsive>
                    </q-item-section>
                  </q-item>
                </q-list>

                <q-list v-else class="bg-transparent">
                  <transition-group name="notify">
                    <q-item
                      v-for="item in notificationStore.items"
                      :key="item.id"
                      :to="item.path"
                      class="hidden-focus-helper"
                    >
                      <q-item-section>
                        <q-item-label class="text-subtitle1 text-weight-normal"
                          >{{ item.name }}
                          <span class="text-grey">
                            {{ t("da-cap-nhat") }}
                          </span>
                          {{ item.chap }}</q-item-label
                        >
                        <q-item-label class="text-grey">{{
                          item.time
                        }}</q-item-label>
                      </q-item-section>
                      <q-item-section side>
                        <div class="flex flex-nowrap">
                          <q-img-custom
                            no-spinner
                            :src="forceHttp2(item.image!)"
                            referrerpolicy="no-referrer"
                            width="128px"
                            :ratio="120 / 81"
                            class="rounded-sm"
                          />
                          <div class="mr-[-32px]">
                            <q-btn
                              round
                              dense
                              unelevated
                              icon="close"
                              @click.prevent="notificationStore.remove(item.id)"
                            />
                          </div>
                        </div>
                      </q-item-section>
                    </q-item>
                  </transition-group>
                </q-list>

                <div
                  v-if="notificationStore.items.length < notificationStore.max"
                  class="text-grey text-center mt-3 mx-2 mb-3"
                >
                  {{
                    t(
                      "do-api-server-khong-day-du-ban-phai-xoa-nhung-thong-bao-moi-de-xem-nhung-thong-bao-cu"
                    )
                  }}
                </div>
              </q-card-section>
            </q-card>
          </q-menu>
        </q-btn>

        <q-btn flat round unelevated>
          <q-avatar v-if="authStore.isLogged" size="35px">
            <q-img-custom
              v-if="authStore.user_data?.avatar"
              :src="forceHttp2(authStore.user_data.avatar)"
              no-spinner
              referrerpolicy="no-referrer"
            />
            <Icon
              v-else
              icon="fluent:person-circle-24-filled"
              width="30"
              height="30"
            />
          </q-avatar>
          <Icon
            v-else
            icon="fluent:settings-24-regular"
            width="30"
            height="30"
          />

          <q-menu
            v-model="showMenuAccount"
            class="rounded-xl bg-dark-page shadow-xl"
          >
            <q-card class="transparent w-[280px] px-2 pb-3">
              <q-list v-if="tabMenuAccountActive === 'normal'">
                <template v-if="authStore.isLogged">
                  <q-item class="rounded-xl">
                    <q-item-section avatar>
                      <q-avatar size="45px">
                        <img
                          v-if="authStore.user_data?.avatar"
                          :src="forceHttp2(authStore.user_data.avatar)"
                          referrerpolicy="no-referrer"
                        />
                        <Icon
                          v-else
                          icon="fluent:person-circle-24-filled"
                          width="45"
                          height="45"
                        />
                      </q-avatar>
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="font-weight-medium text-subtitle1">{{
                        authStore.user_data!.name
                      }}</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-separator class="bg-[rgba(255,255,255,0.1)]" />

                  <q-item
                    clickable
                    v-ripple
                    to="/tai-khoan/edit-profile"
                    active-class=""
                    class="rounded-xl"
                  >
                    <q-item-section avatar class="min-w-0">
                      <Icon
                        icon="fluent:info-24-regular"
                        width="20"
                        height="20"
                      />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label>{{ t("trung-tam-ca-nhan") }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </template>
                <template v-else>
                  <q-item class="rounded-xl">
                    <q-item-section>
                      {{ t("cai-dat") }}
                    </q-item-section>
                  </q-item>

                  <q-separator class="bg-[rgba(255,255,255,0.1)]" />
                </template>

                <q-item
                  clickable
                  v-ripple
                  class="rounded-xl"
                  @click="tabMenuAccountActive = 'locale'"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon icon="carbon:translate" width="20" height="20" />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ t("ngon-ngu") }}</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <Icon icon="fluent:chevron-right-24-regular" />
                  </q-item-section>
                </q-item>

                <q-item
                  clickable
                  v-ripple
                  class="rounded-xl"
                  @click="tabMenuAccountActive = 'setting'"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      icon="fluent:settings-24-regular"
                      width="20"
                      height="20"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ t("cai-dat-chung") }}</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <Icon icon="fluent:chevron-right-24-regular" />
                  </q-item-section>
                </q-item>

                <q-item clickable v-ripple class="rounded-xl">
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      icon="fluent:phone-vertical-scroll-24-regular"
                      width="20"
                      height="20"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ t("cuon-vo-han") }}</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-toggle
                      v-model="settingsStore.infinityScroll"
                      dense
                      color="main"
                    />
                  </q-item-section>
                </q-item>

                <q-item
                  v-if="authStore.isLogged"
                  clickable
                  v-ripple
                  class="rounded-xl"
                  @click="logout"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon icon="fa:sign-out" width="20" height="20" />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ t("thoat") }}</q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>

              <q-list v-if="tabMenuAccountActive === 'locale'">
                <q-item class="rounded-xl">
                  <q-item-section avatar class="min-w-0">
                    <q-btn
                      round
                      dense
                      unelevated
                      @click="tabMenuAccountActive = 'normal'"
                    >
                      <Icon
                        icon="fluent:ios-arrow-ltr-24-regular"
                        width="20"
                        height="20"
                      />
                    </q-btn>
                  </q-item-section>
                  <q-item-section>
                    {{ t("chon-ngon-ngu-cua-ban") }}
                  </q-item-section>
                </q-item>

                <!-- <q-separator class="bg-[rgba(255,255,255,0.1)]" /> -->

                <q-item
                  v-for="{ name, code } in languages"
                  :key="code"
                  clickable
                  v-ripple
                  class="rounded-xl"
                  @click="settingsStore.locale = code"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      v-if="settingsStore.locale === code"
                      icon="fluent:checkmark-24-regular"
                      width="20"
                      height="20"
                    />
                    <span v-else class="block w-[20px]" />
                  </q-item-section>
                  <q-item-section>{{ name }}</q-item-section>
                </q-item>
              </q-list>

              <q-list v-if="tabMenuAccountActive === 'setting'">
                <q-item class="rounded-xl">
                  <q-item-section avatar class="min-w-0">
                    <q-btn
                      round
                      dense
                      unelevated
                      @click="tabMenuAccountActive = 'normal'"
                    >
                      <Icon
                        icon="fluent:ios-arrow-ltr-24-regular"
                        width="20"
                        height="20"
                      />
                    </q-btn>
                  </q-item-section>
                  <q-item-section> {{ t("cai-dat-chung") }} </q-item-section>
                </q-item>

                <!-- <q-separator class="bg-[rgba(255,255,255,0.1)]" /> -->

                <q-item clickable v-ripple class="rounded-xl">
                  <q-item-section>
                    <q-item-label>{{ t("tu-dong-phat") }}</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-toggle
                      v-model="settingsStore.player.autoNext"
                      size="sm"
                      color="green"
                    />
                  </q-item-section>
                </q-item>
                <q-item clickable v-ripple class="rounded-xl">
                  <q-item-section>
                    <q-item-label>{{
                      t("nhac-toi-tam-dung-xem")
                    }}</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-toggle
                      v-model="settingsStore.player.enableRemindStop"
                      size="sm"
                      color="green"
                    />
                  </q-item-section>
                </q-item>
              </q-list>
            </q-card>
          </q-menu>
        </q-btn>

        <q-btn
          v-if="!authStore.isLogged"
          flat
          stack
          no-caps
          rounded
          unelevated
          class="font-weight-normal"
          @click="showDialogLogin = true"
        >
          <Icon icon="fluent:person-24-regular" width="20" height="20" />
          {{ t("dang-nhap") }}
        </q-btn>

        <q-btn
          flat
          no-caps
          rounded
          unelevated
          class="font-weight-normal"
          href="https://anime-vsub.github.io"
          target="_blank"
        >
          <Icon icon="fluent:phone-24-regular" width="20" height="20" />
          {{ t("app") }}
        </q-btn>
      </q-toolbar>
    </q-header>

    <q-drawer
      :model-value="hideDrawer ? showDrawer : true"
      @update:model-value="hideDrawer ? (showDrawer = $event) : undefined"
      :mini="hideDrawer ? false : !showDrawer"
      :width="250"
      :breakpoint="500"
      :overlay="hideDrawer"
      :behavior="hideDrawer ? 'mobile' : undefined"
      class="bg-dark-page overflow-visible column flex-nowrap"
    >
      <q-toolbar v-if="hideDrawer">
        <q-btn
          dense
          flat
          round
          icon="menu"
          class="mr-5"
          @click="showDrawer = !showDrawer"
        />

        <router-link to="/" class="flex items-end">
          <img src="~assets/app_icon.svg" width="35" height="35" />
          <span style="font-family: Caveat" class="text-[25px]">nimeVsub</span>
        </router-link>
      </q-toolbar>

      <div class="h-full overflow-y-auto scrollbar-custom">
        <q-list class="mx-2">
          <template
            v-for="{ icon, active, name, path, divider } in drawers"
            :key="name"
          >
            <q-separator
              v-if="divider"
              class="bg-[rgba(255,255,255,0.1)] my-6 mr-2"
            />
            <q-item
              v-else
              clickable
              v-ripple
              class="min-h-0 my-2 rounded-xl"
              :to="path"
              active-class=""
              exact-active-class="bg-[rgba(255,255,255,0.1)] text-main"
            >
              <q-item-section avatar class="pr-0 min-w-0">
                <Icon
                  v-if="router.resolve(path!).fullPath !== route.fullPath"
                  :icon="icon!"
                  width="23"
                  height="23"
                />
                <Icon v-else :icon="active!" width="23" height="23" />
              </q-item-section>
              <q-item-section class="ml-5">
                <q-item-label class="text-[16px]">{{ name }}</q-item-label>
              </q-item-section>
            </q-item>
          </template>

          <!-- playlist -->

          <q-separator
            v-if="playlistStore.playlists && playlistStore.playlists.length > 0"
            class="bg-[rgba(255,255,255,0.1)] my-6 mr-2"
          />

          <q-item
            v-for="item in playlistStore.playlists"
            :key="item.id"
            :to="`/playlist/${item.id}`"
            clickable
            v-ripple
            class="min-h-0 my-2 rounded-xl"
            active-class=""
            exact-active-class="bg-[rgba(255,255,255,0.1)] text-main"
          >
            <q-item-section avatar class="pr-0 min-w-0">
              <Icon
                icon="fluent:navigation-play-20-regular"
                width="23"
                height="23"
              />
            </q-item-section>
            <q-item-section class="ml-5">
              <q-item-label class="text-[16px]">{{ item.name }}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>

        <div v-if="hideDrawer ? true : showDrawer" class="text-gray-500">
          <a
            v-for="item in drawersBottom"
            :key="item.name"
            class="py-2 px-4 block"
            :href="item.href"
            target="_blank"
            >{{ item.name }}</a
          >
        </div>
      </div>
    </q-drawer>

    <q-page-container>
      <q-page :style-fn="route.meta?.styleFn">
        <router-view
          v-if="Http.version"
          v-slot="{ Component }"
        >
          <component :is="Component" />
        </router-view>
        <NotExistsExtension v-else />
      </q-page>
    </q-page-container>
  </q-layout>

  <q-dialog v-model="showDialogLogin">
    <q-card class="h-[60vh] bg-dark-500 min-w-[300px] rounded-xl">
      <q-card-section>
        <div class="flex justify-between">
          <q-btn dense round flat unelevated />

          <div class="flex-1 text-center text-subtitle1">
            {{ t("dang-nhap-de-dong-bo-du-lieu") }}
          </div>

          <q-btn dense round unelevated icon="close" v-close-popup />
        </div>
      </q-card-section>

      <q-card-section>
        <form @submit.prevent="login">
          <div>
            <q-input
              v-model="email"
              outlined
              required
              type="email"
              name="email"
              class="login-input w-full"
              placeholder="E-mail"
              @keydown.stop
            />
          </div>
          <div class="mt-4 relative flex items-center flex-nowrap input-wrap">
            <q-input
              v-model="password"
              outlined
              required
              :type="showPassword ? 'text' : 'password'"
              name="password"
              class="login-input w-full"
              :placeholder="t('mat-khau')"
              @keydown.stop
            >
              <template v-slot:append>
                <q-btn
                  round
                  unelevated
                  class="mr-1"
                  @click="showPassword = !showPassword"
                >
                  <Icon
                    v-if="showPassword"
                    icon="fluent:eye-24-regular"
                    width="22"
                    height="22"
                  />
                  <Icon v-else icon="fluent:eye-off-24-regular" />
                </q-btn>
              </template>
            </q-input>
          </div>

          <div class="text-center text-gray-300 my-3">
            {{ t("dang-nhap-bang-tai-khoan-cua-ban-tren") }}
            <a href="https://animevietsub.cc" target="_blank">AnimeVietsub</a>.
            {{ t("du-lieu-cua-ban-se-duoc-dong-bo-ca-o-do-va-day") }}
          </div>

          <div class="text-grey text-center mt-5 mb-4">
            {{ t("tim-lai-mat-khau") }}
          </div>

          <q-btn
            type="submit"
            no-caps
            rounded
            unelevated
            class="bg-main w-full"
            :disable="!email || !password"
            >{{ t("dang-nhap") }}</q-btn
          >
        </form>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script lang="ts" setup>
import { getAnalytics, logEvent } from "@firebase/analytics"
// eslint-disable-next-line import/order
import { Icon } from "@iconify/vue"

import "@fontsource/caveat"

// =========== suth

import { useEventListener } from "@vueuse/core"
import { Http } from 'client-ext-animevsub-helper'
import BottomBlur from "components/BottomBlur.vue"
import CardVertical from "components/CardVertical.vue"
import QImgCustom from "components/QImgCustom"
import SkeletonCardVertical from "components/SkeletonCardVertical.vue"
import { debounce, QInput, useQuasar } from "quasar"
import semverGt from "semver/functions/gt"
import { version } from "src/../package.json"
import { PreSearch } from "src/apis/runs/pre-search"
import { TuPhim } from "src/apis/runs/tu-phim"
import { checkContentEditable } from "src/helpers/checkContentEditable"
import { languages } from "src/i18n"
import dayjs from "src/logic/dayjs"
import { forceHttp2 } from "src/logic/forceHttp2"
import { parseChapName } from "src/logic/parseChapName"
import { parseMdBasic } from "src/logic/parseMdBasic"
import { parseTime } from "src/logic/parseTime"
import { installedSW, updatingCache } from "src/logic/state-sw"
import { useAuthStore } from "stores/auth"
import { useHistoryStore } from "stores/history"
import { useNotificationStore } from "stores/notification"
import { usePlaylistStore } from "stores/playlist"
import { useSettingsStore } from "stores/settings"
import { computed, ref, watch } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"

import NotExistsExtension from "./NotExistsExtension.vue"

// key bind

const { t } = useI18n()
const drawers = computed(() => [
  {
    icon: "fluent:home-24-regular",
    active: "fluent:home-24-filled",
    name: t("trang-chu"),
    path: "/",
  },
  {
    icon: "ant-design:fire-outlined",
    active: "ant-design:fire-filled",
    name: t("thinh-hanh"),
    path: "/bang-xep-hang",
  },
  {
    icon: "ic:outline-filter-alt",
    active: "ic:round-filter-alt",
    name: t("muc-luc"),
    path: "/danh-sach/all",
  },
  {
    icon: "fluent:calendar-clock-24-regular",
    active: "fluent:calendar-clock-24-filled",
    name: t("lich-chieu"),
    path: "/lich-chieu-phim",
  },

  { divider: true },

  {
    icon: "material-symbols:favorite-outline-rounded",
    active: "material-symbols:favorite-rounded",
    name: t("theo-doi"),
    path: "/tai-khoan/follow",
  },
  {
    icon: "fluent:history-24-regular",
    active: "fluent:history-24-filled",
    name: t("lich-su"),
    path: "/tai-khoan/history",
  },
])
const drawersBottom = computed(() => [
  {
    name: t("ve-chung-toi"),
    href: "https://anime-vsub.github.io/about",
  },
  {
    name: t("lien-he-chung-toi"),
    href: "mailto:ogmo2r3q@duck.com?subject=Phản hồi ứng dụng web AnimeVsub",
  },
  {
    name: t("tai-ung-dung"),
    href: "https://anime-vsub.github.io",
  },
  {
    name: t("dieu-khoan-su-dung"),
    href: "https://anime-vsub.github.io/about/tems-of-use",
  },
  {
    name: t("chinh-sach-rieng-tu"),
    href: "https://anime-vsub.github.io/about/privacy-police",
  },
  {
    name: t("khieu-nai-vi-pham"),
    href: "https://anime-vsub.github.io/about/disclaimer",
  },
])

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const settingsStore = useSettingsStore()
const playlistStore = usePlaylistStore()
const historyStore = useHistoryStore()
const analytics = getAnalytics()

const query = ref("")
const {
  data: searchResult,
  loading: searchLoading,
  run,
} = useRequest(() => PreSearch(query.value), {
  manual: true,
})
watch(query, debounce(run, 300))
function saveAnalytics() {
  logEvent(analytics, "search", {
    search_terms: query.value,
  })
}

const focusing = ref(false)

const showDrawer = ref(false)

const hideDrawer = computed(() => route.meta?.hideDrawer === true)
watch(
  hideDrawer,
  (hideDrawer) => {
    if (hideDrawer) showDrawer.value = false
    else showDrawer.value = true
  },
  { immediate: true }
)
// import QrScanner from "qr-scanner"

const showDialogLogin = ref(false)

const showPassword = ref(false)

const email = ref("")
const password = ref("")

const $q = useQuasar()

async function login() {
  const loader = $q.loading.show({
    message: t("dang-xac-thuc-vui-long-doi"),
    boxClass: "bg-dark text-light-9",
    spinnerColor: "main",
    delay: Infinity,
  })

  try {
    const data = await authStore.login(email.value, password.value)

    showDialogLogin.value = false
    email.value = ""
    password.value = ""
    $q.notify({
      position: "bottom-right",
      message: t("da-dang-nhap-voi-tu-cach-_user", [data.name]),
    })
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (err: any) {
    console.error(err)
    $q.notify({
      position: "bottom-right",
      message: t("dang-nhap-that-bai"),
      caption: err.message,
    })
  } finally {
    loader()
  }
}
async function logout() {
  authStore.logout()
  $q.notify({
    position: "bottom-right",
    message: t("da-dang-xuat"),
  })
}

// ============= states ===============
const showMenuHistory = ref(false)
const showMenuFollow = ref(false)
const showMenuNotify = ref(false)
const showMenuAccount = ref(false)

// history
const {
  data: histories,
  loading: loadingHistories,
  refreshAsync: refreshHistories,
} = useRequest(() => historyStore.loadMoreAfter(), {
  manual: true,
  cacheKey: "history",
  cacheTime: 5 * 60 * 1000, //
})
watch(
  showMenuHistory,
  (state) => {
    if (state) refreshHistories()
  },
  { immediate: true }
)

// follow
const {
  data: favorites,
  loading: loadingFavorites,
  refreshAsync: refreshFavorites,
} = useRequest(() => TuPhim(1), {
  manual: true,
  cacheKey: "favorites",
  cacheTime: 5 * 60 * 1000,
})
watch(
  showMenuFollow,
  (state) => {
    if (state) refreshFavorites()
  },
  { immediate: true }
)

// account
// showMenuAccount
const tabMenuAccountActive = ref<"normal" | "locale" | "setting">("normal")
watch(showMenuAccount, (val) => {
  if (val) tabMenuAccountActive.value = "normal"
})

// key bind /
const inputSearchRef = ref<QInput>()
useEventListener(window, "keypress", (event) => {
  if (checkContentEditable(document.activeElement)) return

  if (event.code === "Slash") {
    event.preventDefault()
    inputSearchRef.value?.focus()
  }
})

// check for update
const newVersionAble = ref<string | null>(null)
const checkingForUpdate = ref(false)
function updateApp() {
  location.reload()
}
async function checkForUpdate() {
  checkingForUpdate.value = true

  const { tag_name: tagName, body }: { tag_name: string; body: string } =
    await fetch(
      "https://api.github.com/repos/anime-vsub/desktop-web/releases/latest"
    ).then((res) => res.json())

  checkingForUpdate.value = false
  if (semverGt(tagName.slice(1), version)) {
    // new version available
    newVersionAble.value = tagName.slice(1)
    $q.dialog({
      title: t("da-co-ban-cap-nhat-moi"),
      message:
        t(
          "phien-ban-animevsub-da-co-ban-cap-nhat-moi-tai-lai-trang-de-cap-nhat"
        ) + `<div style='margin-top: 10px'>${parseMdBasic(body)}</div>`,
      html: true,
      ok: { flat: true, rounded: true },
      cancel: { flat: true, rounded: true },
      focus: "cancel",
      class: "card-changelog",
    }).onOk(updateApp)
  }
}
</script>

<style lang="scss">
.input-search {
  .q-field__control {
    height: 40px !important;
    input,
    input:focus {
      border: none;
      outline: none;
      box-shadow: none;
    }
  }

  .q-field__control:before {
    border-color: rgba(153, 153, 153, 0.3) !important;
  }
  .q-field__control:after {
    border-width: 1px !important;
  }
}

.filled {
  display: none;
}

.tab-active {
  color: #fff;

  .regular {
    display: none;
  }

  .filled {
    display: inline-block;
  }
}

.tabs-main .q-tab__content {
  min-width: 0 !important;
}
.tabs-main .q-tabs__content {
  width: 100% !important;
  > .q-tab {
    width: (100% / 5);
  }
}

.only-router-active {
  display: none;
}
</style>

<style lang="scss" scoped>
.notify {
  &-move,
  &-enter-active,
  &-leave-active {
    transition: all 0.22s ease;
  }

  &-enter-from,
  &-leave-to {
    opacity: 0;
    transform: translateX(30px);
  }

  &-leave-active {
    position: absolute;
  }
}
</style>

<style lang="scss" scoped>
.key-enter {
  color: #f6f6f7;
  forced-color-adjust: none;
  height: 23px;
  width: auto;
  overflow: hidden;
  font-size: 12px;
  line-height: 1;
  text-transform: uppercase;

  &:hover {
    @apply pt-1;
  }

  span {
    background-color: #727d74;
    box-shadow: inset 0 -4px #202225;
    border: 1px solid hsl(220deg, 7.7%, 22.9%);
    padding: 3px 6px 4px;
    border-radius: 4px;
    min-width: 14px;
    min-height: 14px;
    height: 23px;
    color: #b9bbbe;
  }
}
</style>

<style lang="scss" scoped>
.login-input :deep(.q-field__native) {
  background-color: transparent !important;

  &,
  &:focus,
  &:focus-visible {
    outline: none !important;
    border: none !important;
    box-shadow: none !important;
  }
}
.login-input :deep(.q-field__control),
.login-input :deep(.q-field__append) {
  height: 45px !important;
}
</style>

<style lang="scss" scoped>
.hidden-focus-helper :deep(.q-focus-helper) {
  display: none !important;
}
</style>

<style lang="scss">
.card-changelog {
  a {
    color: #58a6ff;
  }
}
</style>
