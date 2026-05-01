# OkHttp / Okio
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**

# Markwon (uses commonmark via reflection in some plugins)
-keep class io.noties.markwon.** { *; }
-dontwarn io.noties.markwon.**
-keep class org.commonmark.** { *; }
-dontwarn org.commonmark.**

# Kotlinx Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Compose runtime keeps
-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
