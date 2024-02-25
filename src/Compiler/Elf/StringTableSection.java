package Compiler.Elf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StringTableSection extends Section {

    ByteArrayOutputStream data = new ByteArrayOutputStream();

    public StringTableSection(int nameIdx, int flags, int address) {
        super(nameIdx, 3, flags, address, 0, 0, 1, 0);
        data.write('\0');
        size = data.size();
    }

    @Override
    public byte[] getData() {
        return data.toByteArray();
    }

    public int addString(String name) throws IOException {
        int index = data.size();
        data.write(name.getBytes(StandardCharsets.US_ASCII));
        data.write('\0');

        size = data.size() + 1;

        return index;
    }
}
