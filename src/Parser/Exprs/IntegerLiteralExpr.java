package Parser.Exprs;

public class IntegerLiteralExpr implements Expr {
    public int value;

    public IntegerLiteralExpr(int value) {
        this.value = value;
    }

    @Override
    public void log() {
        System.out.printf("Integer Literal Expr:\n\tValue: %d\n", value);
    }
}
