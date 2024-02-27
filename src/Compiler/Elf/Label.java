package Compiler.Elf;

public class Label {
    public final int offset;
    public final int sectionIndex;
    public final String name;

    public Label(int offset, int sectionIndex, String name) {
        this.offset = offset;
        this.sectionIndex = sectionIndex;
        this.name = name;
    }
}
