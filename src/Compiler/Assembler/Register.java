package Compiler.Assembler;

public interface Register {
    enum x64 implements Register {
        RAX, RCX,
        RDX, RBX,
        RSP, RBP,
        RSI, RDI
    }
    enum x32 implements Register {
        EAX, ECX,
        EDX, EBX,
        ESP, EBP,
        ESI, EDI
    }
    enum x16 implements Register {
        AX, CX,
        DX, BX,
        SP, BP,
        SI, DI
    }
    enum x8 implements Register {
        AL, CL,
        DL, BL,
        AH, CH,
        DH, BH
    }
    int ordinal();

    static Register fromSize(int ordinal, int size) {
        return switch (size) {
            case 8 -> x64.values()[ordinal];
            case 4 -> x32.values()[ordinal];
            case 2 -> x16.values()[ordinal];
            case 1 -> x8.values()[ordinal];
            default -> null;
        };
    }
}
