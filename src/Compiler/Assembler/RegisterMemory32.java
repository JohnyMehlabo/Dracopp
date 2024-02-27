package Compiler.Assembler;

public class RegisterMemory32 {
    Register.x32 reg;
    boolean readAddress = false;
    Register.x32 addressReg;
    boolean hasDisplacement = false;
    byte displacement = 0;

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
        this.readAddress = true;
        this.addressReg = addressReg;
        this.displacement = displacement;
        this.hasDisplacement = true;
    }
}
