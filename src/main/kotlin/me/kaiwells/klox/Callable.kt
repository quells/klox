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

class Function(private val declaration: Stmt.Function) : Callable {

    override fun arity(): Int {
        return declaration.params.size
    }

    override fun call(i: Interpreter, args: List<Any?>): Any? {
        val env = Environment(enclosing = interpreter.globals)
        declaration.params.forEachIndexed { idx, param ->
            env.define(param.lexeme, args[idx])
        }
        i.executeBlock(declaration.body, env)
        return null
    }
}