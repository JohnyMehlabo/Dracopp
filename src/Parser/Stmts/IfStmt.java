package Parser.Stmts;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Compiler;
import Compiler.Types.Type;
import Lexer.Token;
import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Parser;

public class IfStmt implements Stmt {
    final Expr condition;
    final Stmt body;
    final Stmt elseBody;

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
        if (elseBody == null) {
            Assembler.jz(String.format("if_%d_end", currentIfCount));
            body.codegen();
            Assembler.addLocalLabel(String.format("if_%d_end", currentIfCount));
        } else {
            Assembler.jz(String.format("else_%d", currentIfCount));
            body.codegen();
            Assembler.jmp(String.format("if_%d_end", currentIfCount));
            Assembler.addLocalLabel(String.format("else_%d", currentIfCount));
            elseBody.codegen();
            Assembler.addLocalLabel(String.format("if_%d_end", currentIfCount));
        }
    }

    private IfStmt(Expr condition, Stmt body, Stmt elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    public static IfStmt parse() {
        Parser.eat();
        Parser.expect(TokenType.OpenParen, "Expected opening '('");
        Expr condition = Parser.parseExpr();
        Parser.expect(TokenType.CloseParen, "Expected closing ')'");
        Stmt body = Parser.parseStmt();
        Stmt elseBody = null;

        if (Parser.at().kind == TokenType.Else) {
            Parser.eat();
            elseBody = Parser.parseStmt();
        }

        return new IfStmt(condition, body, elseBody);
    }
}
