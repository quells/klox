package me.kaiwells.klox

class AstStringer : Expr.Visitor<String>, Stmt.Visitor<String> {
    fun stringify(statements: List<Stmt>): String {
        return statements.joinToString(" ") { it.accept(this) }
    }

    private fun parenthesize(name: String, exprs: List<Expr>): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        exprs.forEach {
            builder.append(" ").append(it.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }

    private fun parenthesize(name: String, token: Token, exprs: List<Expr>): String {
        val builder = StringBuilder()
        builder.append("(").append(name).append(" ").append(token.lexeme)
        exprs.forEach {
            builder.append(" ").append(it.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }

    /* Expr.Visitor<String> */

    override fun visitAssign(expr: Expr.Assign): String {
        return parenthesize("assign", expr.name, listOf(expr.value))
    }
    override fun visitBinary(expr: Expr.Binary): String {
        return parenthesize(expr.op.lexeme, listOf(expr.left, expr.right))
    }
    override fun visitCall(expr: Expr.Call): String {
        val exprs = mutableListOf(expr.callee)
        exprs.addAll(expr.args)
        return parenthesize("call", exprs)
    }
    override fun visitGet(expr: Expr.Get): String {
        return parenthesize("get", expr.name, listOf(expr.obj))
    }
    override fun visitGrouping(expr: Expr.Grouping): String {
        return parenthesize("group", listOf(expr.expression))
    }
    override fun visitLiteral(expr: Expr.Literal): String {
        return (expr.value ?: "nil").toString()
    }
    override fun visitLogical(expr: Expr.Logical): String {
        return parenthesize(expr.op.lexeme, listOf(expr.left, expr.right))
    }
    override fun visitSet(expr: Expr.Set): String {
        return parenthesize("set", expr.name, listOf(expr.obj, expr.value))
    }
    override fun visitSuper(expr: Expr.Super): String {
        return parenthesize(expr.keyword.lexeme, expr.method, emptyList())
    }
    override fun visitTernary(expr: Expr.Ternary): String {
        return parenthesize(expr.q.lexeme, listOf(expr.condition, expr.left, expr.right))
    }
    override fun visitThis(expr: Expr.This): String {
        return expr.keyword.lexeme
    }
    override fun visitUnary(expr: Expr.Unary): String {
        return parenthesize(expr.op.lexeme, listOf(expr.right))
    }
    override fun visitVariable(expr: Expr.Variable): String {
        return expr.name.lexeme
    }

    /* Stmt.Visitor<String> */

    override fun visitBlock(stmt: Stmt.Block): String {
        val builder = StringBuilder()
        builder.append("{ ")
        builder.append(stmt.statements.joinToString(" ") { it.accept(this) })
        builder.append(" }")
        return builder.toString()
    }
    override fun visitClass(stmt: Stmt.Class): String {
        TODO("Not yet implemented")
    }
    override fun visitExpression(stmt: Stmt.Expression): String {
        return stmt.expression.accept(this) + ";"
    }
    override fun visitFunction(stmt: Stmt.Function): String {
        TODO("Not yet implemented")
    }
    override fun visitIf(stmt: Stmt.If): String {
        TODO("Not yet implemented")
    }
    override fun visitPrint(stmt: Stmt.Print): String {
        val builder = StringBuilder()
        builder.append("print ").append(stmt.expression.accept(this)).append(';')
        return builder.toString()
    }
    override fun visitReturn(stmt: Stmt.Return): String {
        TODO("Not yet implemented")
    }
    override fun visitVariable(stmt: Stmt.Variable): String {
        val builder = StringBuilder()
        builder.append("var ").append(stmt.name.lexeme)
        stmt.initializer?.let { builder.append(" = ").append(it.accept(this)) }
        builder.append(';')
        return builder.toString()
    }
    override fun visitWhile(stmt: Stmt.While): String {
        TODO("Not yet implemented")
    }
}
