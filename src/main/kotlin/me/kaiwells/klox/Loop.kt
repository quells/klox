package me.kaiwells.klox

data class Loop (
    val token: Token,
    val enclosing: Loop? = null,
    var broken: Boolean = false
)
