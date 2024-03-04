package Compiler.Types;

public class PointerType implements Type {
    public final Type to;

    public PointerType(Type to) {
        this.to = to;
    }

    @Override
    public int getSize() {
        return 4;
    }

}
