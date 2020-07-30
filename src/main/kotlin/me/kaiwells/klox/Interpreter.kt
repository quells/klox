package me.kaiwells.klox

import me.kaiwells.klox.Token.Type.*

class Interpreter (
        private var env: Environment = Environment()
) : Expr.Visitor<Any?>, Stmt.Visitor<Any?> {

    fun interpret(statements: List<Stmt>): Any? {
        // return the result of the last statement
        val acc: Any? = null
        return statements.fold(acc) { _, stmt -> execute(stmt) }
    }

    private fun execute(stmt: Stmt): Any? {
        return stmt.accept(this)
    }

    /* Expr.Visitor<Any?> */

    private fun eval(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun asNumber(v: Any?): Double {
        return if (v is Double) v else 0.0
    }

    private fun isTruthy(v: Any?): Boolean {
        val x = v ?: return false
        return when (x) {
            is Boolean -> x
            is Double -> x != 0.0
            is String -> x != ""
            else -> true
        }
    }

    private fun isEqual(left: Any?, right: Any?): Boolean {
        return when {
            left == null && right == null -> true
            left is Double && right is Double -> left == right
            left is String && right is String -> left == right
            left is Boolean && right is Boolean -> left == right
            else -> false
        }
    }

    override fun visitAssign(expr: Expr.Assign): Any? {
        val value = eval(expr.value)
        env.assign(expr.name, value)
        return value
    }

    override fun visitBinary(expr: Expr.Binary): Any? {
        val left = eval(expr.left)
        val right = eval(expr.right)
        val l = asNumber(left)
        val r = asNumber(right)
        return when (expr.op.type) {
            Minus -> l - r
            Slash -> l / r
            Star -> l * r
            Plus -> {
                when {
                    left is Double && right is Double -> l + r
                    left is String && right is String -> left + right
                    else -> null
                }
            }
            Greater -> l > r
            GreaterEqual -> l >= r
            Less -> l < r
            LessEqual -> l <= r
            BangEqual -> !isEqual(left, right)
            EqualEqual -> isEqual(left, right)
            else -> null
        }
    }

    override fun visitCall(expr: Expr.Call): Any? {
        TODO("Not yet implemented")
    }

    override fun visitGet(expr: Expr.Get): Any? {
        TODO("Not yet implemented")
    }

    override fun visitGrouping(expr: Expr.Grouping): Any? {
        return eval(expr.expression)
    }

    override fun visitLiteral(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitLogical(expr: Expr.Logical): Any? {
        val left = eval(expr.left)
        return when (expr.op.type) {
            Or -> if (isTruthy(left)) left else eval(expr.right)
            And -> if (isTruthy(left)) eval(expr.right) else left
            else -> throw Error("invalid logical operator", expr.op)
        }
    }

    override fun visitSet(expr: Expr.Set): Any? {
        TODO("Not yet implemented")
    }

    override fun visitSuper(expr: Expr.Super): Any? {
        TODO("Not yet implemented")
    }

    override fun visitTernary(expr: Expr.Ternary): Any? {
        val condition = eval(expr.condition)
        return if (isTruthy(condition)) eval(expr.left) else eval(expr.right)
    }

    override fun visitThis(expr: Expr.This): Any? {
        TODO("Not yet implemented")
    }

    override fun visitUnary(expr: Expr.Unary): Any? {
        val right = eval(expr.right)
        return when (expr.op.type) {
            Bang -> !isTruthy(right)
            Minus -> -asNumber(right)
            else -> null
        }
    }

    override fun visitVariable(expr: Expr.Variable): Any? {
        return env.get(expr.name)
    }

    /* Stmt.Visitor<Any?> */

    override fun visitBlock(stmt: Stmt.Block): Any? {
        executeBlock(stmt.statements, Environment(enclosing = env))
        return null
    }

    private fun executeBlock(statements: List<Stmt>, inner: Environment) {
        val outer = env
        try {
            env = inner
            statements.forEach { execute(it) }
        } finally {
            env = outer
        }
    }

    override fun visitClass(stmt: Stmt.Class): Any? {
        TODO("Not yet implemented")
    }

    override fun visitExpression(stmt: Stmt.Expression): Any? {
        return eval(stmt.expression)
    }

    override fun visitFunction(stmt: Stmt.Function): Any? {
        TODO("Not yet implemented")
    }

    override fun visitIf(stmt: Stmt.If): Any? {
        return if (isTruthy(eval(stmt.condition))) {
            execute(stmt.thenBranch)
        } else {
            stmt.elseBranch?.let { execute(it) }
        }
    }

    override fun visitPrint(stmt: Stmt.Print): Any? {
        val value = eval(stmt.expression)
        println(value)
        return null
    }

    override fun visitReturn(stmt: Stmt.Return): Any? {
        TODO("Not yet implemented")
    }

    override fun visitVariable(stmt: Stmt.Variable): Any? {
        val initializer = stmt.initializer?.let { eval(it) }
        env.define(stmt.name.lexeme, initializer)
        return initializer
    }

    override fun visitWhile(stmt: Stmt.While): Any? {
        var last: Any? = null
        while (isTruthy(eval(stmt.condition))) {
            last = execute(stmt.body)
        }
        return last
    }

    class Error(msg: String, val token: Token) : RuntimeException(msg)
}
