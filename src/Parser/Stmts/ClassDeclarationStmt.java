package Parser.Stmts;

import Compiler.Compiler;
import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Elf.ElfHandler;
import Compiler.Types.Class;
import Compiler.Types.Type;
import Lexer.Token;
import Lexer.TokenType;
import Parser.Parser;
import Compiler.Types.Class.Method;

import java.util.*;

public class ClassDeclarationStmt implements Stmt{
    String name;
    LinkedHashMap<String, Type> members;
    LinkedHashMap<String, MethodDefinition> methods;

    @Override
    public void log() {
        System.out.println("Class Declaration Statement:");
        System.out.printf("\tName: %s\n", name);
        System.out.println("Members:");
        for (Map.Entry<String, Type> member : members.entrySet()) {
            System.out.printf("\t%s:\n", member.getKey());
            System.out.printf("\t\tType: %s\n", member.getValue());
        }
    }

    static final List<Register.x32> ARG_REGISTER_LIST = new ArrayList<>(List.of(Register.x32.EAX, Register.x32.EBX, Register.x32.ECX, Register.x32.EDX, Register.x32.ESI, Register.x32.EDI));

    @Override
    public void codegen() {
        for (String methodName: methods.keySet()) {
            MethodDefinition methodDefinition = methods.get(methodName);
            Compiler.startScope();

            if (methodDefinition.method.args.size() > ARG_REGISTER_LIST.size()) {
                System.err.printf("Function with too many arguments. Maximum of %d\n", ARG_REGISTER_LIST.size() - 1);
                System.exit(-1);
            }

            ElfHandler.Text.addLabel(methodDefinition.method.symbol, 1);
            Assembler.push(Register.x32.EBP);
            Assembler.mov(new RegisterMemory32(Register.x32.EBP), Register.x32.ESP);

            for (int i = 0; i < methodDefinition.method.args.size(); i++) {
                String argName = methodDefinition.method.args.get(i).name;
                Type argType = methodDefinition.method.args.get(i).type;
                int typeSize = argType.getSize();
                Compiler.stackPtr += typeSize;
                Assembler.mov(new RegisterMemory(null, Register.x32.EBP, -Compiler.stackPtr), typeSize, ARG_REGISTER_LIST.get(i).ordinal(), typeSize);
                Compiler.scope.declareVar(argName, argType, Compiler.stackPtr);
            }

            Assembler.sub(new RegisterMemory32(Register.x32.ESP), 0);
            int targetOffset = Assembler.getData().size() - 4;
            for (Stmt stmt : methodDefinition.body) {
                stmt.codegen();
            }
            Assembler.setDataAt(targetOffset, Compiler.stackPtr);

            Assembler.leave();
            Assembler.ret();

            Compiler.endScope();

            Compiler.stackPtr = 0;
        }
    }

    private ClassDeclarationStmt(String name, LinkedHashMap<String, Type> members, LinkedHashMap<String, MethodDefinition> methods) {
        this.name = name;
        this.members = members;
        this.methods = methods;
    }

    private static Map.Entry<String, Type> parseMember() {
        Type type = Parser.parseType();
        Token memberNameToken = Parser.expect(TokenType.Identifier, "Expected member name after type in class declaration");
        type = Parser.parseArrayType(type);
        Parser.expect(TokenType.Semicolon, "Expected \";\" after member in class declaration");
        return new AbstractMap.SimpleEntry<>(memberNameToken.value, type);
    }

    private static Map.Entry<String, Type> parseMethodArg() {
        Type type = Parser.parseType();
        Token nameToken = Parser.expect(TokenType.Identifier, "Expected name identifier");
        return new AbstractMap.SimpleEntry<>(nameToken.value, type);
    }
    
    private static Map.Entry<String, MethodDefinition> parseMethod(String className) {
        Parser.eat();

        String methodName = Parser.expect(TokenType.Identifier, "Expected identifier after \"method\"").value;
        List<Stmt> body = new ArrayList<>();
        Map<String, Type> args = new LinkedHashMap<>();

        Parser.expect(TokenType.OpenParen, "Expected opening \"(\"");

        while (Parser.at().kind != TokenType.CloseParen && Parser.notEOF()) {
            if (!args.isEmpty())
                Parser.expect(TokenType.Comma, "Expected \",\" after argument");

            Map.Entry<String, Type> arg = parseMethodArg();
            args.put(arg.getKey(), arg.getValue());
        }
        Parser.expect(TokenType.CloseParen, "Expected closing \")\"");

        Parser.expect(TokenType.Arrow, "Expected \"->\"");
        Type returnType = Parser.parseType();

        Parser.expect(TokenType.OpenBrace, "Expected opening '{'");
        while ((Parser.at().kind != TokenType.CloseBrace) && Parser.notEOF()) {
            body.add(Parser.parseStmt());
        }
        Parser.expect(TokenType.CloseBrace, "Expected closing '}'");

        List<Class.Method.Arg> argList = new ArrayList<>();
        for (String key : args.keySet()) {
            argList.add(new Class.Method.Arg(key, args.get(key)));
        }

        return new AbstractMap.SimpleEntry<>(methodName, new MethodDefinition( body, new Method(methodName, returnType, argList, String.format("%s:%s@Dracopp", methodName, className))));
    }

    public static ClassDeclarationStmt parse() {
        String name;
        Class parentClass = null;
        LinkedHashMap<String, Type> members = new LinkedHashMap<>();

        LinkedHashMap<String, Class.Method> methods = new LinkedHashMap<>();
        LinkedHashMap<String, MethodDefinition> methodDefinitions = new LinkedHashMap<>();

        Parser.eat();
        Token nameToken = Parser.expect(TokenType.Identifier, "Expected class name after class keyword");
        name = nameToken.value;

        // Check for inheritance
        if (Parser.at().kind == TokenType.Extends) {
            Parser.eat();
            Token parentNameToken = Parser.expect(TokenType.Identifier, "Expected identifier after \"extends\" keyword");
            parentClass = Class.resolveClass(parentNameToken.value);
        }

        Class aClass = Class.declareClass(name, parentClass);

        Parser.expect(TokenType.OpenBrace, "Expected \"{\" after class initialization");

        while (Parser.notEOF() && Parser.at().kind != TokenType.CloseBrace) {
            if (Parser.at().kind == TokenType.Method) {
                Map.Entry<String, MethodDefinition> method = parseMethod(name);

                // Add data to correspondent maps
                methods.put(method.getKey(), method.getValue().method);
                methodDefinitions.put(method.getKey(), method.getValue());
            }
            else {
                Map.Entry<String, Type> member = parseMember();
                members.put(member.getKey(), member.getValue());
            }
        }
        Parser.expect(TokenType.CloseBrace, "Missing closing \"}\" in class declaration");
        Parser.expect(TokenType.Semicolon, "Expected \";\" after class declaration");

        aClass.setData(members, methods);

        return new ClassDeclarationStmt(name, members, methodDefinitions);
    }

    private static class MethodDefinition {
        List<Stmt> body;
        Method method;

        MethodDefinition(List<Stmt> body, Method method) {
            this.body = body;
            this.method = method;
        }
    }
}
