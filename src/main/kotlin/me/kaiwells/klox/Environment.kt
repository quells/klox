package me.kaiwells.klox

data class Environment (
        private val values: MutableMap<String, Any?> = mutableMapOf()
) {

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun get(name: Token): Any? {
        val key = name.lexeme
        return if (values.containsKey(key)) values[key] else
            throw Interpreter.Error("Undefined variable '${name.lexeme}'", name)
    }
}
