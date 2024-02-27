package Compiler.Assembler;

public enum Register {
    ;
    public enum x64 {
        RAX, RCX,
        RDX, RBX,
        RSP, RBP,
        RSI, RDI
    }
    public enum x32 {
        EAX, ECX,
        EDX, EBX,
        ESP, EBP,
        ESI, EDI
    }
    public enum x16 {
        AX, CX,
        DX, BX,
        SP, BP,
        SI, DI
    }
    public enum x8 {
        AL, CL,
        DL, BL,
        AH, CH,
        DH, BH
    }

    public static class ValueAt {
        Register register;
    }
}
