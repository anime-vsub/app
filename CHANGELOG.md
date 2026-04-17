## [2.1.1](https://github.com/anime-vsub/app/compare/v2.1.0...v2.1.1) (2026-04-17)


### Bug Fixes

* Update long press gesture detection logic in `VideoPlayer.kt` to check for any active touch points using `event.changes.any { it.pressed }` instead of `change.changedToUp()`. ([55aada1](https://github.com/anime-vsub/app/commit/55aada1350477116e72ef86ea769af6690c8fece))

# [2.1.0](https://github.com/anime-vsub/app/compare/v2.0.5...v2.1.0) (2026-04-17)

### Features

* **player:** add customizable double-tap skip and long-press speed
  control ([441c8e4](https://github.com/anime-vsub/app/commit/441c8e4d71efc5fad4e2126446dab0463a401cfa))

## [2.0.5](https://github.com/anime-vsub/app/compare/v2.0.4...v2.0.5) (2026-04-14)


### Bug Fixes

* sync not need persistent ([fc72656](https://github.com/anime-vsub/app/commit/fc72656fa58419a7b1ed84bced470af56a4d75b6))

## [2.0.4](https://github.com/anime-vsub/app/compare/v2.0.3...v2.0.4) (2026-04-13)


### Bug Fixes

* Based on the diffs, here is a summary of the changes: ([252bb29](https://github.com/anime-vsub/app/commit/252bb298e280047efbbe3b2159d7028a69af1960))

## [2.0.3](https://github.com/anime-vsub/app/compare/v2.0.2...v2.0.3) (2026-04-13)


### Bug Fixes

* Based on the diffs, here is a summary of the changes: ([3d5037e](https://github.com/anime-vsub/app/commit/3d5037ee9aaa4aa2a7a4e22c51f89214a83d8fc2))

## [2.0.2](https://github.com/anime-vsub/app/compare/v2.0.1...v2.0.2) (2026-04-13)


### Bug Fixes

* Update `.releaserc.json` to remove the `Android Aligned APK` from the release assets configuration. ([e73b41b](https://github.com/anime-vsub/app/commit/e73b41b3feebcf185db92c9871d97ec045813f97))

## [2.0.1](https://github.com/anime-vsub/app/compare/v2.0.0...v2.0.1) (2026-04-13)


### Bug Fixes

* trigger release ([259c3a2](https://github.com/anime-vsub/app/commit/259c3a2a4fd0d3fc4911f2ddf365ed0a338da691))

# 1.0.0 (2026-04-13)


### Bug Fixes

* accessing a new episode for the first time will say not found because the cache already exists ([8d4a63a](https://github.com/anime-vsub/app/commit/8d4a63a8d987b67624eff9b709d2e8f24e1286a6))
* action `build-apk` failed ([5feb621](https://github.com/anime-vsub/app/commit/5feb621f0fdff9090047d73fdd57304be6aa333a))
* action `build-apk` failure ([df0106b](https://github.com/anime-vsub/app/commit/df0106b0d284b9280696bacd64ad55694c22dcf7))
* btn next wrong error ([e69d5c1](https://github.com/anime-vsub/app/commit/e69d5c1dc496bc7f5350621600451d4b9184da20))
* can't set progress watch if change anime ([bc2f1b1](https://github.com/anime-vsub/app/commit/bc2f1b16d8c585f45884fc9610b0c1155ecfaff6))
* gh action ([3a38363](https://github.com/anime-vsub/app/commit/3a383636d5d165f8bd27c844caf1a8b66894af8a))
* lint ([bd0993e](https://github.com/anime-vsub/app/commit/bd0993ee0ee38a288a9dbc7b17ff83c541db982d))
* lint vue ([a11855b](https://github.com/anime-vsub/app/commit/a11855b9db684c4c088ae99476662fc0804e84a6))
* localize lost ([5ce4235](https://github.com/anime-vsub/app/commit/5ce42358bba43f121b163faf96a6f2d9cb9b5029))
* offset point in player, two call fetch opend ([4a0041e](https://github.com/anime-vsub/app/commit/4a0041ef680187a0d8fc85d6995aad3212dfdc5d))
* package.json to reduce vulnerabilities ([#102](https://github.com/anime-vsub/app/issues/102)) ([09f08ee](https://github.com/anime-vsub/app/commit/09f08ee0091a7f82c445f33f933a7fc307d0e2ec))
* package.json to reduce vulnerabilities ([#120](https://github.com/anime-vsub/app/issues/120)) ([7331b57](https://github.com/anime-vsub/app/commit/7331b57ad76b0ef79c301ba8efd5f677219bac45))
* package.json to reduce vulnerabilities ([#130](https://github.com/anime-vsub/app/issues/130)) ([fea659f](https://github.com/anime-vsub/app/commit/fea659fd589338bd5db0ccf9afe93bbb3cdb0329))
* parsing json ([be68d1e](https://github.com/anime-vsub/app/commit/be68d1e8b40ca2e0d71fc61f193c322f2b3d5f37))
* path not exists on artifact ([12e0a0d](https://github.com/anime-vsub/app/commit/12e0a0db4040303b9a48d6e073b05c3ce3ffa1e5))
* progress not show ([3557298](https://github.com/anime-vsub/app/commit/3557298651171c37293b4d94dffa50d6faad0d83))
* release not file output ([8448eef](https://github.com/anime-vsub/app/commit/8448eef72a15d37fece4476a1a8e43a14d038568))
* request realtime data play list not used ([26c8bc8](https://github.com/anime-vsub/app/commit/26c8bc8fe2666f93d4d254c81c79b7d420c4c1db))
* src-capacitor/package.json to reduce vulnerabilities ([#122](https://github.com/anime-vsub/app/issues/122)) ([1261661](https://github.com/anime-vsub/app/commit/12616615b8fdaf39b777d343f9a5082c0f7bd833))
* typing ([7b9e3d5](https://github.com/anime-vsub/app/commit/7b9e3d56b8662ddb59a2f32d48393e0ccc17c16a))
* upgrade @firebase/app from 0.10.18 to 0.11.0 ([#112](https://github.com/anime-vsub/app/issues/112)) ([3ee98bd](https://github.com/anime-vsub/app/commit/3ee98bdf4a539d9b890196a00ade3519b9ea45b5))
* upgrade @firebase/app from 0.11.5 to 0.14.6 ([#118](https://github.com/anime-vsub/app/issues/118)) ([c08a765](https://github.com/anime-vsub/app/commit/c08a765c3cfda55a52a82b9f1e3e825a5dedc3d7))
* upgrade @firebase/app from 0.9.29 to 0.10.6 ([#105](https://github.com/anime-vsub/app/issues/105)) ([39ac971](https://github.com/anime-vsub/app/commit/39ac97110d72025f412755145cdc317c0e2cc2db))
* upgrade cheerio from 1.0.0 to 1.1.2 ([#119](https://github.com/anime-vsub/app/issues/119)) ([447bdc0](https://github.com/anime-vsub/app/commit/447bdc00e652e73d412585b779cf82d34b930394))
* upgrade cheerio from 1.0.0-rc.12 to 1.0.0 ([#110](https://github.com/anime-vsub/app/issues/110)) ([22430f2](https://github.com/anime-vsub/app/commit/22430f26c78f43970a9583dd35f76286407765e1))
* upgrade hls.js from 1.5.9 to 1.5.13 ([#107](https://github.com/anime-vsub/app/issues/107)) ([5223cf3](https://github.com/anime-vsub/app/commit/5223cf3d6052d7b57bc14f080d682a19841a3933))
* upgrade hls.js from 1.5.9 to 1.5.13 ([#108](https://github.com/anime-vsub/app/issues/108)) ([1d99d55](https://github.com/anime-vsub/app/commit/1d99d550bcec5ae95ef285f4ded1b6e99a9ecbfd))
* upgrade vue-request from 2.0.0-rc.4 to 2.0.4 ([#106](https://github.com/anime-vsub/app/issues/106)) ([a3dce63](https://github.com/anime-vsub/app/commit/a3dce63392a29c630e5b2ed583c71b400d7061e1))
* use `pnpm@8` in `ci` ([bc706ea](https://github.com/anime-vsub/app/commit/bc706eaba6a8917d64522a3a59105d15b397c69c))
* wrong port comment native from `desktop-web` ([4dbe3aa](https://github.com/anime-vsub/app/commit/4dbe3aa8d2f4c6921fa62bd422d7893e286740cb))


### Features

* add auto skip `opening` and `ending` ([66520a6](https://github.com/anime-vsub/app/commit/66520a6e5f86b2146b3aa613e09b877d7f14f0ec))
* add episode search and bottom sheet navigation ([40993db](https://github.com/anime-vsub/app/commit/40993db8e2280c44003361b773951d25604d8b05))
* add fullscreen support and orientation handling to VideoPlayer ([be32800](https://github.com/anime-vsub/app/commit/be328009598fb626bbf8ad2aaa497f637a9e5323))
* add gesture controls and auto-skip functionality to VideoPlayer ([fa88ff8](https://github.com/anime-vsub/app/commit/fa88ff8dfed87a1b3d62cbcb6ddc6c07c528f2bb))
* add localization for session expiration and Cloudflare bypass ([0cbd51e](https://github.com/anime-vsub/app/commit/0cbd51efcb76c3ea9ce47d93fa9e2e2cc87150bb))
* enable eruda in dev mode ([9561d5d](https://github.com/anime-vsub/app/commit/9561d5de272ed4889d54c9e3fda271887403d1f6))
* enhance Account screen with watch history, followed anime, and expanded settings ([9890506](https://github.com/anime-vsub/app/commit/9890506f0d1289b0d19d8961e86af26733232550))
* enhance metadata navigation and refine category filtering across screens ([256a492](https://github.com/anime-vsub/app/commit/256a4924f0d8a13fd7207d65e3bf8f3d3408548f))
* enhance Notifications with pull-to-refresh, swipe-to-dismiss, and improved UI ([116cde6](https://github.com/anime-vsub/app/commit/116cde6cdc30e90631e7c6fa67ff099924162c2d))
* enhance playlist management with add/remove toggles and checked state tracking ([0275d07](https://github.com/anime-vsub/app/commit/0275d07d81bb782956fb48e84ccebb9743748cc5))
* enhance schedule screen with improved date parsing and grouping ([5af6b36](https://github.com/anime-vsub/app/commit/5af6b36d017bd8abf51b1ad395676005e0740183))
* enhance studio metadata and navigation in Detail screen ([a3f1735](https://github.com/anime-vsub/app/commit/a3f173515875a462864b251508224efa6520f3dc))
* enhance VideoPlayer with playback speed control and auto-next functionality ([2617283](https://github.com/anime-vsub/app/commit/26172833cae841bd627973d38ee18b0e4932e959))
* **error controller:** :zap: add control fix `click:rery` and search type error ([c8a480f](https://github.com/anime-vsub/app/commit/c8a480ffa9c29172fe959f12130112100f0b898b))
* fix category not found ([1473b9e](https://github.com/anime-vsub/app/commit/1473b9ee88522cae2a5944ccba5b4438af1f6f50))
* fix firestore realtime not reactive ([c1e5a66](https://github.com/anime-vsub/app/commit/c1e5a664918ad9b295180e935181fd91655b3378))
* fix ui `ScreenError` ([26f2e78](https://github.com/anime-vsub/app/commit/26f2e78ccc5d6037c5645868af357e18e1ed9ee8))
* implement advanced filtering and grid view in Category screen ([2e3dd1d](https://github.com/anime-vsub/app/commit/2e3dd1d128b10a94ad5c6a1496d597001aae5267))
* implement automated skip range detection for intro and outro ([af3f77f](https://github.com/anime-vsub/app/commit/af3f77fa2e5ff1db64f112269eb9078fffc05d09))
* implement comment system in Detail screen ([127efbc](https://github.com/anime-vsub/app/commit/127efbcdd69d17f16d4a6bce3ca3c85fc73e6b2e))
* implement comprehensive playlist management and navigation ([4005633](https://github.com/anime-vsub/app/commit/40056330014309dadf3d9a3a284cc28795902278))
* implement follow/unfollow functionality and movie sharing ([2b79d2f](https://github.com/anime-vsub/app/commit/2b79d2f230195f097a771d1ac8241e627f414509))
* implement full search functionality and pagination in Search screen ([9b6091c](https://github.com/anime-vsub/app/commit/9b6091c8d94d531d03fff91a3d9e36b205265b10))
* implement History screen and UI refinements for Account and History components ([8d802e1](https://github.com/anime-vsub/app/commit/8d802e15e00d2f65b843c85f3fc737fbb2b551e4))
* implement in-app update checking and notification system ([45362b4](https://github.com/anime-vsub/app/commit/45362b445ab4e1668a776094c582eebdb288ad5e))
* implement in-app update system via GitHub Releases ([c30d28a](https://github.com/anime-vsub/app/commit/c30d28abb12334ecdf2e6c88165d41e923db29e7))
* implement notification caching, unread badges, and shimmer loading ([a1ef7e1](https://github.com/anime-vsub/app/commit/a1ef7e1eeda630c2087a40f96c455e7f8e26439b))
* implement pager-based rankings with dynamic categories ([a5455be](https://github.com/anime-vsub/app/commit/a5455bef9025010101327f57e9d2d9e5c8790891))
* implement player side menus for episode and server selection ([c065c71](https://github.com/anime-vsub/app/commit/c065c71a32cc632d95a07b52277f0ea48dbea072))
* implement pull-to-refresh across major screens and update Compose BOM ([03116aa](https://github.com/anime-vsub/app/commit/03116aa4b76c5d3050ef0a611240998920d1647a))
* implement Settings screen and refactor Account UI ([48524cc](https://github.com/anime-vsub/app/commit/48524ccd1381e960e82f9e08e7ad55c6a0b316f2))
* implement unified settings menu and support options in VideoPlayer ([f7d9a70](https://github.com/anime-vsub/app/commit/f7d9a7032263877e051920dd90398b1865f37c9c))
* implement video quality selection and refine DetailScreen types ([d25a832](https://github.com/anime-vsub/app/commit/d25a83225339c05ceca0eeef6d8666aa78e30dbb))
* implement watch history and progress syncing via Supabase ([59838d0](https://github.com/anime-vsub/app/commit/59838d0e663c766869fc84a3406f3f4f189a102a))
* improve Playlist Detail UI and episode label formatting ([e8be077](https://github.com/anime-vsub/app/commit/e8be077590243e83a79f6009e6ff576302efb984))
* integrate Firebase Analytics for user activity tracking ([ba046d9](https://github.com/anime-vsub/app/commit/ba046d9fb6179dac045a8cf2faf30da9ec9afb83))
* not save history view if <5s ([d0f56ed](https://github.com/anime-vsub/app/commit/d0f56edf68a0f5ca0460ae12fea6c1a4d12bee69))
* not show content require login in anony `/tai-khoan` ([f5e392d](https://github.com/anime-vsub/app/commit/f5e392d99d9d14216523c091ccb752d58cdf60d0))
* refactor anime models and implement horizontal paging in Schedule screen ([72c1746](https://github.com/anime-vsub/app/commit/72c1746b4660b23b29fc5518a3a2adb23b3969d2))
* refactor category navigation to use a flexible filter-based system ([783a712](https://github.com/anime-vsub/app/commit/783a7126089c238f187394245a34f4d8d55251c4))
* refactor UI components and improve anime data parsing ([6ae48f9](https://github.com/anime-vsub/app/commit/6ae48f9ccb47754a2ca09e190afd5f1bbf509480))
* Rewrite app using Kotlin Jetpack Compose ([f47e075](https://github.com/anime-vsub/app/commit/f47e07553e34fd6023b1aa72d35cc394760e3581))
* secure Supabase configuration using BuildConfig and local.properties ([aea3eec](https://github.com/anime-vsub/app/commit/aea3eecbabc01e7b760168599c01bbe4c83221fe))
* size image poster in menu info `/phim/_season` ([b9081cf](https://github.com/anime-vsub/app/commit/b9081cf5a241175a4b7b79349f95cb095f70d1d5))
* support comment native in native ([a0fcb26](https://github.com/anime-vsub/app/commit/a0fcb26993037a3fcfb9e3808bff72f41aa99753))
* upgrade dependencies and enhance video player with server selection and skip ranges ([09a06d0](https://github.com/anime-vsub/app/commit/09a06d0f5d0e6f69656bab760dbc05c6ad375f58))
* use `unplugin-auto-import` and `unplugin-vue-components` ([8b9d78f](https://github.com/anime-vsub/app/commit/8b9d78f98cd98a3901b1a1d3382a1d305fd44f3e))
* wrong ep restore ([a12da4d](https://github.com/anime-vsub/app/commit/a12da4dbd3b181b542f5a43c5fb9cc93f22b8a0a))
