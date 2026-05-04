package com.king250

import io.ktor.server.application.*
import io.ktor.http.CacheControl
import io.ktor.http.content.CachingOptions
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.request.path

fun Application.configureHttp() {
    install(CachingHeaders) {
        options { call, _ ->
            if (call.request.path().startsWith("/background/")) {
                CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60))
            } else {
                null
            }
        }
    }
}
