package Compiler.Types;

import java.util.*;

public class Class {
    static Map<String, Class> classes = new HashMap<>();

    public static void declareClass(String name, Class parent, LinkedHashMap<String, Type> members, LinkedHashMap<String, Method> methods) {
        classes.put(name, new Class(parent, members, methods));
    }

    public static Class resolveClass(String name) {
        Class struct = classes.get(name);
        if (struct == null) {
            System.err.printf("Undeclared class \"%s\"\n", name);
            System.exit(-1);
        }
        return struct;
    }

    Class parent;
    LinkedHashMap<String, Member> members = new LinkedHashMap<>();
    LinkedHashMap<String, Method> methods = new LinkedHashMap<>();

    int size;
    int alignmentSize;

    private Class(Class parent, LinkedHashMap<String, Type> members, LinkedHashMap<String, Method> methods) {
        this.parent = parent;

        if (parent != null)
            // Add super member
            this.members.put("super", new Member(new ClassType(parent), 0));

        int offset = 0;
        int highestSize = 0;

        // Set-up correct values if parent is present
        if (parent != null) {
            if (parent.alignmentSize > highestSize)
                highestSize = parent.alignmentSize;
            offset += parent.size;
        }

        // Calculate new member offsets
        for (Map.Entry<String, Type> member : members.entrySet()) {
            Type type = member.getValue();
            int size = type.getSize();
            int alignmentSize = type.getAlignmentSize();

            if (alignmentSize > highestSize)
                highestSize = alignmentSize;
            if (offset % alignmentSize > 0)
                offset += alignmentSize - (offset % alignmentSize);

            this.members.put(member.getKey(), new Member(member.getValue(), offset));
            offset += size;
        }
        if (highestSize == 0) {
            System.err.println("Attempt to declare empty class");
            System.exit(-1);
        }
        if (offset % highestSize > 0)
            offset += highestSize - (offset % highestSize);
        size = offset;
        alignmentSize = highestSize;

        // Add methods
        for (String methodName: methods.keySet()) {
            Method method = methods.get(methodName);
            method.args.add(0, new Method.Arg("this", new PointerType(new ClassType(this))));
            this.methods.put(methodName, method);
        }
    }

    public Member getMember(String name) {
        Member member = members.get(name);
        if (member == null && parent != null) {
            member = parent.getMember(name);
        }
        else if (member == null) {
            System.err.printf("No member \"%s\" in class\n", name);
            System.exit(-1);
        }
        return member;
    }

    public Method getMethod(String name) {
        Method method = methods.get(name);
        if (method == null && parent != null) {
            method = parent.getMethod(name);
        }
        else if (method == null) {
            System.err.printf("No method \"%s\" in class\n", name);
            System.exit(-1);
        }
        return method;
    }

    public static class Member {
        public Type type;
        public int offset;

        Member(Type type, int offset) {
            this.type = type;
            this.offset = offset;
        }
    }

    public static class Method {
        public String name;
        public Type returnType;
        public List<Arg> args;
        public String symbol;

        public Method(String name, Type returnType, List<Arg> args, String symbol) {
            this.name = name;
            this.returnType = returnType;
            this.args = args;
            this.symbol = symbol;
        }

        public static class Arg {
            public String name;
            public Type type;

            public Arg(String name, Type type) {
                this.name = name;
                this.type = type;
            }
        }
    }
}
