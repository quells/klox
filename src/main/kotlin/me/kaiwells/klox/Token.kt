package me.kaiwells.klox

data class Token(
    val type: Type,
    val lexeme: String,
    val literal: Any? = null,
    val line: Int = 0,
    val column: Int = 0
) {
    enum class Type {
        LeftParen, RightParen, LeftBrace, RightBrace, Comma, Dot, Semicolon, Plus, Minus, Star, Slash, Question, Colon,
        Bang, BangEqual,
        Equal, EqualEqual,
        Greater, GreaterEqual,
        Less, LessEqual,
        Identifier, String, Number,
        And, Break, Class, Else, False, Fun, For, If, Nil, Or, Print, Return, Super, This, True, Var, While,
        EOF, Error, Comment
    }

    override fun toString(): String {
        val lit = if (literal == null) "" else " $literal"
        return "$type $lexeme$lit"
    }
}
