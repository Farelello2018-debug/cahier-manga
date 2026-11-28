package com.matchs.tv

data class Channel(val name: String, val group: String, val url: String)

/** Garde la liste en mémoire pour que le lecteur puisse passer d'une chaîne à l'autre. */
object ChannelStore {
    var channels: List<Channel> = emptyList()
}
