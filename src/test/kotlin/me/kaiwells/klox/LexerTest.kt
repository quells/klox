package me.kaiwells.klox

import org.junit.Test
import java.io.File
import me.kaiwells.klox.Token.Type.*
import kotlin.test.assertEquals

class LexerTest {
    @Test
    fun lexTokens() {
        val f = File("src/test/resources/tokens.txt")
        val source = f.readText(Charsets.UTF_8)
        val actualTokens = Lexer(source).lex()

        val expectedTokens = listOf(
            Token(Comment, "// this is a comment", " this is a comment"),
            Token(LeftParen, "("),
            Token(LeftParen, "("),
            Token(RightParen, ")"),
            Token(LeftBrace, "{"),
            Token(RightBrace, "}"),
            Token(Comment, "// grouping stuff", " grouping stuff"),
            Token(Bang, "!"),
            Token(Star, "*"),
            Token(Plus, "+"),
            Token(Minus, "-"),
            Token(Slash, "/"),
            Token(Equal, "="),
            Token(Less, "<"),
            Token(Greater, ">"),
            Token(LessEqual, "<="),
            Token(GreaterEqual, ">="),
            Token(EqualEqual, "=="),
            Token(BangEqual, "!="),
            Token(Comment, "// operators", " operators"),
            Token(Token.Type.String, "\"a man a plan a canal panama\"", "a man a plan a canal panama"),
            Token(Comment, "// string", " string"),
            Token(Token.Type.String, "\"four score\nand seven years ago\"", "four score\nand seven years ago"),
            Token(Comment, "// multi-line string", " multi-line string"),
            Token(Number, "0123", 123.0),
            Token(Number, "3.14", 3.14),
            Token(And, "and"),
            Token(Class, "class"),
            Token(Else, "else"),
            Token(False, "false"),
            Token(For, "for"),
            Token(Fun, "fun"),
            Token(If, "if"),
            Token(Nil, "nil"),
            Token(Or, "or"),
            Token(Print, "print"),
            Token(Return, "return"),
            Token(Super, "super"),
            Token(This, "this"),
            Token(True, "true"),
            Token(Var, "var"),
            Token(While, "while"),
            Token(Identifier, "_identify_THIS_9001"),
            Token(Comment, "///* this is not a block comment", "/* this is not a block comment"),
            Token(Comment, "/* this is\na block comment\n/* with nested blocks */*/", " this is\na block comment\n/* with nested blocks */"),
            Token(Error, "\" // it goes on an on and on and", "unterminated string"),
            Token(EOF, "")
        )

        assertEquals(expectedTokens.size, actualTokens.size, "unexpected token count")
        for (i in actualTokens.indices) {
            val actual = actualTokens[i]
            val expected = expectedTokens[i]
            assertEquals(expected.type, actual.type, "unexpected type $actual")
            assertEquals(expected.lexeme, actual.lexeme, "unexpected source $actual")
            assertEquals(expected.literal, actual.literal, "unexpected literal $actual")
        }
    }
}