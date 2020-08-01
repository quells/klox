package me.kaiwells.klox

import me.kaiwells.klox.Token.Type.*

class Parser(private val source: String, private val tokens: List<Token>) {
    private var current = 0
    private val errors = mutableListOf<Error>()

    data class Result(val statements: List<Stmt>, val errors: List<Error>)

    fun parse(): Result {
        return try {
            val statements = mutableListOf<Stmt>()
            while (!atEnd()) {
                declaration()?.let { statements.add(it) }
            }
            Result(statements, errors)
        } catch (e: Error) {
            val t = peek()
            throw Error("[${t.line}:${t.column}] Error: ${e.message}, found ${t.type}", peek())
        }
    }

    private fun atEnd(): Boolean {
        return peek().type == EOF
    }

    private fun advance(): Token {
        if (!atEnd()) {
            current++
        }
        return previous()
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    private fun check(type: Token.Type): Boolean {
        return if (atEnd()) {
            false
        } else {
            when (peek().type) {
                Comment -> check(advance().type)
                Error -> check(advance().type)
                type -> true
                else -> false
            }
        }
    }

    private fun match(vararg expected: Token.Type): Boolean {
        for (type in expected) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun consume(expected: Token.Type, msg: String): Token {
        if (check(expected)) {
            return advance()
        }
        throw Error("expected $expected $msg", peek())
    }

    private fun synchronize() {
        advance()

        while (!atEnd()) {
            if (previous().type == Semicolon) {
                return
            }
            when (peek().type) {
                Class -> return
                Fun -> return
                Var -> return
                For -> return
                If -> return
                While -> return
                Print -> return
                Return -> return
                else -> advance()
            }
        }
    }

    private fun declaration(): Stmt? {
        return try {
            if (match(Var)) varDeclaration() else statement()
        } catch (err: Error) {
            synchronize()
            errors.add(err)
            null
        }
    }

    private fun varDeclaration(): Stmt {
        val name = consume(Identifier, "for variable name")
        val initializer = if (match(Equal)) expression() else null
        consume(Semicolon, "after variable declaration")
        return Stmt.Variable(name, initializer)
    }

    private fun statement(): Stmt {
        return when {
            match(If) -> ifStatement()
            match(For) -> forStatement()
            match(Break) -> breakStatement()
            match(Print) -> printStatement()
            match(While) -> whileStatement()
            match(LeftBrace) -> Stmt.Block(block())
            else -> expressionStatement()
        }
    }

    private fun ifStatement(): Stmt {
        consume(LeftParen, "after 'if'")
        val condition = expression()
        consume(RightParen, "after 'if' condition")
        val thenBranch = statement()
        val elseBranch = when {
            match(Else) -> statement()
            else -> null
        }
        return Stmt.If(condition, thenBranch, elseBranch)
    }

    private fun forStatement(): Stmt {
        val token = previous()
        consume(LeftParen, "after 'for'")
        val initializer = when {
            match(Semicolon) -> null
            match(Var) -> varDeclaration()
            else -> expressionStatement()
        }
        val condition = if (!check(Semicolon)) expression() else Expr.Literal(true)
        consume(Semicolon, "after 'for' condition")
        val increment = if (!check(RightParen)) expression() else null
        consume(RightParen, "after 'for' increment")
        var body = statement()
        increment?.let { body = Stmt.Block(listOf(body, Stmt.Expression(it))) }
        body = Stmt.While(token, condition, body)
        initializer?.let { body = Stmt.Block(listOf(it, body)) }
        return body
    }

    private fun breakStatement(): Stmt {
        val token = previous()
        consume(Semicolon, "after 'break'")
        return Stmt.Break(token)
    }

    private fun printStatement(): Stmt {
        val value = expression()
        consume(Semicolon, "after value to print")
        return Stmt.Print(value)
    }

    private fun whileStatement(): Stmt {
        val token = previous()
        consume(LeftParen, "after 'while'")
        val condition = expression()
        consume(RightParen, "after 'while' condition")
        val body = statement()
        return Stmt.While(token, condition, body)
    }

    private fun block(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!check(RightBrace) && !atEnd()) {
            declaration()?.let { statements.add(it) }
        }
        consume(RightBrace, "after block")
        return statements
    }

    private fun expressionStatement(): Stmt {
        val value = expression()
        consume(Semicolon, "after value")
        return Stmt.Expression(value)
    }

    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        val expr = ternary()
        if (match(Equal)) {
            val eq = previous()
            val value = ternary()

            return when (expr) {
                is Expr.Variable -> Expr.Assign(expr.name, value)
                else -> throw Error("invalid assignment target", eq)
            }
        }
        return expr
    }

    private fun ternary(): Expr {
        val expr = or()
        if (match(Question)) {
            val q = previous()
            val l = expression()
            val c = consume(Colon, "in ternary")
            val r = expression()
            return Expr.Ternary(expr, q, l, c, r)
        }
        return expr
    }

    private fun or(): Expr {
        var expr = and()
        while (match(Or)) {
            val op = previous()
            val right = and()
            expr = Expr.Logical(expr, op, right)
        }
        return expr
    }

    private fun and(): Expr {
        var expr = equality()
        while (match(And)) {
            val op = previous()
            val right = equality()
            expr = Expr.Logical(expr, op, right)
        }
        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()
        if (match(BangEqual, EqualEqual)) {
            val op = previous()
            val right = comparison()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    private fun comparison(): Expr {
        var expr = addition()
        if (match(Greater, GreaterEqual, Less, LessEqual)) {
            val op = previous()
            val right = addition()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    private fun addition(): Expr {
        var expr = multiplication()
        while (match(Plus, Minus)) {
            val op = previous()
            val right = multiplication()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    private fun multiplication(): Expr {
        var expr = unary()
        while (match(Star, Slash)) {
            val op = previous()
            val right = unary()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(Bang, Minus)) {
            val op = previous()
            val right = unary()
            return Expr.Unary(op, right)
        }
        return primary()
    }

    private fun primary(): Expr {
        return when {
            match(False) -> Expr.Literal(false)
            match(True) -> Expr.Literal(true)
            match(Nil) -> Expr.Literal(null)
            match(Number, Token.Type.String) -> Expr.Literal(previous().literal)
            match(Identifier) -> Expr.Variable(previous())
            match(LeftParen) -> {
                val e = expression()
                consume(RightParen, "after expression")
                Expr.Grouping(e)
            }
            else -> {
                throw Error("expected one of false, true, nil, NUMBER, STRING, VARIABLE, ( for expression", peek())
            }
        }
    }

    class Error(msg: String, val token: Token) : RuntimeException(msg)
}
