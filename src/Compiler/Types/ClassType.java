package Compiler.Types;

public class ClassType implements Type {
    public Class aClass;

    public ClassType(Class aClass) {
        this.aClass = aClass;
    }

    @Override
    public int getSize() {
        return aClass.size;
    }

    @Override
    public int getAlignmentSize() {
        return aClass.alignmentSize;
    }
}
