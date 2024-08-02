package Compiler.Types;

public class ClassType implements Type {
    public Class aClass;

    public ClassType(Class aClass) {
        this.aClass = aClass;
    }

    @Override
    public int getSize() {
        if (!aClass.isDefined) {
            System.err.println("Cannot get size of class before it is defined");
            System.exit(-1);
        }
        return aClass.size;
    }

    @Override
    public int getAlignmentSize() {
        return aClass.alignmentSize;
    }
}
