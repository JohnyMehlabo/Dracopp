package Parser.Stmts;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Compiler;
import Compiler.Types.Type;
import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Parser;

public class WhileStmt implements Stmt {
    final Expr condition;
    final Stmt body;

    @Override
    public void log() {
        System.out.println("While Statement: ");
        System.out.println("\tCondition: ");
        condition.log();
        System.out.println("\tBody: ");
        body.log();
    }

    @Override
    public void codegen(){
        // TODO: Fix bug where a variable gets declared over and over
        int currentWhileCount = Compiler.whileCount;

        Compiler.whileCount++;

        Assembler.addLocalLabel(String.format("while_%d_start", currentWhileCount));
        Type type = condition.codegen();
        Assembler.test(new RegisterMemory(Register.x32.EAX), type.getSize(), Register.x32.EAX.ordinal(), type.getSize());
        Assembler.jz(String.format("while_%d_end", currentWhileCount));

        body.codegen();

        Assembler.jmp(String.format("while_%d_start", currentWhileCount));
        Assembler.addLocalLabel(String.format("while_%d_end", currentWhileCount));
    }

    private WhileStmt(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }

    public static WhileStmt parse() {
        Parser.eat();
        Parser.expect(TokenType.OpenParen, "Expected opening '('");
        Expr condition = Parser.parseExpr();
        Parser.expect(TokenType.CloseParen, "Expected closing ')'");
        Stmt body = Parser.parseStmt();

        return new WhileStmt(condition, body);
    }
}
