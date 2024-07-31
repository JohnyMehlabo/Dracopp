package Compiler.Assembler;

public class RegisterMemory {
    Register reg;
    boolean readAddress = false;
    Register.x32 addressReg;
    boolean hasDisplacement = false;
    int displacement = 0;

    public RegisterMemory(Register reg) {
        this.reg = reg;
    }

    public RegisterMemory(Register reg, Register.x32 addressReg) {
        this.reg = reg;
        this.readAddress = true;
        this.addressReg = addressReg;
    }

    public RegisterMemory(Register reg, Register.x32 addressReg, int displacement) {
        this.reg = reg;
        this.addressReg = addressReg;
        if (addressReg != null)
            this.readAddress = true;
        this.displacement = displacement;
        if (displacement != 0)
            this.hasDisplacement = true;
    }
}

