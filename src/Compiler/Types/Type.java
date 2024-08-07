package Compiler.Types;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;

public interface Type {

    int getSize();
    default int getAlignmentSize() {
        return getSize();
    }

    static void cast(Type from, Type to) {
        if (from.getClass() == to.getClass()) {
            if (from instanceof BasicType) {
                if (((BasicType) from).size == 0) {
                    System.err.println("Cannot cast from void type");
                    System.exit(-1);
                }
                // TODO: Do the reverse
                if (((BasicType) from).isFloat() && !((BasicType) to).isFloat()) {
                    Assembler.push(Register.x32.EAX);
                    Assembler.cvttss2si(Register.x32.EAX, new RegisterMemory32(null, Register.x32.ESP));
                    Assembler.add(new RegisterMemory32(Register.x32.ESP), 4);
                } else if (!((BasicType) from).isFloat() && ((BasicType) to).isFloat()) {
                    // Be careful with assuming the non-float is 32 bit wide
                    Assembler.push(Register.x32.EAX);                     // Push integer value onto the stack
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP)); // Load integer as float into st(0)
                    Assembler.fstp(new RegisterMemory32(null, Register.x32.ESP)); // Store float back to the top of the stack
                    Assembler.pop(Register.x32.EAX);
                }
                else if (((BasicType) from).size < ((BasicType) to).size) {
                    Assembler.movzx(Register.x32.EAX, ((BasicType) to).size, new RegisterMemory(Register.x32.EAX), ((BasicType) from).size);
                }
            }
        } else if(from instanceof ReferenceType) {
            int size = ((ReferenceType) from).to.getSize();
            Assembler.mov(Register.x32.EAX.ordinal(), size, new RegisterMemory(null, Register.x32.EAX), size);
            Type.cast(((ReferenceType) from).to, to);
        }
        else {
            System.err.println("Types types don't match");
            System.exit(-1);
        }
    }

    static void castToSize(Type type, int size) {
        Assembler.movzx(Register.x32.EAX, size, new RegisterMemory(Register.x32.EAX), type.getSize());
    }
}
