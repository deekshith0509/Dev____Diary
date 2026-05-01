# 💖 Devadasu Diary

> *"Some apps are built for productivity. This one is built for emotion."*

A cinematic romantic poetry reader for Android — built with Kotlin and Jetpack Compose. Poems live on GitHub Gist. The UI lives in a midnight diary.

---

## 📦 Download — v2.0

The signed release APK is published with each tag on the [**Releases page**](https://github.com/deekshith0509/Dev____Diary/releases).

```
DevadasuDiary-v2.0.apk        ~13 MB    minSdk 24 (Android 7.0+)
```

Install directly on the phone (enable *Install from unknown sources* once), or via ADB:

```bash
adb install -r DevadasuDiary-v2.0.apk
```

---

## What makes this different

Most poetry apps are just text on a white screen.

Devadasu Diary treats every poem like a love letter. The background breathes — stars twinkle, hearts float upward, meteors cut across the sky. The card glows. The text is set in serif, the way handwritten notes deserve to be read. Settings like font size and line spacing exist because how you *read* a poem matters as much as the poem itself.

It is built for:
- poems written in silence
- memories that cannot be forgotten
- confessions that were never sent

---

## What's new in v2.0

This release is a **full rewrite** under the hood — same look and feel, drastically better internals.

**Architecture**
- New layered package structure: `core` → `data` → `ui`
- `PreferencesRepository` collapses 7+ raw DataStore touches into one typed `UserPreferences` flow
- `PoemCatalog` hoisted out of the ViewModel
- `PoetryRepository` uses a singleton `OkHttpClient` (connection pooling preserved across config changes), `.use { }` on every response (no more leaked connections), and a single `cleanContent` step so the BOM stripping isn't bypassed on the cache path
- `PoetryViewModel` exposes one `uiState`, one `readingSettings`, one `appState` — all `@Immutable`, all `stateIn(WhileSubscribed(5_000))`
- Single tracked `loadJob` cancels the previous load on a new tap — no more racing state writes
- `CancellationException` is no longer swallowed by the catch-all
- ViewModel created via `viewModelFactory { initializer { … } }` (idiomatic, no more `AndroidViewModel`-only path)

**UI**
- Edge-to-edge enabled; status & nav bar icons flip contrast per theme
- The "twinkling" background actually animates now — driven by `withFrameMillis` instead of one-shot `Random` (the old field was static)
- Markwon only re-parses when the poem **changes**, not on every slider tick — smooth dragging
- Bottom-nav long-press toggles favorite (was a stubbed callback)
- Icon mapping is data-driven, not hard-coded by `poem.id`
- Header marquee width measurement fixed (Spacer used to swallow the size signal)
- Error card re-shakes on each new error
- `AnimatedContent` keyed on the poem id so loading→success on the same poem doesn't redundantly re-animate

**Settings**
- Slider ranges sourced from `DiaryRanges` — UI and VM cannot drift apart any more
- New persisted toggles: **Haptic Feedback** + **Dynamic Theme Colors** (the old code forced dynamic color on, killing the romantic palette on Android 12+)

**Build & packaging**
- Lifecycle deps aligned to 2.8.4 (was 2.8.4 + 2.7.0 mix)
- Release APK shrunk from 19 MB → **13 MB** via packaging excludes
- `android:configChanges` covers rotation/uiMode/fontScale → state survives
- Explicit `backup_rules.xml` + `data_extraction_rules.xml`
- ProGuard rules for Markwon, commonmark, OkHttp, Compose

A line-by-line bug+enhancement audit (32 bugs / 44 enhancements) is in the v2.0 commit message.

---

## Features

**Content**
- Fetches poems from GitHub Gist raw URLs — no backend needed
- Offline cache — works without internet after first load
- Multiple poems with bottom navigation
- Full Markdown rendering via Markwon (bold, italic, tables, code, task lists, HTML)
- Supports Telugu and other Unicode scripts natively

**Reading experience**
- Compose-native smooth scroll with full fling physics
- Font size, line spacing, padding, alignment — adjustable, persisted, with a live preview
- Text selection — long-press inside the poem and a custom **Share** action mode item appears
- Favorites persisted across sessions; long-press a bottom-nav item to toggle

**UI & atmosphere**
- Animated background — twinkling stars, shooting meteors, drifting hearts
- Glassmorphism card with gradient border
- Romantic Dark theme (midnight purple) and Soft Light theme (pastel pink)
- Optional Material You dynamic colors (Android 12+) behind a settings toggle
- Shimmer loading card, romantic error screen with retry & shake

**Utilities**
- Share selected lines via any app
- Force refresh from network with stale-while-revalidate fallback
- Haptic feedback (toggleable)

---

## Architecture

| Layer | Technology |
|---|---|
| UI | Jetpack Compose, Material 3 |
| State | `ViewModel` + `StateFlow` (one immutable record per stream) |
| Persistence | DataStore Preferences |
| Networking | OkHttp 4 (singleton client, connection pooling) |
| Markdown | Markwon (core + strikethrough + tables + tasklist + html) |
| Pattern | MVVM with a thin Repository layer |

The scroll system deserves a note: Markwon's `TextView` is rendered at full `wrapContentHeight` inside a Compose `verticalScroll` column — this gives native fling momentum and lets complex scripts (Telugu, Devanagari) shape correctly, which the Compose text engine doesn't yet handle as cleanly.

---

## Project structure

```
DevadasuDiary/
└── app/src/main/java/com/love/devadasudiary/
    ├── DiaryApplication.kt
    ├── MainActivity.kt
    ├── core/
    │   ├── Constants.kt          # DiaryDefaults / DiaryRanges / DiaryDimens / DiaryTimings
    │   ├── DataStoreExt.kt       # single dataStore delegate
    │   └── Haptics.kt            # lazy vibrator + permission-safe tick()
    ├── data/
    │   ├── PoemCatalog.kt        # static poem list
    │   ├── PoetryRepository.kt   # network + cache (singleton OkHttp)
    │   ├── model/Poem.kt         # @Immutable
    │   └── prefs/
    │       ├── PreferencesKeys.kt
    │       └── PreferencesRepository.kt   # typed UserPreferences flow
    └── ui/
        ├── PoetryViewModel.kt    # one uiState / readingSettings / appState
        ├── screens/LoveDiaryScreen.kt
        ├── components/
        │   ├── RomanticPoemCard.kt        # Markwon, no re-parse on slider drag
        │   ├── RomanticBackground.kt      # frame-clock driven particles
        │   ├── RomanticTopBar.kt
        │   ├── RomanticBottomNavigation.kt
        │   ├── RomanticHeader.kt
        │   ├── RomanticLoadingCard.kt
        │   ├── RomanticErrorCard.kt
        │   └── PremiumEffects.kt
        ├── dialogs/RomanticSettingsSheet.kt
        ├── state/PoetryUiState.kt         # sealed interface + ReadingSettings
        └── theme/Theme.kt                 # dynamic colors opt-in (was forced)
```

---

## Adding poems

Open [PoemCatalog.kt](app/src/main/java/com/love/devadasudiary/data/PoemCatalog.kt) and append:

```kotlin
Poem(
    id = "4",
    title = "Midnight Letter",
    subtitle = "Written at 2AM",
    gistUrl = "https://gist.githubusercontent.com/<user>/<id>/raw/<file>.txt"
)
```

The URL **must** be the *raw* Gist URL, not the Gist page URL. Poems can be written in any language — Telugu, Hindi, English, or mixed.

The bottom-nav icon for an entry is picked by index from `PoemIcons` in [RomanticBottomNavigation.kt](app/src/main/java/com/love/devadasudiary/ui/components/RomanticBottomNavigation.kt) — extra entries fall back to a heart automatically.

---

## Default reading settings

| Setting | Default | Range |
|---|---|---|
| Font size | 14 sp | 10..34 |
| Line spacing | 14 | 8..30 |
| Padding | 21 dp | 0..48 |
| Center align | off | toggle |
| Haptics | on | toggle |
| Dynamic colors | off | toggle |
| Theme | Dark | toggle |

All ranges are defined once in [`core.Constants.DiaryRanges`](app/src/main/java/com/love/devadasudiary/core/Constants.kt) so the UI sliders and ViewModel coercion can never disagree.

---

## Build

**Requirements:** JDK 17, Android SDK API 34+

```bash
# Debug APK
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.love.devadasudiary.debug/com.love.devadasudiary.MainActivity

# Release APK (signed with the debug keystore for GitHub distribution)
./gradlew :app:assembleRelease
adb install -r app/build/outputs/apk/release/app-release.apk
```

The release build is intentionally signed with the debug keystore so the APK published on GitHub Releases is installable. Replace `signingConfigs.getByName("debug")` in [`app/build.gradle.kts`](app/build.gradle.kts) with a real release key before any Play Store upload.

---

## Minimum SDK

API 24 (Android 7.0 Nougat).

---

## License

Personal and educational use. Fork freely, modify gently.

---

*Made with love by Devadasu 👀✨ — "written by my soul"*
