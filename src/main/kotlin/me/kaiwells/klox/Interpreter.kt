package me.kaiwells.klox

import me.kaiwells.klox.Token.Type.*

class Interpreter (
        private val env: Environment = Environment()
) : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {

    fun interpret(statements: List<Stmt>) {
        statements.forEach { execute(it) }
    }

    private fun execute(stmt: Stmt) {
        stmt.accept(this)
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun visitSet(expr: Expr.Set): Any? {
        TODO("Not yet implemented")
    }

    override fun visitSuper(expr: Expr.Super): Any? {
        TODO("Not yet implemented")
    }

    override fun visitTernary(expr: Expr.Ternary): Any? {
        val condition = eval(expr.condition)
        val left = eval(expr.left)
        val right = eval(expr.right)
        return if (isTruthy(condition)) left else right
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

    /* Stmt.Visitor<Unit> */

    override fun visitBlock(stmt: Stmt.Block) {
        TODO("Not yet implemented")
    }

    override fun visitClass(stmt: Stmt.Class) {
        TODO("Not yet implemented")
    }

    override fun visitExpression(stmt: Stmt.Expression) {
        eval(stmt.expression)
    }

    override fun visitFunction(stmt: Stmt.Function) {
        TODO("Not yet implemented")
    }

    override fun visitIf(stmt: Stmt.If) {
        TODO("Not yet implemented")
    }

    override fun visitPrint(stmt: Stmt.Print) {
        val value = eval(stmt.expression)
        println(value)
    }

    override fun visitReturn(stmt: Stmt.Return) {
        TODO("Not yet implemented")
    }

    override fun visitVariable(stmt: Stmt.Variable) {
        val initializer = stmt.initializer?.let { eval(it) }
        env.define(stmt.name.lexeme, initializer)
    }

    override fun visitWhile(stmt: Stmt.While) {
        TODO("Not yet implemented")
    }

    class Error(msg: String, val token: Token) : RuntimeException(msg)
}
