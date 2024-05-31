package Compiler.Elf;

import Compiler.Assembler.Assembler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ElfHandler {
    public static StringTableSection stringTableSection;
    public static SymbolTableSection symbolTableSection;
    public static StringTableSection symbolStringTableSection;

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
        symbolTableSection.addSymbol(0, 0, 0, (byte) 0,  (byte) 0, (short) 0);
    }

    private static Section addSection(String name, int type, int flags, int address, int link, int info, int addressAlign, int entrySize) {
        int idx;
        try {
            idx = stringTableSection.addString(name);
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

    public static class Text {
        private static Section textSection;
        private static final ByteArrayOutputStream textSectionData = new ByteArrayOutputStream();
        public static RelocationSection relocationSection;

        private static final Map<String, Label> labels = new HashMap<>();

        public static void addLabel(String name, int global) {
            int value = textSectionData.size();
            short index = textSection.getIndex();

            Label label = new Label(value, index, name);
            labels.put(name, label);

            try {
                symbolTableSection.addSymbol(name, value, 0, (byte) (global << 4), (byte) 0, index);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public static Label getLabel(String name) {
            return labels.get(name);
        }

        public static short getSectionIndex() {
            return textSection.getIndex();
        }

        static void initRelocationSection() throws IOException {
            int nameIndex = stringTableSection.addString(".rel.text");
            Text.relocationSection = new RelocationSection(nameIndex, 0, 0, symbolTableSection, textSection);
            Elf.addSection(Text.relocationSection);
        }
    }

    public static class Data {
        private static Section dataSection;
        private static final ByteArrayOutputStream dataSectionData = new ByteArrayOutputStream();
        private static int stringCount = 0;

        private static final Map<String, Label> labels = new HashMap<>();

        public static void addLabel(String name, int global) {
            int value = dataSectionData.size();
            short index = dataSection.getIndex();

            Label label = new Label(value, index, name);
            labels.put(name, label);

            try {
                symbolTableSection.addSymbol(name, value, 0, (byte) (global << 4), (byte) 0, index);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public static Label getLabel(String name) {
            return labels.get(name);
        }

        public static String addString(String string) {
            String stringLabel = String.format("string_%d", stringCount);
            try {
                addLabel(stringLabel, 1);
                dataSectionData.write(string.getBytes(StandardCharsets.US_ASCII));
            }
            catch (IOException ignored) {
            }
            stringCount++;
            return stringLabel;
        }

        public static short getSectionIndex() {
            return dataSection.getIndex();
        }
    }

    public static void initElfHandler() throws IOException {
        addSection(0, 0, 0, 0, 0, 0, 0, 0);
        initStringTableSection();
        initSymbolTableSection();

        Text.textSection = addSection(".text", 1,  2 | 4, 0, 0, 0, 16, 0);
        Data.dataSection = addSection(".data", 1,  2, 0, 0, 0, 16, 0);
        Text.initRelocationSection();
        Assembler.setData(Text.textSectionData);
        Assembler.setSection(Text.textSection);
    }

    public static void save(Path outputFile) throws IOException {
        Assembler.computeRelocations();
        Text.textSection.setData(Assembler.getData().toByteArray());
        Data.dataSection.setData(Data.dataSectionData.toByteArray());

        Elf.save(outputFile);

    }
}
