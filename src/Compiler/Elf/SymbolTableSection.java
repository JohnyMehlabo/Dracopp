package Compiler.Elf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SymbolTableSection extends Section {

    private static class Symbol {
        int nameIndex;
        int value;
        int size;
        byte info;
        byte other;
        short sectionHeaderIndex;

        Symbol(int nameIndex, int value, int size, byte info, byte other, short sectionHeaderIndex) {
            this.nameIndex = nameIndex;
            this.value = value;
            this.size = size;
            this.info = info;
            this.other = other;
            this.sectionHeaderIndex = sectionHeaderIndex;
        }
    }

    StringTableSection stringTableSection;
    List<Symbol> localSymbols;
    List<Symbol> globalSymbols;

    public SymbolTableSection(int nameIdx, int flags, int address, int stringTableSectionIndex) {
        super(nameIdx, 2, flags, address, stringTableSectionIndex, 1, 4, 0x10);

        localSymbols = new ArrayList<>();
        globalSymbols = new ArrayList<>();

        stringTableSection = (StringTableSection) Elf.getSection(stringTableSectionIndex);
    }

    public void addSymbol(String name, int value, int size, byte info, byte other, short sectionHeaderIndex) throws IOException {
        int idx = stringTableSection.addString(name);

        if ((info >> 4) == 0) {
            localSymbols.add(new Symbol(idx, value, size, info, other, sectionHeaderIndex));
            this.info = localSymbols.size();
        } else if ((info >> 4) == 1) {
            globalSymbols.add(new Symbol(idx, value, size, info, other, sectionHeaderIndex));
        } else {
            System.err.println("Invalid symbol info");
            System.exit(-1);
        }

        this.size = 16 * localSymbols.size() + 16 * globalSymbols.size();
    }
    public void addSymbol(int nameIndex, int value, int size, byte info, byte other, short sectionHeaderIndex) throws IOException {

        if ((info >> 4) == 0) {
            localSymbols.add(new Symbol(nameIndex, value, size, info, other, sectionHeaderIndex));
            this.info = localSymbols.size();
        } else if ((info >> 4) == 1) {
            globalSymbols.add(new Symbol(nameIndex, value, size, info, other, sectionHeaderIndex));
        } else {
            System.err.println("Invalid symbol info");
            System.exit(-1);
        }
        this.size = 16 * localSymbols.size() + 16 * globalSymbols.size();
    }
    @Override
    public byte[] getData() {
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        try {
            for (Symbol s : localSymbols) {
                data.write(littleEndian(s.nameIndex));
                data.write(littleEndian(s.value));
                data.write(littleEndian(s.size));
                data.write(s.info);
                data.write(s.other);
                data.write(littleEndian(s.sectionHeaderIndex));
            }
            for (Symbol s : globalSymbols) {
                data.write(littleEndian(s.nameIndex));
                data.write(littleEndian(s.value));
                data.write(littleEndian(s.size));
                data.write(s.info);
                data.write(s.other);
                data.write(littleEndian(s.sectionHeaderIndex));
            }
        } catch (IOException e) {
            System.err.println("Error");
            System.exit(-1);
        }

        return data.toByteArray();
    }

    private static byte[] littleEndian(int imm32) {
        return new byte[] {
                (byte) (imm32 & 0b11111111),
                (byte) ((imm32 & (0b11111111 << 8)) >> 8),
                (byte) ((imm32 & (0b11111111 << 16)) >> 16),
                (byte) ((imm32 & (0b11111111 << 24)) >> 24)
        };
    }
    private static byte[] littleEndian(short imm16) {
        return new byte[] {
                (byte) (imm16 & 0b11111111),
                (byte) ((imm16 & (0b11111111 << 8)) >> 8),
        };
    }
}
