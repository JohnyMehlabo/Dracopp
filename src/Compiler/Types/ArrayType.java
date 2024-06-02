package Compiler.Types;

public class ArrayType implements Type {
    public Type type;
    public int size;

    public ArrayType(Type type, int size) {
        this.type = type;
        this.size = size;
    }

    @Override
    public int getSize() {
        return type.getSize() * size;
    }

    @Override
    public int getAlignmentSize() {
        return type.getAlignmentSize();
    }
}
