package Compiler.Types;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;

public interface Type {

    int getSize();
    default int getAlignmentSize() {
        return getSize();
    };

    static void cast(Type from, Type to) {
        if (from.getClass() == to.getClass()) {
            if (from instanceof BasicType) {
                if (((BasicType) from).size < ((BasicType) to).size) {
                    Assembler.movsx(Register.x32.EAX, ((BasicType) to).size, new RegisterMemory(Register.x32.EAX), ((BasicType) from).size);
                }
            }
        } else {
            System.err.println("Types types don't match");
            System.exit(-1);
        }
    }

    static void castToSize(Type type, int size) {
        Assembler.movsx(Register.x32.EAX, size, new RegisterMemory(Register.x32.EAX), type.getSize());
    }
}
