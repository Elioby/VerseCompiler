package dev.jok.verse.ast.types.expr;

import dev.jok.verse.ast.AstVisitor;
import dev.jok.verse.ast.types.AstExpr;
import dev.jok.verse.lexer.Token;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AstBinaryExpr extends AstExpr {

    public final AstExpr left;
    public final Token operator;
    public final AstExpr right;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitBinaryExpr(this);
    }
}
