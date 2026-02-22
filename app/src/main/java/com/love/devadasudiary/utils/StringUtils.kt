package com.love.devadasudiary.utils

fun stripHtmlTags(text: String): String =
    text.replace(Regex("<[^>]*>"), "")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&gt;", ">")
        .trim()