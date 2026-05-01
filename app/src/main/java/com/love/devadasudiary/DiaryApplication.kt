package com.love.devadasudiary

import android.app.Application

/**
 * Application entry point. The class itself is intentionally light — its
 * presence in the manifest is what matters: it ensures the app uses an
 * explicit `Application` subclass for future hooks (DI, logging, etc.)
 * and gives DataStore a known process-wide owner.
 */
class DiaryApplication : Application()
