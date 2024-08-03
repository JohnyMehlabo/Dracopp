package Compiler.Assembler;

import Compiler.Elf.ElfHandler;
import Compiler.Elf.Section;
import Compiler.Elf.SymbolTableSection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assembler {
    private static ByteArrayOutputStream data;
    private static Section section;

    private static final Map<Integer, String> labelRelocations = new HashMap<>();
    private static final Map<String, Integer> localLabels = new HashMap<>();

    // Immediate
    public static void mov(Register.x32 reg, int imm32) {
        data.write((byte) (0xb8 | reg.ordinal()));
        littleEndian(imm32);
    }

    // r/m <-- r
    public static void mov(RegisterMemory dst, int dstSize, int srcOrdinal, int srcSize) {
        if (dstSize == srcSize) {
            switch (dstSize) {
                case 4:
                    mov(new RegisterMemory32(dst), Register.x32.values()[srcOrdinal]);
                    break;
                case 2:
                    mov(new RegisterMemory16(dst), Register.x16.values()[srcOrdinal]);
                    break;
                case 1:
                    mov(new RegisterMemory8(dst), Register.x8.values()[srcOrdinal]);
                    break;
            }
        } else {
            System.err.println("Sizes don't match");
            System.exit(-1);
        }

    }
    public static void mov(RegisterMemory32 dst , Register.x32 src) {
        data.write(0x89);
        generateAddressingBytes32(dst, src.ordinal());
    }
    public static void mov(RegisterMemory16 dst , Register.x16 src) {
        data.write(0x66);
        data.write(0x89);
        generateAddressingBytes32(dst, src.ordinal());
    }
    public static void mov(RegisterMemory8 dst , Register.x8 src) {
        data.write(0x88);
        generateAddressingBytes32(dst, src.ordinal());
    }

    // r <-- r/m
    public static void mov(int dstOrdinal, int dstSize, RegisterMemory src, int srcSize) {
        if (dstSize == srcSize) {
            switch (dstSize) {
                case 4:
                    Assembler.mov(Register.x32.values()[dstOrdinal], new RegisterMemory32(src));
                    break;
                case 2:
                    Assembler.mov(Register.x16.values()[dstOrdinal], new RegisterMemory16(src));
                    break;
                case 1:
                    Assembler.mov(Register.x8.values()[dstOrdinal], new RegisterMemory8(src));
                    break;
            }
        } else {
            System.err.println("Sizes don't match");
            System.exit(-1);
        }
    }

    public static void lea(Register.x32 dst, RegisterMemory32 memory) {
        checkOnlyMemory(memory);
        data.write(0x8d);
        generateAddressingBytes32(memory, dst.ordinal());
    }

    public static void lea(Register.x32 dst, int memory) {
        data.write(0x8d);
        data.write(dst.ordinal() << 3 | 0x05);
        littleEndian(memory);
    }

    public static void mov(Register.x32 dst, RegisterMemory32 src) {
        data.write(0x8b);
        generateAddressingBytes32(src, dst.ordinal());
    }
    public static void mov(Register.x16 dst, RegisterMemory16 src) {
        data.write(0x66);
        data.write(0x8b);
        generateAddressingBytes32(src, dst.ordinal());
    }
    public static void mov(Register.x8 dst, RegisterMemory8 src) {
        data.write(0x8a);
        generateAddressingBytes32(src, dst.ordinal());
    }

    public static void movzx(Register dst, int dstSize, RegisterMemory src, int srcSize) {
        if (dstSize == 4 && srcSize == 2) movzx((Register.x32) dst, new RegisterMemory16(src));
        if (dstSize == 4 && srcSize == 1) movzx((Register.x32) dst, new RegisterMemory8(src));
        if (dstSize == 2 && srcSize == 1) movzx((Register.x16) dst, new RegisterMemory8(src));

    }
    public static void movzx(Register.x16 dst, RegisterMemory8 src) {
        data.write(0x66);
        data.write(0x0f);
        data.write(0xb6);
        generateAddressingBytes32(src, dst.ordinal());
    }
    public static void movzx(Register.x32 dst, RegisterMemory8 src) {
        data.write(0x0f);
        data.write(0xb6);
        generateAddressingBytes32(src, dst.ordinal());
    }
    public static void movzx(Register.x32 dst, RegisterMemory16 src) {
        data.write(0x0f);
        data.write(0xb6);
        generateAddressingBytes32(src, dst.ordinal());
    }

    public static void cmp(RegisterMemory32 regM , Register.x32 reg) {
        data.write(0x39);
        generateAddressingBytes32(regM, reg.ordinal());
    }

    public static void setg(RegisterMemory8 regM) {
        data.write(0x0f);
        data.write(0x9f);
        generateAddressingBytes32(regM, 0);
    }
    public static void setge(RegisterMemory8 regM) {
        data.write(0x0f);
        data.write(0x9d);
        generateAddressingBytes32(regM, 0);
    }
    public static void setl(RegisterMemory8 regM) {
        data.write(0x0f);
        data.write(0x9c);
        generateAddressingBytes32(regM, 0);
    }
    public static void setle(RegisterMemory8 regM) {
        data.write(0x0f);
        data.write(0x9e);
        generateAddressingBytes32(regM, 0);
    }
    public static void setb(RegisterMemory8 regM) {
        data.write(0x0f);
        data.write(0x92);
        generateAddressingBytes32(regM, 0);
    }
    public static void setbe(RegisterMemory8 regM) {
        data.write(0x0f);
        data.write(0x96);
        generateAddressingBytes32(regM, 0);
    }
    public static void seta(RegisterMemory8 regM) {
        data.write(0x0f);
        data.write(0x97);
        generateAddressingBytes32(regM, 0);
    }
    public static void setae(RegisterMemory8 regM) {
        data.write(0x0f);
        data.write(0x93);
        generateAddressingBytes32(regM, 0);
    }


    public static void test(RegisterMemory regM, int regMSize, int registerOrdinal, int registerSize) {
        if (regMSize == registerSize) {
            switch (regMSize) {
                case 4:
                    test(new RegisterMemory32(regM), Register.x32.values()[registerOrdinal]);
                    break;
                case 2:
                    test(new RegisterMemory16(regM), Register.x16.values()[registerOrdinal]);
                    break;
                case 1:
                    test(new RegisterMemory8(regM), Register.x8.values()[registerOrdinal]);
                    break;
            }
        } else {
            System.err.println("Sizes don't match");
            System.exit(-1);
        }
    }
    public static void test(RegisterMemory32 reg , Register.x32 other) {
        data.write(0x85);
        generateAddressingBytes32(reg, other.ordinal());
    }
    public static void test(RegisterMemory16 reg , Register.x16 other) {
        data.write(0x85);
        generateAddressingBytes32(reg, other.ordinal());
    }
    public static void test(RegisterMemory8 reg , Register.x8 other) {
        data.write(0x84);
        generateAddressingBytes32(reg, other.ordinal());
    }

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

    public static void add(RegisterMemory32 dst, Register.x32 src) {
        data.write((byte)0x01);
        generateAddressingBytes32(dst, src.ordinal());
    }
    public static void add(RegisterMemory32 dst, int imm32) {
        data.write((byte)0x81);
        generateAddressingBytes32(dst, 0);
        littleEndian(imm32);
    }

    public static void sub(RegisterMemory32 dst, Register.x32 src) {
        data.write((byte)0x29);
        generateAddressingBytes32(dst, src.ordinal());
    }
    public static void sub(RegisterMemory32 dst, int imm32) {
        data.write((byte)0x81);
        generateAddressingBytes32(dst, 5);
        littleEndian(imm32);
    }

    public static void int_(byte imm8) {
        data.write((byte)0xcd);
        data.write(imm8);
    }

    // TODO
    public static void shl(RegisterMemory32 regM, byte imm8) {
        data.write(0xc1);
        generateAddressingBytes32(regM, 4);
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

    // Float operations (FPU)
    public static void fld(RegisterMemory32 floatMemory) {
        checkOnlyMemory(floatMemory);
        data.write(0xd9);
        generateAddressingBytes32(floatMemory, 0);
    }

    public static void fild(RegisterMemory32 intMemory) {
        checkOnlyMemory(intMemory);
        data.write(0xdb);
        generateAddressingBytes32(intMemory, 0);
    }

    public static void fadd(RegisterMemory32 floatMemory) {
        checkOnlyMemory(floatMemory);
        data.write(0xd8);
        generateAddressingBytes32(floatMemory, 0);
    }
    public static void fiadd(RegisterMemory32 intMemory) {
        checkOnlyMemory(intMemory);
        data.write(0xda);
        generateAddressingBytes32(intMemory, 0);
    }

    public static void fsub(RegisterMemory32 floatMemory) {
        checkOnlyMemory(floatMemory);
        data.write(0xd8);
        generateAddressingBytes32(floatMemory, 4);
    }
    public static void fisub(RegisterMemory32 intMemory) {
        checkOnlyMemory(intMemory);
        data.write(0xda);
        generateAddressingBytes32(intMemory, 4);
    }

    public static void fmul(RegisterMemory32 floatMemory) {
        checkOnlyMemory(floatMemory);
        data.write(0xd8);
        generateAddressingBytes32(floatMemory, 1);
    }
    public static void fimul(RegisterMemory32 intMemory) {
        checkOnlyMemory(intMemory);
        data.write(0xda);
        generateAddressingBytes32(intMemory, 1);
    }

    public static void fdiv(RegisterMemory32 floatMemory) {
        checkOnlyMemory(floatMemory);
        data.write(0xd8);
        generateAddressingBytes32(floatMemory, 6);
    }
    public static void fidiv(RegisterMemory32 intMemory) {
        checkOnlyMemory(intMemory);
        data.write(0xda);
        generateAddressingBytes32(intMemory, 6);
    }

    public static void fcomip(int st0, int sti) {
        if (st0 != 0) {
            System.err.println("St0 arg mus be 0");
            System.exit(-1);
        }
        data.write(0xdf);
        data.write(0xf0+sti);
    }

    public static void fstp(RegisterMemory32 floatMemory) {
        checkOnlyMemory(floatMemory);
        data.write(0xd9);
        generateAddressingBytes32(floatMemory, 3);
    }

    public static void fstp(int sti) {
        data.write(0xdd);
        data.write(0xd8 + sti);
    }

    public static void cvttss2si(Register.x32 register, RegisterMemory32 regM) {
        checkOnlyMemory(regM);
        data.write(0xf3);
        data.write(0x0f);
        data.write(0x2c);
        generateAddressingBytes32(regM, register.ordinal());
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
                data.write(0b10000000 | regM.addressReg.ordinal() | (regBits << 3));
                littleEndian(regM.displacement);
            }
            else {
                data.write(regM.addressReg.ordinal() | (regBits << 3));
                if (regM.addressReg ==  Register.x32.ESP) data.write(0x24);
            }
        }
        else {
            data.write(0b11000000 | regM.reg.ordinal() | (regBits << 3));
        }
    }
    private static void generateAddressingBytes32(RegisterMemory16 regM, int regBits) {
        if (regM.readAddress) {
            if (regM.hasDisplacement ||  (regM.addressReg == Register.x32.EBP)) {
                data.write(0b010000000 | regM.addressReg.ordinal() | (regBits << 3));
                littleEndian(regM.displacement);
            }
            else {
                data.write(regM.addressReg.ordinal() | (regBits << 3));
            }
        }
        else {
            data.write(0b11000000 | regM.reg.ordinal() | (regBits << 3));
        }
    }
    private static void generateAddressingBytes32(RegisterMemory8 regM, int regBits) {
        if (regM.readAddress) {
            if (regM.hasDisplacement ||  (regM.addressReg == Register.x32.EBP)) {
                data.write(0b10000000 | regM.addressReg.ordinal() | (regBits << 3));
                littleEndian(regM.displacement);
            }
            else {
                data.write(regM.addressReg.ordinal() | (regBits << 3));
            }
        }
        else {
            data.write(0b11000000 | regM.reg.ordinal() | (regBits << 3));
        }
    }

    private static void checkOnlyMemory(RegisterMemory32 memory) {
        if (memory.reg != null) {
            System.err.println("Cannot use register. Only memory allowed");
            System.exit(-1);
        }
    }
    private static void checkOnlyMemory(RegisterMemory16 memory) {
        if (memory.reg != null) {
            System.err.println("Cannot use register. Only memory allowed");
            System.exit(-1);
        }
    }
    private static void checkOnlyMemory(RegisterMemory8 memory) {
        if (memory.reg != null) {
            System.err.println("Cannot use register. Only memory allowed");
            System.exit(-1);
        }
    }

    public static void computeRelocations() throws IOException {
        byte[] dataAsArray = data.toByteArray();

        for (Integer offset : labelRelocations.keySet()) {
            String name = labelRelocations.get(offset);

            boolean targetFound = false;
            int targetOffset = 0;
            if (localLabels.get(name) != null) {
                targetOffset = localLabels.get(name);
                targetFound = true;
            }
            else {
                SymbolTableSection.Symbol sym = ElfHandler.symbolTableSection.getSymbolByName(name);
                if (sym == null || sym.getSectionHeaderIndex() == 0) {
                    ElfHandler.Text.relocationSection.addRelocation(labelRelocations.get(offset), offset, (byte) 2);
                }
                else {
                    targetFound = true;
                    targetOffset = sym.getValue();
                }
            }
            if (targetFound) {
                int rel = targetOffset - offset - 4;
                dataAsArray[offset] = (byte) (rel & 0b11111111);
                dataAsArray[offset+1] = (byte) ((rel & (0b11111111 << 8)) >> 8);
                dataAsArray[offset+2] = (byte) ((rel & (0b11111111 << 16)) >> 16);
                dataAsArray[offset+3] = (byte) ((rel & (0b11111111 << 24)) >> 24);

                data.reset();
                data.write(dataAsArray);
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

    public static void addLocalLabel(String label) {
        localLabels.put(label, data.size());
    }

    public static void setDataAt(int offset, int int32) {
        byte[] dataAsArray = data.toByteArray();

        dataAsArray[offset] = (byte) (int32 & 0b11111111);
        dataAsArray[offset+1] = (byte) ((int32 & (0b11111111 << 8)) >> 8);
        dataAsArray[offset+2] = (byte) ((int32 & (0b11111111 << 16)) >> 16);
        dataAsArray[offset+3] = (byte) ((int32 & (0b11111111 << 24)) >> 24);

        try {
            data.reset();
            data.write(dataAsArray);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }
}
