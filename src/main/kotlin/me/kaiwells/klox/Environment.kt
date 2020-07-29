package me.kaiwells.klox

data class Environment (
        private val values: MutableMap<String, Any?> = mutableMapOf(),
        private val enclosing: Environment? = null
) {

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun assign(name: Token, value: Any?) {
        val key = name.lexeme
        if (values.containsKey(key)) {
            values[key] = value
        } else {
            enclosing?.assign(name, value) ?: throw undef(name)
        }
    }

    fun get(name: Token): Any? {
        val key = name.lexeme
        return if (values.containsKey(key)) values[key] else
            enclosing?.get(name) ?: throw undef(name)
    }

    private fun undef(name: Token): Interpreter.Error {
        return Interpreter.Error("Undefined variable '${name.lexeme}'", name)
    }
}
