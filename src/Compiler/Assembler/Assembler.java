package Compiler.Assembler;

import java.io.ByteArrayOutputStream;

public class Assembler {
    private static final ByteArrayOutputStream data = new ByteArrayOutputStream();

    public static void mov(Register.x32 reg , int imm32) {
        data.write((byte) (0xb8 | reg.ordinal()));
        littleEndian(imm32);
    }

    public static void mov(Register.x32 dst , Register.x32 src) {
        data.write(0x89);
        data.write(0b11000000 | src.ordinal() << 3 | dst.ordinal());
    }

    // Remember that this is a near ret.
    public static void ret() {
        data.write(0xc3);
    }

    public static void push(Register.x32 reg) {
        data.write((byte)0x50 + reg.ordinal());
    }

    public static void pop(Register.x32 reg) {
        data.write((byte)0x58 + reg.ordinal());
    }

    public static void add(Register.x32 dst, Register.x32 src) {
        data.write((byte)0x01);
        data.write(0b11000000 | src.ordinal() << 3 | dst.ordinal());
    }

    public static void int_(byte imm8) {
        data.write((byte)0xcd);
        data.write(imm8);
    }

    private static void littleEndian(int imm32) {
        data.write((byte) (imm32 & 0b11111111));
        data.write((byte) ((imm32 & (0b11111111 << 8)) >> 8));
        data.write((byte) ((imm32 & (0b11111111 << 16)) >> 16));
        data.write((byte) ((imm32 & (0b11111111 << 24)) >> 24));
    }

    public static ByteArrayOutputStream getData() {
        return data;
    }
}
