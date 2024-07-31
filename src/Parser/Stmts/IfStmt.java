package Parser.Stmts;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Compiler;
import Compiler.Types.Type;
import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Parser;

public class IfStmt implements Stmt {
    final Expr condition;
    final Stmt body;

    @Override
    public void log() {
        System.out.println("If Statement: ");
        System.out.println("\tCondition: ");
        condition.log();
        System.out.println("\tBody: ");
        body.log();
    }

    @Override
    public void codegen(){
        int currentIfCount = Compiler.ifCount;
        Type type = condition.codegen();
        Assembler.test(new RegisterMemory(Register.x32.EAX), type.getSize(), Register.x32.EAX.ordinal(), type.getSize());

        Compiler.ifCount++;
        Assembler.jz(String.format("if_%d_end", currentIfCount));
        body.codegen();
        Assembler.addLocalLabel(String.format("if_%d_end", currentIfCount));
    }

    private IfStmt(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }

    public static IfStmt parse() {
        Parser.eat();
        Parser.expect(TokenType.OpenParen, "Expected opening '('");
        Expr condition = Parser.parseExpr();
        Parser.expect(TokenType.CloseParen, "Expected closing ')'");
        Stmt body = Parser.parseStmt();

        return new IfStmt(condition, body);
    }
}
