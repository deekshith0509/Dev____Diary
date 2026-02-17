<<<<<<< HEAD
# Dev____Diary
Devadasu Diary is a premium romantic poetry and love-diary app built using Kotlin and Jetpack Compose. It fetches poems from GitHub Gist, supports offline reading, theme switching, font resizing, copy/share, and a cinematic UI with animated stars, hearts, and glow effects.
=======
# 💖 Devadasu Diary — Poetry / Love Diary Reader App

**Devadasu Diary** is a premium, cinematic, romantic poetry diary Android application built with **Kotlin + Jetpack Compose**.  
This app is designed like a luxury love diary, where every poem feels like a confession written under moonlight.

It is not just a simple text viewer — it is an **emotion-driven UI experience** built for long poems, dramatic typography, smooth scrolling, offline reading, and beautiful love-themed animations like stars, sparkles, floating hearts, and shooting meteors.

---

## 🌙 App Vision

Devadasu Diary is made for:
- people who write poems in silence
- hearts that want to preserve memories
- love stories that cannot be forgotten
- confessions that remain unread but still exist

It turns raw text into an elegant love letter experience.

---

## ✨ Core Features

### 📌 Dynamic Poem Loading
- Poems are **NOT hardcoded**
- App fetches poem content from **GitHub Gist RAW URL**
- Supports multiple poems (different gist links)

### 📴 Offline Cache Support
- Once loaded, the poem is stored locally
- App works without internet after first successful load

### 🎨 Premium Romantic UI
- Deep purple + midnight gradients
- Soft glow shadows
- Glassmorphism cards
- Elegant typography
- Romantic header + footer styling

### 🌌 Animated Dreamy Background
- Twinkling stars animation (continuous)
- Shooting star meteor animation (falling diagonally)
- Floating hearts rising upward (love symbols)
- Smooth dreamy cinematic background effect

### 📜 Perfect Poem Formatting Preservation
- Preserves:
  - line breaks
  - indentation
  - empty lines
  - stanza spacing
  - punctuation
- Selection enabled (copy individual lines easily)

### 🔤 Reading Enhancements
- Font size slider
- Theme toggle (Romantic Dark / Soft Light)
- Copy poem to clipboard
- Share poem feature
- Refresh poem button (re-fetch gist)

### 🧠 Smart Display Handling
- HTML stripping option (if gist returns unwanted HTML)
- Displays clean poem content for best readability

---

## 🖼️ UI Philosophy

Devadasu Diary follows these principles:

### 💎 Luxury Look
- Smooth gradients
- Soft shadow elevation
- Minimalistic premium spacing
- Rounded corners and glass effects

### 📖 Reading First
- Text is the main hero
- UI never distracts from poem
- Typography is elegant and calm

### 🌙 Night Mood
- The dark theme is designed like a midnight diary
- Light theme is designed like a pastel romantic journal

---

## 🧩 App Architecture

This app is built with modern Android best practices:

### 🏗️ MVVM Pattern
- UI Layer: Jetpack Compose
- State Layer: ViewModel + StateFlow
- Data Layer: Repository

### 🔁 Reactive UI
- UI updates automatically using StateFlow + collectAsState()

### 💾 Storage
- Uses DataStore for preferences (font size, theme, favorites, strip-html)
- Uses local file caching for poems

### 🌐 Networking
- Fetches poem content using OkHttp / HttpURLConnection
- Handles errors gracefully with romantic error UI

---

## 📂 Project Structure

```

DevadasuDiary/
│── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/love/devadasudiary/
│   │   │   ├── MainActivity.kt
│   │   │   ├── PoetryViewModel.kt
│   │   │   ├── PoetryRepository.kt
│   │   │   ├── Theme.kt
│   │   │   ├── UiComponents.kt
│   │   ├── res/
│   │       ├── drawable/
│   │       ├── mipmap-*/
│   │       ├── values/
│── build.gradle.kts
│── settings.gradle.kts
│── gradle.properties
│── README.md

```

---

## 🔥 Screens & UX

### 🌙 Home Screen
- Love-themed top bar with:
  - theme toggle
  - copy button
  - share button
  - settings
- Animated romantic header
- Poem displayed inside premium diary card
- Footer watermark: “written by my soul”
- Bottom navigation for multiple poems

### ⚙️ Settings Sheet
- Font size slider
- HTML stripping toggle

### 💔 Error Screen
If poem fetch fails:
- Shows romantic error message
- Retry button included
- Falls back to cached poem if available

### ⏳ Loading Screen
Premium loading card:
- shimmering border
- heart glow pulse
- romantic loading text

---

## 📡 GitHub Gist Integration

This app fetches poems from GitHub Gist RAW URLs.

Example RAW gist URL format:

```

[https://gist.githubusercontent.com/](https://gist.githubusercontent.com/)<username>/<gist_id>/raw/<filename>.txt

```

### ✅ How to update poem source
Open:

```

PoetryViewModel.kt

````

Update poem list:

```kotlin
private val poemList = listOf(
    Poem("1", "Devadasu Diary", "", "YOUR_GIST_RAW_URL_HERE")
)
````

---

## 💾 Offline Cache Explained

The app stores poem text locally after downloading.

So:

* First time → internet required
* Later → poem loads instantly from cache
* Refresh button forces re-fetch from network

This is useful when:

* traveling
* low network
* heartbreak at 2AM with no signal 😭

---

## 🌈 Theme System

### Romantic Dark Theme

* Midnight purple background
* Pink glow highlights
* Elegant white text

### Soft Light Theme

* Pastel pink surface
* Gentle purple accents
* Dark romantic text

---

## 🔤 Typography & Poem Style

The poem is styled like a love letter:

* serif font family feel
* large line height for readability
* centered alignment
* stanza spacing preserved
* selectable text

---

## ❤️ Icons & Branding

The app uses a custom adaptive icon built using XML vector drawables:

* moon
* heart
* sparkles
* romantic gradient background

No external PNG required.

---

## 🔧 Build Requirements

### Minimum SDK

* **24**

### Target SDK

* Latest stable Android version

### Language

* Kotlin

### UI Framework

* Jetpack Compose

---

## 🛠️ How to Build on Linux

### 1️⃣ Install Java 17

Debian/Ubuntu:

```bash
sudo apt update
sudo apt install openjdk-17-jdk -y
java -version
```

### 2️⃣ Install Android SDK Commandline Tools

Download Android command line tools and extract to:

```
~/Android/Sdk/cmdline-tools/latest/
```

### 3️⃣ Install Required SDK Packages

Run:

```bash
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

### 4️⃣ Build Debug APK

Inside project folder:

```bash
./gradlew clean assembleDebug
```

APK output:

```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔐 Release Build (Signed APK)

### 1️⃣ Generate Keystore

Run:

```bash
keytool -genkeypair -v \
  -keystore devadasu_keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias devadasu
```

### 2️⃣ Build Release APK

```bash
./gradlew assembleRelease
```

APK output:

```
app/build/outputs/apk/release/app-release.apk
```

### 3️⃣ Sign the APK (Manual)

```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore devadasu_keystore.jks \
  app/build/outputs/apk/release/app-release.apk devadasu
```

### 4️⃣ Verify Signature

```bash
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk
```

---

## 📲 Install APK to Phone using ADB

### 1️⃣ Enable Developer Options on Android

* Settings → About Phone → tap Build Number 7 times
* Enable USB Debugging

### 2️⃣ Check Device

```bash
adb devices
```

### 3️⃣ Install Debug APK

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 4️⃣ Launch App

```bash
adb shell monkey -p com.love.devadasudiary -c android.intent.category.LAUNCHER 1
```

---

## 🧪 Debugging Tips

### Check crash logs

```bash
adb logcat | grep devadasudiary
```

### Clear app cache

```bash
adb shell pm clear com.love.devadasudiary
```

### Uninstall app

```bash
adb uninstall com.love.devadasudiary
```

---

## 🧠 Common Issues & Fixes

### ❌ "Manifest parse error"

Fix:

* check duplicate attributes
* ensure manifest has package name

### ❌ "Theme not found"

Fix:

* ensure Material3 dependency exists
* ensure Theme.kt defines Theme.DevadasuDiary

### ❌ Gist returns HTML

Fix:

* enable "strip html" setting in app
* ensure gist raw URL is correct

### ❌ Copy button crash

Fix:

* ensure ClipboardManager is called with valid context
* ensure poem is not empty

---

## 💖 Roadmap (Future Improvements)

This project can be expanded into a full premium diary platform:

### 🌹 Possible Features

* Save personal poems locally
* Add password lock (privacy mode)
* Add favorites sorting
* Add poem categories (Love / Pain / Memories)
* Add daily quote notifications
* Add offline multi-poem library
* Add custom fonts
* Add export poem as PDF
* Add background music toggle (piano / rain / lofi)

### ✨ UI Enhancements

* Page-turn animation
* Animated moonlight shimmer overlay
* Heart beat pulse when poem is opened
* Smooth blur glass backgrounds

---


## 🌙 About Devadasu Diary

This is not just an app.

It is a digital place where:

* pain becomes poetry
* memories become words
* silence becomes confession
* and love becomes a story that never dies

---

## ❤️ Author

**Made with love by Devadasu 👀✨**
“— written by my soul”

---

## ⭐ Support

If you like this project:

* star the repository ⭐
* share it with someone who loves poetry
* or keep it as your private diary forever 💖

---

## 📜 License

This project is personal and poetic.
You may modify it for learning purposes.

---

## 🌹 Final Words

Some apps are built for productivity.

**Devadasu Diary is built for emotion.**

Because love deserves a UI too.

>>>>>>> 5532403 (Devadasu Diary app)
