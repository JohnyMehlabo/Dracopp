package Compiler.Elf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RelocationSection extends Section {
    List<Relocation> relocations = new ArrayList<>();
    SymbolTableSection symbolTableSection;
    Section section;

    public RelocationSection(int nameIdx, int flags, int address, SymbolTableSection symbolTableSection, Section section) {
        super(nameIdx, 9, flags, address, symbolTableSection.getIndex(), section.getIndex(), 4, 8);
        this.section = section;
        this.symbolTableSection = symbolTableSection;
    }

    static class Relocation {
        int offset;
        int info;

        Relocation(int offset, int info) {
            this.offset = offset;
            this.info = info;
        }
    }

    public void addRelocation(String name, int offset, byte type) throws IOException {
        SymbolTableSection.Symbol sym = ElfHandler.symbolTableSection.addSymbol(name, 0, 0, (byte) 16, (byte) 0, (short) 0);
        int symIndex = symbolTableSection.getSymbolIndex(sym);
        relocations.add(new Relocation(offset, (((symIndex)<<8)+ type)));
        size = relocations.size() * 8;
    }

    @Override
    public byte[] getData() {
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        try {
            for (Relocation relocation : relocations) {
                data.write(littleEndian(relocation.offset));
                data.write(littleEndian(relocation.info));
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
