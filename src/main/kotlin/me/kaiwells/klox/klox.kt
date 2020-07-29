package me.kaiwells.klox

import java.io.*
import java.lang.IllegalStateException
import kotlin.system.exitProcess

val astStringer = AstStringer()
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

private fun run(source: String, repl: Boolean = false): Boolean {
    val tokens = Lexer(source).lex()
    var hadErrors = hadLexerErrors(source, tokens)
    try {
        val ast = Parser(tokens).parse()
        println(astStringer.stringify(ast))
        if (!hadErrors) {
            interpreter.interpret(ast)?.let {
                if (repl) println(it)
            }
        }
    }
    catch (e: Parser.Error) {
        handleParserError(source, e)
        hadErrors = true
    }
    return hadErrors
}

private fun hadLexerErrors(source: String, tokens: List<Token>): Boolean {
    var hadErrors = false
    val lines = source.split('\n')
    for (t in tokens) {
        if (t.type == Token.Type.Error) {
            hadErrors = true
            println(lines[t.line - 1])
            val leadingSpaceSize = t.column - 1
            var leadingSpace = " ".repeat(leadingSpaceSize)
            println("${leadingSpace}^")
            val msg = "[${t.line}:${t.column}] ${t.literal}"
            if (msg.length <= leadingSpaceSize) {
                leadingSpace = " ".repeat(leadingSpaceSize - msg.length + 1)
            }
            println("$leadingSpace$msg")
        }
    }
    return hadErrors
}

private fun handleParserError(source: String, e: Parser.Error) {
    val lines = source.split('\n')
    val line = when (e.token.type) {
        Token.Type.EOF -> lines.last()
        else -> lines[e.token.line - 1]
    }
    println(line)
    val leadingSpaceSize = when (e.token.type) {
        Token.Type.EOF -> 0
        else -> e.token.column - 1
    }
    val leadingSpace = " ".repeat(leadingSpaceSize)
    println("$leadingSpace^")
    println(e.message)
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
