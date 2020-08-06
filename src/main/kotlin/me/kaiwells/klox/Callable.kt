package me.kaiwells.klox

import kotlin.system.exitProcess

interface Callable {
    fun arity(): Int
    fun call(i: Interpreter, args: List<Any?>): Any?
}

/**
 * Get the current epoch time in seconds with millisecond precision.
 */
class Clock : Callable {

    override fun arity(): Int {
        return 0
    }

    override fun call(i: Interpreter, args: List<Any?>): Any? {
        return (System.currentTimeMillis().toDouble()) / 1000.0
    }

    override fun toString(): String {
        return "<native fn>"
    }
}

/**
 * Cleanly exit the program or REPL.
 */
class Exit : Callable {

    override fun arity(): Int {
        return 0
    }

    override fun call(i: Interpreter, args: List<Any?>): Any? {
        exitProcess(0)
    }

    override fun toString(): String {
        return "<native fn>"
    }
}

class Function(
    private val name: Token,
    private val params: List<Token>,
    private val body: List<Stmt>,
    private val closure: Environment
) : Callable {

    override fun arity(): Int {
        return params.size
    }

    override fun call(i: Interpreter, args: List<Any?>): Any? {
        val env = Environment(enclosing = closure)
        params.forEachIndexed { idx, param ->
            env.define(param.lexeme, args[idx])
        }
        try {
            i.executeBlock(body, env)
        } catch (ret: Interpreter.Return) {
            return ret.value
        }
        return null
    }

    override fun toString(): String {
        return "<fn ${name.lexeme}>"
    }
}
