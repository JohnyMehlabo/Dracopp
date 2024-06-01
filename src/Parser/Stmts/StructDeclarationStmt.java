package Parser.Stmts;

import Compiler.Types.Struct;
import Compiler.Types.Type;
import Lexer.Token;
import Lexer.TokenType;
import Parser.Parser;

import java.util.LinkedHashMap;
import java.util.Map;

public class StructDeclarationStmt implements Stmt{
    String name;
    LinkedHashMap<String, Type> members;

    @Override
    public void log() {
        System.out.println("Struct Declaration Statement:");
        System.out.printf("\tName: %s\n", name);
        System.out.println("Members:");
        for (Map.Entry<String, Type> member : members.entrySet()) {
            System.out.printf("\t%s:\n", member.getKey());
            System.out.printf("\t\tType: %s\n", member.getValue());
        }
    }

    @Override
    public void codegen() {    }

    private StructDeclarationStmt(String name, LinkedHashMap<String, Type> members) {
        this.name = name;
        this.members = members;
    }

    public static StructDeclarationStmt parse() {
        String name;
        LinkedHashMap<String, Type> members = new LinkedHashMap<>();

        Parser.eat();
        Token nameToken = Parser.expect(TokenType.Identifier, "Expected struct name after struct keyword");
        name = nameToken.value;
        Parser.expect(TokenType.OpenBrace, "Expected \"{\" after struct name");

        while (Parser.notEOF() && Parser.at().kind != TokenType.CloseBrace) {
            Type type = Parser.parseType();
            Token memberNameToken = Parser.expect(TokenType.Identifier, "Expected member name after type in struct declaration");
            Parser.expect(TokenType.Semicolon, "Expected \";\" after member in struct declaration");
            members.put(memberNameToken.value, type);
        }
        Parser.expect(TokenType.CloseBrace, "Missing closing \"}\" in struct declaration");
        Parser.expect(TokenType.Semicolon, "Expected \";\" after struct declaration");

        Struct.declareStruct(name, members);
        return new StructDeclarationStmt(name, members);
    }
}
