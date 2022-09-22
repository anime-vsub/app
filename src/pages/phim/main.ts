import Hls from "./hls.js"
import Artplayer from "artplayer"

const art = new Artplayer({
  container: "#player",
    autoplay: true,
    url: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8',
    customType: {
        m3u8: function (video, url) {
            if (Hls.isSupported()) {
                const hls = new Hls({
                  // progressive: true,
                  debug: true
                });
                // customLoader(hls.config)
                hls.loadSource(url);
                hls.attachMedia(video);
            } else {
                const canPlay = video.canPlayType('application/vnd.apple.mpegurl');
                if (canPlay === 'probably' || canPlay === 'maybe') {
                    video.src = url;
                } else {
                    art.notice.show = '不支持播放格式：m3u8';
                }
            }
        },
    },
     volume: 0.5,
    isLive: false,
    muted: false,
    autoplay: false,
    pip: true,
    autoSize: true,
    autoMini: true,
    screenshot: true,
    setting: true,
    loop: true,
    flip: true,
    playbackRate: true,
    aspectRatio: true,
    fullscreen: true,
    fullscreenWeb: true,
    subtitleOffset: true,
    miniProgressBar: true,
    mutex: true,
    backdrop: true,
    playsInline: true,
    autoPlayback: true,
    airplay: true,
    theme: '#23ade5',
    lang: navigator.language.toLowerCase(),
    whitelist: ['*'],
    settings: [
    ],
    contextmenu: [
    ],
    layers: [
    ],
    quality: [
    ],
    icons: {
        loading: '<img src="https://artplayer.org/assets/img/ploading.gif">',
        state: '<img width="150" heigth="150" src="https://artplayer.org/assets/img/state.svg">',
        indicator: '<img width="16" heigth="16" src="https://artplayer.org/assets/img/indicator.svg">',
    },
});