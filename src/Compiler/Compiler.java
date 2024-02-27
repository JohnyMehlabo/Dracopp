package Compiler;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Elf.*;
import Parser.Stmts.Stmt;

import java.io.IOException;
import java.nio.file.Path;

public class Compiler {

    public static Scope scope = new Scope(null);
    public static int stackPtr = 0;
    public static int ifCount = 0;

    public static void startScope() {
        scope = new Scope(scope);
    }

    public static void endScope() {
        scope = scope.parentScope;
    }

    public static void compile(Stmt AST) throws IOException {
        ElfHandler.initElfHandler();

        Assembler.mov(new RegisterMemory32(Register.x32.EBP), Register.x32.ESP);
        AST.codegen();
        // End code
        Assembler.mov(new RegisterMemory32(Register.x32.EBX), Register.x32.EAX);
        Assembler.mov(Register.x32.EAX, 0x1);
        Assembler.int_((byte)0x80);

        ElfHandler.save(Path.of("out.o"));
    }


}
