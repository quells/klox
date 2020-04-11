package me.kaiwells.klox

import me.kaiwells.klox.Token.Type.*

class Lexer(private val source: String) {
    private val tokens: MutableList<Token> = mutableListOf()
    private var start = 0
    private var current = 0
    private var line = 1
    private var column = 0
    private var blockCommentDepth = 0

    fun lex(): List<Token> {
        while (!atEnd()) {
            start = current
            scanToken()
        }
        tokens.add(Token(EOF, "", null, line, column))
        return tokens
    }

    private fun atEnd(): Boolean {
        return current >= source.length
    }

    private fun advance(): Char {
        return if (atEnd()) {
            '\u0000'
        } else {
            current++
            column++
            val c = source[current - 1]
            if (c == '\n') {
                line++
                column = 0
            }
            c
        }
    }

    private fun peek(): Char {
        return if (atEnd()) {
            '\u0000'
        } else {
            source[current]
        }
    }

    private fun peekNext(): Char {
        return if (current + 1 >= source.length) {
            '\u0000'
        } else {
            source[current + 1]
        }
    }

    private fun addToken(type: Token.Type, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line, column))
    }

    private fun match(expected: Char): Boolean {
        if (peek() != expected) {
            return false
        }
        advance()
        return true
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'A'..'Z' || c in 'a'..'z' || c == '_'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun scanToken() {
        when (val c = advance()) {
            '(' -> addToken(LeftParen)
            ')' -> addToken(RightParen)
            '{' -> addToken(LeftBrace)
            '}' -> addToken(RightBrace)
            ',' -> addToken(Comma)
            '.' -> addToken(Dot)
            ';' -> addToken(Semicolon)
            '+' -> addToken(Plus)
            '-' -> addToken(Minus)
            '*' -> addToken(Star)
            '?' -> addToken(Question)
            ':' -> addToken(Colon)
            '!' -> if (match('=')) addToken(BangEqual) else addToken(Bang)
            '=' -> if (match('=')) addToken(EqualEqual) else addToken(Equal)
            '<' -> if (match('=')) addToken(LessEqual) else addToken(Less)
            '>' -> if (match('=')) addToken(GreaterEqual) else addToken(Greater)
            '/' -> when {
                match('/') -> addComment()
                match('*') -> addBlockComment()
                else -> addToken(Slash)
            }
            '"' -> addString()
            ' ' -> return
            '\r' -> return
            '\t' -> return
            '\n' -> return
            else -> {
                when {
                    isDigit(c) -> addNumber()
                    isAlpha(c) -> addIdentifier()
                    else -> addToken(Error, "invalid input")
                }
            }
        }
    }

    private fun addComment() {
        while (peek() != '\n' && !atEnd()) {
            advance()
        }
        val text = source.substring(start + 2, current)
        addToken(Comment, text)
    }

    private fun addBlockComment() {
        blockCommentDepth++
        while (!atEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                blockCommentDepth--
                advance()
                if (blockCommentDepth == 0) {
                    advance()
                    break
                }
            } else if (peek() == '/' && peekNext() == '*') {
                blockCommentDepth++
                advance()
            }

            advance()
        }

        if (atEnd()) {
            addToken(Error, "unterminated block quote")
        } else {
            val text = source.substring(start + 2, current - 2)
            addToken(Comment, text)
        }
    }

    private fun addString() {
        while (peek() != '"' && !atEnd()) {
            advance()
        }

        if (atEnd()) {
            addToken(Error, "unterminated string")
            return
        }

        advance() // trailing "
        val text = source.substring(start + 1, current - 1)
        addToken(Token.Type.String, text)
    }

    private fun addNumber() {
        while (isDigit(peek())) {
            advance()
        }
        if (peek() == '.' && isDigit(peekNext())) {
            advance() // .
            while (isDigit(peek())) {
                advance()
            }
        }

        addToken(Number, source.substring(start, current).toDouble())
    }

    private fun addIdentifier() {
        while (isAlphaNumeric(peek())) {
            advance()
        }

        val text = source.substring(start, current)
        addToken(keywords[text] ?: Identifier)
    }

    companion object {
        val keywords = mapOf(
            "and" to And,
            "class" to Class,
            "else" to Else,
            "false" to False,
            "for" to For,
            "fun" to Fun,
            "if" to If,
            "nil" to Nil,
            "or" to Or,
            "print" to Print,
            "return" to Return,
            "super" to Super,
            "this" to This,
            "true" to True,
            "var" to Var,
            "while" to While
        )
    }
}
