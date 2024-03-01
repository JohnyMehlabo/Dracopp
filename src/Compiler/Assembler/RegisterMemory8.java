package Compiler.Assembler;

public class RegisterMemory8 {
    Register.x8 reg;
    boolean readAddress = false;
    Register.x32 addressReg;
    boolean hasDisplacement = false;
    byte displacement = 0;

    public RegisterMemory8(RegisterMemory registerMemory) {
        this.reg = (Register.x8) Register.fromSize(registerMemory.reg.ordinal(), 1);

        this.addressReg = registerMemory.addressReg;
        if (addressReg != null)
            this.readAddress = true;
        this.displacement = registerMemory.displacement;
        if (displacement != 0)
            this.hasDisplacement = true;
    }

    public RegisterMemory8(Register.x8 reg) {
        this.reg = reg;
    }

    public RegisterMemory8(Register.x8 reg, Register.x32 addressReg) {
        this.reg = reg;
        this.readAddress = true;
        this.addressReg = addressReg;
    }

    public RegisterMemory8(Register.x8 reg, Register.x32 addressReg, byte displacement) {
        this.reg = reg;
        this.addressReg = addressReg;
        if (addressReg != null)
            this.readAddress = true;
        this.displacement = displacement;
        if (displacement != 0)
            this.hasDisplacement = true;
    }
}

