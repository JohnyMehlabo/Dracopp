package Compiler.Types;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Struct {
    static Map<String, Struct> structs = new HashMap<>();

    public static void declareStruct(String name, LinkedHashMap<String, Type> members) {
        structs.put(name, new Struct(members));
    }

    public static Struct resolveStruct(String name) {
        Struct struct = structs.get(name);
        if (struct == null) {
            System.err.printf("Undeclared struct \"%s\"\n", name);
            System.exit(-1);
        }
        return struct;
    }

    LinkedHashMap<String, Member> members = new LinkedHashMap<>();
    int size;
    int alignmentSize;

    private Struct(LinkedHashMap<String, Type> members) {
        int offset = 0;
        int highestSize = 0;
        for (Map.Entry<String, Type> member : members.entrySet()) {
            Type type = member.getValue();
            int size = type.getSize();
            int alignmentSize = size;

            if (type instanceof StructType)
                alignmentSize = ((StructType) type).getAlignmentSize();

            if (alignmentSize > highestSize)
                highestSize = alignmentSize;
            if (offset % alignmentSize > 0)
                offset += alignmentSize - (offset % alignmentSize);

            this.members.put(member.getKey(), new Member(member.getValue(), offset));
            offset += size;
        }
        if (offset % highestSize > 0)
            offset += highestSize - (offset % highestSize);
        size = offset;
        alignmentSize = highestSize;
    }

    public Member getMember(String name) {
        Member member = members.get(name);
        if (member == null) {
            System.err.printf("No member \"%s\" in struct\n", name);
            System.exit(-1);
        }
        return member;
    }

    public static class Member {
        public Type type;
        public int offset;

        Member(Type type, int offset) {
            this.type = type;
            this.offset = offset;
        }
    }
}
