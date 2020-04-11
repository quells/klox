package me.kaiwells.klox

import java.io.*
import java.lang.IllegalStateException
import kotlin.system.exitProcess

val interpreter = Interpreter()

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

private fun run(source: String): Boolean {
    val tokens = Lexer(source).lex()
    var hadErrors = hadLexerErrors(tokens)
    try {
        val ast = Parser(tokens).parse()
        println(AstStringer().stringify(ast))
        if (!hadErrors) {
            val result = interpreter.evaluate(ast)
            println(result)
        }
    }
    catch (e: Parser.ParseException) {
        println(e.message)
        hadErrors = true
    }
    return hadErrors
}

private fun hadLexerErrors(tokens: List<Token>): Boolean {
    var hadErrors = false
    for (t in tokens) {
        if (t.type == Token.Type.Error) {
            hadErrors = true
            println("[${t.line}:${t.column}] Error: ${t.literal}")
        }
    }
    return hadErrors
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
    if (run(source)) {
        exitProcess(65)
    }
}
