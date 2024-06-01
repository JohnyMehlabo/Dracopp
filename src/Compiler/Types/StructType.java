package Compiler.Types;

public class StructType implements Type {
    public Struct struct;

    public StructType(Struct struct) {
        this.struct = struct;
    }

    @Override
    public int getSize() {
        return struct.size;
    }

    public int getAlignmentSize() {
        return struct.alignmentSize;
    }
}
