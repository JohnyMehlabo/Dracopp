package Compiler;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Elf.*;
import Parser.Stmts.Stmt;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Compiler {

    private static List<Function> functions = new ArrayList<>();

    public static Scope scope = new Scope(null);
    public static int stackPtr = 0;

    public static int ifCount = 0;
    public static int whileCount = 0;

    public static void addFunction(String name) {
        if (functions.stream().anyMatch(f -> f.name.equals(name))) {
            System.err.printf("Redefinition of function '%s'", name);
            System.exit(-1);
        }
        functions.add(new Function(name));
    }
    public static Function resolveFunction(String name) {
        return functions.stream().filter(f -> f.name.equals(name)).findFirst().orElse(null);
    }

    public static void startScope() {
        scope = new Scope(scope);
    }
    public static void endScope() {
        scope = scope.parentScope;
    }

    public static void compile(Stmt AST) throws IOException {
        ElfHandler.initElfHandler();
        AST.codegen();
        ElfHandler.save(Path.of("out.o"));
    }


}
