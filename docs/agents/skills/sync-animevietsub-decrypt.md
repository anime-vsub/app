# Skill: Sync decrypt logic from `animevietsub-decrypt-new` to Android

## Source

`F:\Users\Admin\animevietsub-decrypt-new\run.ts`

## Target files (Kotlin)

| File                                           | Purpose                                                             |
|------------------------------------------------|---------------------------------------------------------------------|
| `data/remote/api_hidden/SegmentDecryptUtil.kt` | All crypto utilities (f2, f3, f4, f7, f8, f10, f12, decryptSegment) |
| `data/remote/api_hidden/M3u8Result.kt`         | Result data class (content, skGlobalDecoded, avsCryptoHarden)       |
| `data/remote/api_hidden/AnimeApiCore.kt`       | `getM3u8Content()` + `fnCrypto()` + `extractIdAndSid()`             |
| `data/remote/api_hidden/AnimeApiPlayer.kt`     | `lastSkGlobalDecoded`, `lastAvsCryptoHarden` state                  |
| `data/remote/api_hidden/AnimeApi.kt`           | `segmentUrlInterceptor` + `segmentDataInterceptor` wiring           |

## Function mapping

### `api_hidden/SegmentDecryptUtil.kt`

```
run.ts                    → Kotlin
─────────────────────────────────────────────────────
f2(value)                → Fnv1aPrng(value) class + .next()
f3(data, k, salt)        → xorPermute(data, permKey, permSalt)
f4(token)                → parseEnvelope(token): EnvelopeData
f7(n)                    → lcgNext(n) (private inline)
f8(content, etag)        → descrambleContent(content, etag)
f10(token)               → extractJtiOdd(token): String
f12(data)                → trimSegmentHeader(data): ByteArray
decryptSegment(url, sk)  → decryptSegmentUrl(url, skGlobalDecoded): String
transferStag(stag)       → base64Decode(stag) (private inline)
```

### `api_hidden/AnimeApiCore.kt`

**`getM3u8Content(url, referer)` — matches TS `avsDecryptM3u8` exactly:**

1. `extractIdAndSid()`: fetch player page HTML, extract `id`, `_avsSk`, `_avsCryptoHarden`,
   `_avsCryptoHardenShadow`
2. `skGlobalDecoded = f10(avsSk)`, `fc = btoa("cross-origin")`
3. Build playlist URL: `https://<host>/playlist/{id}/playlist.m3u8?token={avsSk}&fc={fc}`
4. Fetch playlist → parse X-Envelope header (JSON with `cn`/`sk`/`ts`/`uid`) or fallback
   `X-edge-Tag`/`X-Cache-Node`/`X-Request-Trace`/`X-Proxy-Digest`
5. **`_c` guard**: check if any non-comment line matches `[?&]_c=[0-9]+`. If none → return raw body.
6. **`stag`/`etag` guard**: if either is empty → return raw body.
7. **Header + content extraction**:
    - Build `header[]` from lines that start with `#` but do NOT start with `#EXTINF:`,
      `#EXT-X-ENDLIST`, or `#EXT-X-KEY`
    - Build `content` by concatenating `_t=` values from URL lines (regex `[?&]_t=([^&\s]+)`)
    - If content empty → return raw body
8. **`f8` descramble**: if `_avsCryptoHarden`, apply `descrambleContent(content, etag)`
9. **`f9` decrypt**:
   `fnCrypto(processedContent, stag, etag, custom, envId, avsCryptoHarden, avsCryptoHardenShadow)`
10. **URL resolution**: for each line in decrypted content, if starts with `/` → prepend origin
11. **Return**: `header.join("\n") + "\n" + newContent`

**`fnCrypto(content, stag, etag, custom, id, avsCryptoHarden, avsCryptoHardenShadow)` — matches
TS `f9`:**

1. `key = HmacSHA256(stag, mergeKey)`, where mergeKey is `custom:id:etag` or `custom:id:etag:0` when
   harden
2. AES-GCM decrypt with key, iv = stag[0..12), 128-bit tag
3. If harden: apply `xorPermute(decrypted, etag, id)` post-decrypt
4. On `AEADBadTagException`: if harden, generate noise via
   `Fnv1aPrng("AES-GCM:$etag:$id:$length:noise")` matching TS fallback bit-exact

### AnimeApiPlayer.kt

```
getPlayerLink():
  iframe path:
    core.getM3u8Content() → M3u8Result(content, skGlobalDecoded, avsCryptoHarden)
    → stores lastSkGlobalDecoded, lastAvsCryptoHarden
  decryptM3u8 path:
    → clears lastSkGlobalDecoded = null, lastAvsCryptoHarden = false
```

### AnimeApi.kt

```
segmentUrlInterceptor: uses decryptSegmentUrl() when _avsCryptoHarden=true
segmentDataInterceptor: always uses trimSegmentHeader() (f12)
```

## Bitwise pitfalls (TS → Kotlin)

These are the most critical correctness issues when porting 32-bit integer operations from JS to
Kotlin:

### 1. `2166136261` exceeds `Int.MAX_VALUE` (2147483647)

Kotlin infers `Long` for `var hash = 2166136261`, breaking FNV-1a 32-bit overflow.

**Fix**: Use `0x811C9DC5.toInt()` (hex literal fits in Int via bit representation).

### 2. `v7 >>> 0 || 1` vs `(hash or 1)`

JS `|| 1` keeps the original value if non-zero. `hash or 1` in Kotlin **modifies** even non-zero
values (e.g., `2 or 1 = 3`).

**Fix**: `state = if (hash == 0) 1 else hash`

### 3. Unsigned modulo with `>>> 0`

JS `>>> 0` gives an unsigned 32-bit value used in `%`. Kotlin Int is signed, so `a and 0x7FFFFFFF`
incorrectly clears the sign bit.

**Fix** (for `Int` values where unsigned interpretation is needed):

```kotlin
(a.toUInt() % b.toUInt()).toInt()
```

Applies to: `xorPermute` shuffle index, `descrambleContent` shuffle index.

### 4. `parseInt(hex, 16)` for unsigned hex strings

Strings like `"FFFFFFFF"` cannot be parsed via `toIntOrNull(16)` (exceeds signed Int range). JS
`parseInt` handles up to 2^53.

**Fix**: `hex.toLongOrNull(16)?.toInt() ?: 0` — parse as Long then truncate to 32 bits.

### 5. JS `Math.imul(a, b)` ≡ Kotlin `a * b` (Int overflow)

Both produce the low 32 bits of the product as a signed 32-bit integer. No special handling needed.

### 6. JS `>>>` ≡ Kotlin `ushr`

Both are zero-fill (unsigned) right shift. Operates on the 32-bit bit pattern regardless of signed
interpretation.

## Update checklist when run.ts changes

1. **`getM3u8Content()`** (`AnimeApiCore.kt`): compare TS `avsDecryptM3u8` line-by-line — check `_c`
   guard, header[] build, content extraction via `_t` regex, URL resolution, return format.
2. **`fnCrypto()`** (`AnimeApiCore.kt`): compare TS `f9` + `cryptoDecrypt` — mergeKey format, GCM
   params, `xorPermute` post-decrypt, noise fallback PRNG key format.
3. **`extractIdAndSid()`** (`AnimeApiCore.kt`): check for new regex patterns in player page HTML.
4. **`SegmentDecryptUtil.kt`**: after any changes, verify bitwise correctness (see pitfalls above).
5. **`AnimeApiPlayer.kt`**: verify state vars are correctly set/cleared.
6. **`AnimeApi.kt`**: verify interceptors still match.
7. Run: `make format`
