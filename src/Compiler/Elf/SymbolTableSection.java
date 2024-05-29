package Compiler.Elf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SymbolTableSection extends Section {

    public static class Symbol {
        String name;
        int nameIndex;
        int value;
        int size;
        byte info;
        byte other;
        short sectionHeaderIndex;

        Symbol(String name, int nameIndex, int value, int size, byte info, byte other, short sectionHeaderIndex) {
            this.name = name;
            this.nameIndex = nameIndex;
            this.value = value;
            this.size = size;
            this.info = info;
            this.other = other;
            this.sectionHeaderIndex = sectionHeaderIndex;
        }

        public int getValue() {
            return value;
        }
        public int getSectionHeaderIndex() {
            return sectionHeaderIndex;
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

    public int getSymbolIndex(Symbol symbol) {
        int index = localSymbols.indexOf(symbol);
        if (index == -1) {
            index = globalSymbols.indexOf(symbol);
            if (index == -1) {
                System.err.println("Symbol not found");
            }
            index +=  + localSymbols.size();
        }
        return index;
    }

    public Symbol getSymbolByName(String name) {
        Optional<Symbol> sym = localSymbols.stream().filter(s -> s.name.equals(name)).findFirst();
        if (sym.isPresent()) return  sym.get();
        sym = globalSymbols.stream().filter(s -> s.name.equals(name)).findFirst();
        return sym.orElse(null);
    }

    public Symbol addSymbol(String name, int value, int size, byte info, byte other, short sectionHeaderIndex) throws IOException {
        int idx = stringTableSection.addString(name);

        Symbol symbol = new Symbol(name, idx, value, size, info, other, sectionHeaderIndex);
        if ((info >> 4) == 0) {
            localSymbols.add(symbol);
            this.info = localSymbols.size();
        } else if ((info >> 4) == 1) {
            globalSymbols.add(symbol);
        } else {
            System.err.println("Invalid symbol info");
            System.exit(-1);
        }

        this.size = 16 * localSymbols.size() + 16 * globalSymbols.size();
        return symbol;
    }
    public Symbol addSymbol(int nameIndex, int value, int size, byte info, byte other, short sectionHeaderIndex) throws IOException {

        Symbol symbol = new Symbol("", nameIndex, value, size, info, other, sectionHeaderIndex);

        if ((info >> 4) == 0) {
            localSymbols.add(symbol);
            this.info = localSymbols.size();
        } else if ((info >> 4) == 1) {
            globalSymbols.add(symbol);
        } else {
            System.err.println("Invalid symbol info");
            System.exit(-1);
        }
        this.size = 16 * localSymbols.size() + 16 * globalSymbols.size();

        return symbol;
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
