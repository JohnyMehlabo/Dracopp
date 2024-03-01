package Parser.Stmts;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Compiler;
import Compiler.Elf.ElfHandler;
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
        ElfHandler.Text.addLabel(String.format("while_%d_start", Compiler.whileCount), 0);
        condition.codegen();
        Assembler.test(new RegisterMemory32(Register.x32.EAX), Register.x32.EAX);
        Assembler.jz(String.format("while_%d_end", Compiler.whileCount));

        body.codegen();
        Assembler.jmp(String.format("while_%d_start", Compiler.whileCount));
        ElfHandler.Text.addLabel(String.format("while_%d_end", Compiler.whileCount), 0);

        Compiler.whileCount++;
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
