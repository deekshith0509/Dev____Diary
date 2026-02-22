# 💖 Devadasu Diary
> *"Some apps are built for productivity. This one is built for emotion."*

A cinematic romantic poetry reader for Android — built with Kotlin and Jetpack Compose. Poems live on GitHub Gist. The UI lives in a midnight diary.

---

## What makes this different

Most poetry apps are just text on a white screen.

Devadasu Diary treats every poem like a love letter. The background breathes — stars twinkle, hearts float upward, meteors cut across the sky. The card glows. The text is set in serif, the way handwritten notes deserve to be read. Settings like font size and line spacing exist because how you *read* a poem matters as much as the poem itself.

It is built for:
- poems written in silence
- memories that cannot be forgotten  
- confessions that were never sent

---

## Features

**Content**
- Fetches poems from GitHub Gist RAW URLs — no backend needed
- Offline cache — works without internet after first load
- Multiple poems with bottom navigation
- Full Markdown rendering via Markwon (bold, italic, tables, code blocks with rounded corners)
- Supports Telugu and other Unicode scripts natively

**Reading Experience**
- Compose-native smooth scroll with full fling physics
- Font size, line spacing, padding, alignment — all adjustable and persisted
- Text selection enabled — copy individual lines
- Favorites persisted across sessions

**UI & Atmosphere**
- Animated background — twinkling stars, shooting meteors, floating hearts
- Glassmorphism card with gradient border
- Romantic Dark theme (midnight purple) and Soft Light theme (pastel pink)
- Shimmer loading card, romantic error screen with retry
- Live settings preview — see font/spacing changes before closing the sheet

**Utilities**
- Copy poem to clipboard
- Share via any app
- Force refresh from network
- Haptic feedback on every interaction

---

## Architecture

| Layer | Technology |
|---|---|
| UI | Jetpack Compose |
| State | ViewModel + StateFlow |
| Persistence | DataStore Preferences + local file cache |
| Networking | HttpURLConnection |
| Markdown | Markwon with custom `LineBackgroundSpan` |
| Pattern | MVVM |

The scroll system deserves a note: `AndroidView` (Markwon's `TextView`) is rendered at full `wrapContentHeight` inside a Compose `verticalScroll` column — this gives native fling momentum rather than the janky event-bridging that `nestedScrollInterop` alone produces.

The code block span uses `LineBackgroundSpan` with first/last line awareness to draw one unified rounded rectangle across multiple lines — not individual per-line boxes.

---

## Project Structure

```
DevadasuDiary/
└── app/src/main/java/com/love/devadasudiary/
    ├── MainActivity.kt
    ├── PoetryViewModel.kt              # State, settings, poem loading, cache
    ├── PoetryRepository.kt             # Network fetch + local file cache
    └── ui/
        ├── screens/
        │   └── LoveDiaryScreen.kt      # Main screen composition
        ├── components/
        │   ├── RomanticPoemCard.kt     # Markwon renderer + smooth scroll
        │   ├── RomanticBackground.kt   # Animated stars / hearts / meteors
        │   ├── RomanticTopBar.kt
        │   ├── RomanticBottomNavigation.kt
        │   ├── RomanticLoadingCard.kt
        │   └── RomanticErrorCard.kt
        ├── dialogs/
        │   └── RomanticSettingsSheet.kt  # Live preview while adjusting
        └── theme/
            └── Theme.kt
```

---

## Adding Poems

Open `PoetryViewModel.kt` and add to the `poems` list:

```kotlin
Poem(
    id = "4",
    title = "Midnight Letter",
    subtitle = "Written at 2AM",
    gistUrl = "https://gist.githubusercontent.com/<user>/<id>/raw/<file>.txt"
)
```

The URL **must** be the RAW Gist URL, not the Gist page URL. Poems can be written in any language — Telugu, Hindi, English, or mixed.

---

## Default Reading Settings

| Setting | Default |
|---|---|
| Font size | 14 sp |
| Line spacing | 14 |
| Padding | 21 dp |
| Alignment | Left |
| Theme | Dark |

---

## Build

**Requirements:** JDK 17, Android SDK API 34+

```bash
# Build debug APK
./gradlew clean assembleDebug

# Output
app/build/outputs/apk/debug/app-debug.apk

# Install via ADB
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch directly
adb shell monkey -p com.love.devadasudiary -c android.intent.category.LAUNCHER 1
```

---


## Minimum SDK

API 24 (Android 7.0 Nougat)

---

## License

Personal and educational use. Fork freely, modify gently.

---

*Made with love by Devadasu 👀✨ — "written by my soul"*
