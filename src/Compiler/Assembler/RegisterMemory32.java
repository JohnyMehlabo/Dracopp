package Compiler.Assembler;

public class RegisterMemory32 {
    Register.x32 reg;
    boolean readAddress = false;
    Register.x32 addressReg;
    boolean hasDisplacement = false;
    byte displacement = 0;

    public RegisterMemory32(RegisterMemory registerMemory) {
        if (registerMemory.reg != null)
            this.reg = (Register.x32) Register.fromSize(registerMemory.reg.ordinal(), 2);

        this.addressReg = registerMemory.addressReg;
        if (addressReg != null)
            this.readAddress = true;
        this.displacement = registerMemory.displacement;
        if (displacement != 0)
            this.hasDisplacement = true;
    }
    public RegisterMemory32(Register.x32 reg) {
        this.reg = reg;
    }

    public RegisterMemory32(Register.x32 reg, Register.x32 addressReg) {
        this.reg = reg;
        this.readAddress = true;
        this.addressReg = addressReg;
    }

    public RegisterMemory32(Register.x32 reg, Register.x32 addressReg, byte displacement) {
        this.reg = reg;
        this.addressReg = addressReg;
        if (addressReg != null)
            this.readAddress = true;
        this.displacement = displacement;
        if (displacement != 0)
            this.hasDisplacement = true;
    }
}

