package Compiler.Types;

import java.util.Arrays;

public enum BasicType implements Type {
    Float("float", 4, true),

    Int("int", 4),
    Short("short", 2),
    Char("char", 1),
    Bool("bool", 1),
    Void("void", 0),
    ;
    BasicType(String name, int size) {
        this.name = name;
        this.size = size;
        this.isFloat = false;
    }
    BasicType(String name, int size, boolean isFloat) {
        this.name = name;
        this.size = size;
        this.isFloat = isFloat;
    }

    final String name;
    final int size;
    final boolean isFloat;

    public boolean isFloat() {
        return isFloat;
    }

    public static BasicType get(String name) {
        return Arrays.stream(BasicType.values()).filter(t ->t.name.equals(name)).findFirst().orElse(null);
    }

    @Override
    public int getSize() {
        return size;
    }
}
