package Compiler.Assembler;

import Compiler.Elf.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assembler {
    private static ByteArrayOutputStream data;
    private static Section section;

    private static final Map<Integer, String> labelRelocations = new HashMap<>();

    public static void mov(Register.x32 reg, int imm32) {
        data.write((byte) (0xb8 | reg.ordinal()));
        littleEndian(imm32);
    }
    public static void mov(RegisterMemory32 dst , Register.x32 src) {
        data.write(0x89);
        generateAddressingBytes32(dst, src.ordinal());
    }
    public static void mov(Register.x32 dst, RegisterMemory32 src) {
        data.write(0x8b);
        generateAddressingBytes32(src, dst.ordinal());
    }

    public static void test(RegisterMemory32 reg , Register.x32 other) {
        data.write(0x85);
        generateAddressingBytes32(reg, other.ordinal());
    }

    // Remember that this is a near ret.
    public static void ret() {
        data.write(0xc3);
    }
    public static void leave() {data.write(0xc9); }

    // TODO
    public static void push(Register.x32 reg) {
        data.write((byte)0x50 + reg.ordinal());
    }
    // TODO
    public static void pop(Register.x32 reg) {
        data.write((byte)0x58 + reg.ordinal());
    }

    // TODO
    public static void add(Register.x32 dst, Register.x32 src) {
        data.write((byte)0x01);
        data.write(0b11000000 | src.ordinal() << 3 | dst.ordinal());
    }

    public static void sub(RegisterMemory32 dst, Register.x32 src) {
        data.write((byte)0x29);
        generateAddressingBytes32(dst, src.ordinal());
    }
    public static void sub(RegisterMemory32 dst, byte imm8) {
        data.write((byte)0x83);
        generateAddressingBytes32(dst, 5);
        data.write(imm8);
    }

    public static void int_(byte imm8) {
        data.write((byte)0xcd);
        data.write(imm8);
    }

    // TODO
    public static void mul(Register.x32 other) {
        data.write(0xf7);
        data.write(0b11000000 | 0b00100000 | other.ordinal());
    }

    // TODO
    public static void div(Register.x32 other) {
        data.write(0xf7);
        data.write(0b11000000 | 0b00110000 | other.ordinal());
    }

    public static void nop() {
        data.write((byte)0x90);
    }

    public static void jmp(String labelName) {
        data.write(0xe9);
        labelRelocations.put(data.size(), labelName);
        writeUndefined32Rel();
    }
    public static void call(String labelName) {
        data.write(0xe8);
        labelRelocations.put(data.size(), labelName);
        writeUndefined32Rel();
    }
    public static void je(String labelName) {
        data.write(0x0f);
        data.write(0x84);
        labelRelocations.put(data.size(), labelName);
        writeUndefined32Rel();
    }
    public static void jz(String labelName) {
        je(labelName);
    }

    private static void writeUndefined32Rel() {
        data.write(0xfc);
        data.write(0xff);
        data.write(0xff);
        data.write(0xff);
    }
    private static void littleEndian(int imm32) {
        data.write((byte) (imm32 & 0b11111111));
        data.write((byte) ((imm32 & (0b11111111 << 8)) >> 8));
        data.write((byte) ((imm32 & (0b11111111 << 16)) >> 16));
        data.write((byte) ((imm32 & (0b11111111 << 24)) >> 24));
    }
    private static void generateAddressingBytes32(RegisterMemory32 regM, int regBits) {
        if (regM.readAddress) {
            if (regM.hasDisplacement ||  (regM.addressReg == Register.x32.EBP)) {
                data.write(0b01000000 | regM.addressReg.ordinal() | (regBits << 3));
                data.write(regM.displacement);
            }
            else {
                data.write(regM.addressReg.ordinal() | (regBits << 3));
            }
        }
        else {
            data.write(0b11000000 | regM.reg.ordinal() | (regBits << 3));
        }
    }

    public static void computeRelocations() throws IOException {
        byte[] dataAsArray = data.toByteArray();

        for (Integer offset : labelRelocations.keySet()) {
            String name = labelRelocations.get(offset);
            SymbolTableSection.Symbol sym = ElfHandler.symbolTableSection.getSymbolByName(name);
            if (sym != null) {
                int rel = sym.getValue() - offset - 4;
                dataAsArray[offset] = (byte) (rel & 0b11111111);
                dataAsArray[offset+1] = (byte) ((rel & (0b11111111 << 8)) >> 8);
                dataAsArray[offset+2] = (byte) ((rel & (0b11111111 << 16)) >> 16);
                dataAsArray[offset+3] = (byte) ((rel & (0b11111111 << 24)) >> 24);

                data.reset();
                data.write(dataAsArray);
            } else {
                ElfHandler.Text.relocationSection.addRelocation(labelRelocations.get(offset), offset, (byte) 2);
            }
        }
    }

    public static ByteArrayOutputStream getData() {
        return data;
    }
    public static void setData(ByteArrayOutputStream data) {
        Assembler.data = data;
    }
    public static Section getSection() {
        return section;
    }
    public static void setSection(Section section) {
        Assembler.section = section;
    }
}
