package me.kaiwells.klox

import java.io.*
import java.lang.IllegalStateException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    when (args.size) {
        0 -> repl()
        1 -> runFile(args.first())
        else -> {
            println("Usage: klox [script]")
            exitProcess(64)
        }
    }
}

private fun run(source: String) {
    val tokens = Lexer(source).lex()
    val ast = Parser(tokens).parse()
    println(AstStringer().stringify(ast))
}

private fun repl() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)
    while (true) {
        try {
            print("> ")
            val line = reader.readLine()
            run(line)
        }
        catch (e: IllegalStateException) {
            return
        }
    }
}

private fun runFile(filepath: String) {
    val source = File(filepath).readText(Charsets.UTF_8)
    run(source)
}
