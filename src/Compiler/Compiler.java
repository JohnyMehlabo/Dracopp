package Compiler;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Elf.Elf;
import Compiler.Elf.Section;
import Compiler.Elf.StringTableSection;
import Compiler.Elf.SymbolTableSection;
import Parser.Stmts.Stmt;

import java.io.IOException;

public class Compiler {
    public static StringTableSection stringTableSection;
    public static SymbolTableSection symbolTableSection;
    public static StringTableSection symbolStringTableSection;

    private static Section addSection(String name, int type, int flags, int address, int link, int info, int addressAlign, int entrySize) {
        int idx;
        try {
            idx = Compiler.stringTableSection.addString(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Section section = new Section(idx, type, flags, address, link, info, addressAlign, entrySize);
        Elf.addSection(section);
        return section;
    }
    private static Section addSection(int nameIdx, int type, int flags, int address, int link, int info, int addressAlign, int entrySize) {
        Section section = new Section(nameIdx, type, flags, address, link, info, addressAlign, entrySize);
        Elf.addSection(section);
        return section;
    }
    private static void initStringTableSection() throws IOException {
        stringTableSection = new StringTableSection(1, 0, 0);
        stringTableSection.addString(".shstrtab");
        Elf.addSection(stringTableSection);
        Elf.setStringSectionHeaderIndex(stringTableSection.getIndex());
    }
    private static void initSymbolTableSection() throws IOException {
        int stringTableIndex = stringTableSection.addString(".strtab");
        symbolStringTableSection = new StringTableSection(stringTableIndex, 0, 0);
        Elf.addSection(symbolStringTableSection);

        int symbolTableIndex = stringTableSection.addString(".symtab");
        symbolTableSection = new SymbolTableSection(symbolTableIndex, 0, 0, symbolStringTableSection.getIndex());
        Elf.addSection(symbolTableSection);
    }


    public static void compile(Stmt AST) throws IOException {
        addSection(0, 0, 0, 0, 0, 0, 0, 0);
        initStringTableSection();
        initSymbolTableSection();

        Section textSection = addSection(".text", 1,  2 | 4, 0, 0, 0, 16, 0);

        AST.codegen();

        Assembler.mov(Register.x32.EBX, Register.x32.EAX);
        Assembler.mov(Register.x32.EAX, 0x1);
        Assembler.int_((byte)0x80);

        textSection.setData(Assembler.getData().toByteArray());
        symbolTableSection.addSymbol(0, 0, 0, (byte) 0,  (byte) 0, (short) 0);
        symbolTableSection.addSymbol("_start", 0, 0, (byte) 16,  (byte) 0, textSection.getIndex());

        Elf.save();
    }


}
