package me.kaiwells.klox

abstract class Expr {
    interface Visitor<R> {
        fun visitAssign(expr: Assign): R
        fun visitBinary(expr: Binary): R
        fun visitCall(expr: Call): R
        fun visitFunction(expr: Function): R
        fun visitGet(expr: Get): R
        fun visitGrouping(expr: Grouping): R
        fun visitLiteral(expr: Literal): R
        fun visitLogical(expr: Logical): R
        fun visitSet(expr: Set): R
        fun visitSuper(expr: Super): R
        fun visitTernary(expr: Ternary): R
        fun visitThis(expr: This): R
        fun visitUnary(expr: Unary): R
        fun visitVariable(expr: Variable): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    data class Assign(val name: Token, val value: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitAssign(this)
        }
    }
    data class Binary(val left: Expr, val op: Token, val right: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitBinary(this)
        }
    }
    data class Call(val callee: Expr, val paren: Token, val args: List<Expr>) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitCall(this)
        }
    }
    data class Function(val params: List<Token>, val body: List<Stmt>) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitFunction(this)
        }
    }
    data class Get(val obj: Expr, val name: Token) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitGet(this)
        }
    }
    data class Grouping(val expression: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitGrouping(this)
        }
    }
    data class Literal(val value: Any?) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitLiteral(this)
        }
    }
    data class Logical(val left: Expr, val op: Token, val right: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitLogical(this)
        }
    }
    data class Set(val obj: Expr, val name: Token, val value: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitSet(this)
        }
    }
    data class Super(val keyword: Token, val method: Token) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitSuper(this)
        }
    }
    data class Ternary(val condition: Expr, val q: Token, val left: Expr, val c: Token, val right: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitTernary(this)
        }
    }
    data class This(val keyword: Token) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitThis(this)
        }
    }
    data class Unary(val op: Token, val right: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitUnary(this)
        }
    }
    data class Variable(val name: Token) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitVariable(this)
        }
    }
}
