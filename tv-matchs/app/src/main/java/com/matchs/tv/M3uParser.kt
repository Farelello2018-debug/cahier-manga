package com.matchs.tv

import java.net.HttpURLConnection
import java.net.URL

object M3uParser {

    private val attr = Regex("""([a-zA-Z-]+)="([^"]*)"""")

    fun download(address: String): String {
        var url = URL(address)
        // Suit les redirections, y compris http -> https.
        repeat(5) {
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 15000
            conn.readTimeout = 30000
            conn.instanceFollowRedirects = false
            conn.setRequestProperty("User-Agent", "MatchsTV/1.0")
            val code = conn.responseCode
            if (code in 300..399) {
                val location = conn.getHeaderField("Location") ?: error("Redirection sans adresse")
                url = URL(url, location)
                conn.disconnect()
            } else {
                if (code !in 200..299) error("Le serveur a répondu $code")
                return conn.inputStream.bufferedReader().use { it.readText() }
            }
        }
        error("Trop de redirections")
    }

    fun parse(text: String): List<Channel> {
        val result = ArrayList<Channel>()
        var name: String? = null
        var group = ""
        for (raw in text.lineSequence()) {
            val line = raw.trim()
            when {
                line.isEmpty() -> Unit
                line.startsWith("#EXTINF", ignoreCase = true) -> {
                    val attrs = attr.findAll(line).associate { it.groupValues[1].lowercase() to it.groupValues[2] }
                    group = attrs["group-title"].orEmpty()
                    name = line.substringAfterLast(',', "").trim().ifEmpty { attrs["tvg-name"] }
                }
                line.startsWith("#EXTGRP:", ignoreCase = true) -> group = line.substringAfter(':').trim()
                line.startsWith("#") -> Unit
                else -> {
                    result.add(Channel(name ?: line, group, line))
                    name = null
                    group = ""
                }
            }
        }
        return result
    }
}
