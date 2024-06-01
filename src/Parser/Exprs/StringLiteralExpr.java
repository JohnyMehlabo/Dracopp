package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Elf.ElfHandler;
import Compiler.Types.BasicType;
import Compiler.Types.PointerType;
import Compiler.Types.Type;

import java.io.IOException;

public class StringLiteralExpr implements Expr {
    final String string;

    public StringLiteralExpr(String string) {
        this.string = string;
    }

    @Override
    public void log() {
        System.out.printf("Integer Literal Expression:\n\tValue: %s\n", string);
    }

    @Override
    public Type codegen() {
        String name = ElfHandler.Data.addString(string);
        Assembler.lea(Register.x32.EAX, 0x0);
        try {
            ElfHandler.Text.relocationSection.addRelocation(name, Assembler.getData().size()-4, (byte) 1);
        }
        catch (IOException ignored) {}
        return new PointerType(BasicType.Char);
    }
}
