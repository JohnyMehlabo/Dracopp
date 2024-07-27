package Compiler.Types;

public class ReferenceType implements Type {
    public final Type to;

    public ReferenceType(Type to) {
        this.to = to;
    }

    @Override
    public int getSize() {
        return 4;
    }
}
