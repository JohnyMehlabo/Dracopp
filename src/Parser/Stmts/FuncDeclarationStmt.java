package Parser.Stmts;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Compiler;
import Compiler.Elf.ElfHandler;
import Compiler.Types.Type;
import Lexer.Token;
import Lexer.TokenType;
import Parser.Parser;

import java.util.*;

public class FuncDeclarationStmt implements Stmt {
    String name;
    List<Stmt> body;
    Map<String, Type> args;
    Type returnType;
    boolean defined;

    static final List<Register.x32> ARG_REGISTER_LIST = new ArrayList<>(List.of(Register.x32.EAX, Register.x32.EBX, Register.x32.ECX, Register.x32.EDX, Register.x32.ESI, Register.x32.EDI));

    @Override
    public void log() {
        System.out.println("Function Declaration Statement:");
        System.out.printf("\tName: %s\n", name);
        if (defined) {
            System.out.println("\tBody:");
            for (Stmt stmt : body) {
                stmt.log();
            }
        }
    }

    @Override
    public void codegen() {
        Compiler.addFunction(name, returnType, args);

        if (!defined) return;
        Compiler.startScope();


        if (args.size() > ARG_REGISTER_LIST.size()) {
            System.err.printf("Function with too many arguments. Maximum of %d\n", ARG_REGISTER_LIST.size());
            System.exit(-1);
        }

        ElfHandler.Text.addLabel(name, 1);
        Assembler.push(Register.x32.EBP);
        Assembler.mov(new RegisterMemory32(Register.x32.EBP), Register.x32.ESP);

        for (int i = 0; i < args.keySet().size(); i++) {
            String argName = (String) args.keySet().toArray()[i];
            Type argType = args.get(argName);
            int typeSize = argType.getSize();
            Compiler.stackPtr += typeSize;
            Assembler.mov(new RegisterMemory(null, Register.x32.EBP, (byte) -Compiler.stackPtr), typeSize, ARG_REGISTER_LIST.get(i).ordinal(), typeSize);
            Assembler.sub(new RegisterMemory32(Register.x32.ESP), (byte) typeSize);
            Compiler.scope.declareVar(argName, argType, Compiler.stackPtr);
        }

        for (Stmt stmt : body) {
            stmt.codegen();
        }

        Assembler.leave();
        Assembler.ret();

        Compiler.endScope();

        Compiler.stackPtr = 0;
    }

    private FuncDeclarationStmt(String name, List<Stmt> body, Map<String, Type> args, Type returnType, boolean defined) {
        this.name = name;
        this.body = body;
        this.args = args;
        this.returnType = returnType;
        this.defined = defined;
    }

    private static Map.Entry<String, Type> parseArg() {
        Type type = Parser.parseType();
        Token nameToken = Parser.expect(TokenType.Identifier, "Expected name identifier");
        return new AbstractMap.SimpleEntry<>(nameToken.value, type);
    }

    public static FuncDeclarationStmt parse() {
        Parser.eat();

        String name = Parser.expect(TokenType.Identifier, "Expected identifier after 'func'").value;
        List<Stmt> body = new ArrayList<>();
        Map<String, Type> args = new HashMap<>();

        Parser.expect(TokenType.OpenParen, "Expected opening '('");

        while (Parser.at().kind != TokenType.CloseParen && Parser.notEOF()) {
            if (!args.isEmpty())
                Parser.expect(TokenType.Comma, "Expected ',' after argument");

            Map.Entry<String, Type> arg = parseArg();
            args.put(arg.getKey(), arg.getValue());
        }
        Parser.expect(TokenType.CloseParen, "Expected closing ')'");

        Parser.expect(TokenType.Arrow, "Expected '->'");
        Type returnType = Parser.parseType();

        if (Parser.at().kind == TokenType.Semicolon) {
            Parser.eat();
            return new FuncDeclarationStmt(name, body, args, returnType, false);
        }


        Parser.expect(TokenType.OpenBrace, "Expected opening '{'");
        while ((Parser.at().kind != TokenType.CloseBrace) && Parser.notEOF()) {
            body.add(Parser.parseStmt());
        }
        Parser.expect(TokenType.CloseBrace, "Expected closing '}'");

        return new FuncDeclarationStmt(name, body, args, returnType, true);
    }
}
