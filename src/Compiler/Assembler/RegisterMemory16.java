package Compiler.Assembler;

public class RegisterMemory16 {
    Register.x16 reg;
    boolean readAddress = false;
    Register.x32 addressReg;
    boolean hasDisplacement = false;
    int displacement = 0;

    public RegisterMemory16(RegisterMemory registerMemory) {
        if (registerMemory.reg != null)
            this.reg = (Register.x16) Register.fromSize(registerMemory.reg.ordinal(), 2);

        this.addressReg = registerMemory.addressReg;
        if (addressReg != null)
            this.readAddress = true;
        this.displacement = registerMemory.displacement;
        if (displacement != 0)
            this.hasDisplacement = true;
    }
    public RegisterMemory16(Register.x16 reg) {
        this.reg = reg;
    }

    public RegisterMemory16(Register.x16 reg, Register.x32 addressReg) {
        this.reg = reg;
        this.readAddress = true;
        this.addressReg = addressReg;
    }

    public RegisterMemory16(Register.x16 reg, Register.x32 addressReg, int displacement) {
        this.reg = reg;
        this.addressReg = addressReg;
        if (addressReg != null)
            this.readAddress = true;
        this.displacement = displacement;
        if (displacement != 0)
            this.hasDisplacement = true;
    }
}

