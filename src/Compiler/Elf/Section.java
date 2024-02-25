package Compiler.Elf;

public class Section {
    protected short index = -1;
    protected int nameIndex;
    protected int type;
    protected int flags;
    protected int address;
    protected int offset;
    protected int size = 0;
    protected int link;
    protected int info;
    protected int addressAlign;
    protected int entrySize;
    private byte[] data;

    public Section(int nameIdx, int type, int flags, int address, int link, int info, int addressAlign, int entrySize) {
        this.nameIndex = nameIdx;
        this.type = type;
        this.flags = flags;
        this.address = address;
        this.link = link;
        this.info = info;
        this.addressAlign = addressAlign;
        this.entrySize = entrySize;
        this.data = new byte[0];
    }

    public byte[] getData() {
        return data;
    }

    public short getIndex() {
        return index;
    }

    public void setData(byte[] data) {
        this.data = data;
        size = data.length;
    }
}
