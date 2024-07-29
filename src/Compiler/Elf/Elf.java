package Compiler.Elf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Elf {
    private static final short  ELF_HEADER_SIZE = 0x34;
    private static final short  SECTION_HEADER_SIZE = 0x28;
    private static final short  PROGRAM_HEADER_SIZE = 0x0;

    private static short        type = 0x1;
    private static short        machine = 0x3;
    private static int          version = 0x1;
    private static int          entry = 0x0;
    private static final int    programHeaderOffset = 0x0;
    private static int          sectionHeaderOffset = 0x40;
    private static int          flags = 0x0;
    private static final short  programHeaderNum = 0x0;
    private static short        sectionHeaderNum = 0x0;
    private static short        stringSectionHeaderIndex = 0x1;

    private static final List<Section> sections = new ArrayList<>();

    public static void setType(short type) {
        Elf.type = type;
    }
    public static void setMachine(short machine) {
        Elf.machine = machine;
    }
    public static void setVersion(int version) {
        Elf.version = version;
    }
    public static void setEntry(int entry) {
        Elf.entry = entry;
    }
    public static void setFlags(int flags) {
        Elf.flags = flags;
    }
    public static void setStringSectionHeaderIndex(short stringSectionHeaderIndex) {
        Elf.stringSectionHeaderIndex = stringSectionHeaderIndex;
    }
    public static void setSectionHeaderOffset(short sectionHeaderOffset) {
        Elf.sectionHeaderOffset = sectionHeaderOffset;
    }

    public static short getStringSectionHeaderIndex() {
        return stringSectionHeaderIndex;
    }
    public static Section getSection(int idx) {
        return sections.get(idx);
    }

    public static void addSection(Section section) {
        sectionHeaderNum++;
        sections.add(section);
        section.index = (short) (sections.size() - 1);
    }

    public static void save(Path path) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        // ELF Header
        writeHeader(data);

        int paddingSize = sectionHeaderOffset - data.size();
        addPadding(paddingSize, data);

        int size = data.size() + sectionHeaderNum * SECTION_HEADER_SIZE;
        for (Section section : sections) {
            int sectionPaddingSize = 16 - (size % 16);
            size += sectionPaddingSize;

            section.offset = size;
            writeSectionHeader(section, data);
            size += section.size;
        }

        for (Section section : sections) {
            addPadding(section.offset - data.size(), data);
            data.write(section.getData());
        }

        Files.write(path, data.toByteArray());
    }

    private static void addPadding(int paddingSize, ByteArrayOutputStream data) {
        for (int i = 0; i < paddingSize; i++) {
            data.write((byte) 0);
        }
    }
    private static void writeSectionHeader(Section section, ByteArrayOutputStream data) throws IOException {
        data.write(littleEndian(section.nameIndex));
        data.write(littleEndian(section.type));
        data.write(littleEndian(section.flags));
        data.write(littleEndian(section.address));
        data.write(littleEndian(section.offset));
        data.write(littleEndian(section.size));
        data.write(littleEndian(section.link));
        data.write(littleEndian(section.info));
        data.write(littleEndian(section.addressAlign));
        data.write(littleEndian(section.entrySize));
    }
    private static void writeHeader(ByteArrayOutputStream data) throws IOException {
        data.write(new byte[]{0x7f, 'E', 'L', 'F', 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        data.write(littleEndian(type));
        data.write(littleEndian(machine));
        data.write(littleEndian(version));
        data.write(littleEndian(entry));
        data.write(littleEndian(programHeaderOffset));
        data.write(littleEndian(sectionHeaderOffset));
        data.write(littleEndian(flags));
        data.write(littleEndian(ELF_HEADER_SIZE));
        data.write(littleEndian(PROGRAM_HEADER_SIZE));
        data.write(littleEndian(programHeaderNum));
        data.write(littleEndian(SECTION_HEADER_SIZE));
        data.write(littleEndian(sectionHeaderNum));
        data.write(littleEndian(stringSectionHeaderIndex));
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
