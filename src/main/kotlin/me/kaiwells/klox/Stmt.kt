package me.kaiwells.klox

abstract class Stmt {
    interface Visitor<R> {
        fun visitBlock(stmt: Block): R
        fun visitClass(stmt: Class): R
        fun visitExpression(stmt: Expression): R
        fun visitFunction(stmt: Function): R
        fun visitIf(stmt: If): R
        fun visitPrint(stmt: Print): R
        fun visitReturn(stmt: Return): R
        fun visitVariable(stmt: Variable): R
        fun visitWhile(stmt: While): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    data class Block(val statements: List<Stmt>) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitBlock(this)
        }
    }
    data class Class(val name: Token, val superclass: Expr.Variable, val methods: List<Function>) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitClass(this)
        }
    }
    data class Expression(val expression: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitExpression(this)
        }
    }
    data class Function(val name: Token, val params: List<Token>, val body: List<Stmt>) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitFunction(this)
        }
    }
    data class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitIf(this)
        }
    }
    data class Print(val expression: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitPrint(this)
        }
    }
    data class Return(val keyword: Token, val value: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitReturn(this)
        }
    }
    data class Variable(val name: Token, val initializer: Expr?) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitVariable(this)
        }
    }
    data class While(val condition: Expr, val body: Stmt) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitWhile(this)
        }
    }
}
