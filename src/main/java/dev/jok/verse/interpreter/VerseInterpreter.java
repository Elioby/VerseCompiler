package dev.jok.verse.interpreter;

import dev.jok.verse.ast.Expr;
import dev.jok.verse.ast.Stmt;
import dev.jok.verse.types.number.VNumber;

import java.util.List;

public class VerseInterpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    public void interpret(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            interpret(stmt);
        }
    }

    private void interpret(Stmt stmt) {
        stmt.accept(this);
    }

    public void interpret(Expr expr) {
        Object value = evaluate(expr);
        System.out.println(value);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression expression) {
        evaluate(expression.expression);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block block) {
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function function) {
        return null;
    }

    @Override
    public Void visitParameterStmt(Stmt.Parameter parameter) {
        return null;
    }

    @Override
    public Void visitVariableDeclarationStmt(Stmt.VariableDeclaration variableDeclaration) {
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print print) {
        Object value = evaluate(print.expression);
        System.out.println(value);
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign assign) {
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary binary) {
        Object left = evaluate(binary.left);
        Object right = evaluate(binary.right);

        return switch (binary.operator.type) {
            case PLUS -> {
                if (left instanceof VNumber<?> leftVal && right instanceof VNumber<?> rightVal) {
                    yield leftVal.add(rightVal);
                }

                throw runtimeError(binary, "Expected numbers");
            }

            case MINUS -> {
                if (left instanceof VNumber<?> leftVal && right instanceof VNumber<?> rightVal) {
                    yield leftVal.subtract(rightVal);
                }

                throw runtimeError(binary, "Expected numbers");
            }

            case STAR -> {
                if (left instanceof VNumber<?> leftVal && right instanceof VNumber<?> rightVal) {
                    yield leftVal.multiply(rightVal);
                }

                throw runtimeError(binary, "Expected numbers");
            }

            case SLASH -> {
                if (left instanceof VNumber<?> leftVal && right instanceof VNumber<?> rightVal) {
                    yield leftVal.divide(rightVal);
                }

                throw runtimeError(binary, "Expected numbers");
            }

            case EQUALS -> left.equals(right);

            case GREATER -> {
                if (left instanceof VNumber<?> leftVal && right instanceof VNumber<?> rightVal) {
                    yield leftVal.greaterThan(rightVal);
                }

                throw runtimeError(binary, "Expected numbers");
            }

            case GREATER_EQUAL -> {
                if (left instanceof VNumber<?> leftVal && right instanceof VNumber<?> rightVal) {
                    yield leftVal.greaterThanOrEqual(rightVal);
                }

                throw runtimeError(binary, "Expected numbers");
            }

            case LESS -> {
                if (left instanceof VNumber<?> leftVal && right instanceof VNumber<?> rightVal) {
                    yield leftVal.lessThan(rightVal);
                }

                throw runtimeError(binary, "Expected numbers");
            }

            case LESS_EQUAL -> {
                if (left instanceof VNumber<?> leftVal && right instanceof VNumber<?> rightVal) {
                    yield leftVal.lessThanOrEqual(rightVal);
                }

                throw runtimeError(binary, "Expected numbers");
            }

            default -> throw internalError(binary, "Unknown binary operator: " + binary.operator.type);
        };
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping grouping) {
        return evaluate(grouping.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal literal) {
        return literal.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary unary) {
        Object right = evaluate(unary.right);

        return switch (unary.operator.type) {
            case MINUS -> {
                if (right instanceof VNumber<?> val) {
                    yield val.negate();
                }

                throw runtimeError(unary, "Expected number");
            }

            case NOT -> !isTruthy(right);

            default -> null;
        };
    }

    @Override
    public Object visitVariableExpr(Expr.Variable variable) {
        System.out.println(variable);

        return variable;
    }

    @Override
    public Object visitCallExpr(Expr.Call call) {
        return null;
    }

    private boolean isTruthy(Object obj) {
        if (obj instanceof Boolean val) {
            return val;
        }

        return false;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    public static RuntimeError runtimeError(Expr expr, String message) {
        return new RuntimeError("Runtime error at " + expr + ": " + message);
    }

    public static InternalError internalError(Expr expr, String message) {
        return new InternalError("Internal error interpreting expression " + expr + ": " + message);
    }

    // @Todo(Jok) @Error: we should use better interpreter handling for this kind of error
    @Deprecated
    public static InternalError internalError(String message) {
        return new InternalError("Internal error interpreting " + ": " + message);
    }

    private static final class RuntimeError extends RuntimeException {

        public RuntimeError(String message) {
            super(message);
        }

    }
    private static final class InternalError extends RuntimeException {

        public InternalError(String message) {
            super(message);
        }

    }

}
