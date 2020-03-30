package works.hop.reducer.json;

import lombok.*;
import org.objectweb.asm.*;

import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.ASM7;

public class ClassInspect extends ClassVisitor {

    private String prefix;
    private Map<String, PropAttr> properties;
    private List<String> getters;

    public ClassInspect(String prefix, Map<String, PropAttr> properties, List<String> getters) {
        super(ASM7);
        this.prefix = prefix;
        this.properties = properties;
        this.getters = getters;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.printf("visit -> version: %d, access: %d, name: %s, signature: %s, superName: %s extends interfaces[]: %s {%n",
                version, access, name, signature, superName, String.join(",", interfaces));
        this.prefix = this.prefix.trim().length() > 0 ? this.prefix += (":" + name) : name;
    }

    public void visitSource(String source, String debug) {
        System.out.printf("visitSource -> source: %s, debug: %s%n", source, debug);
    }

    public void visitOuterClass(String owner, String name, String desc) {
        System.out.printf("visitOuterClass -> owner: %s, name: %s, desc: %s%n", owner, name, desc);
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        System.out.printf("visitAnnotation -> desc: %s, visible: %s%n", desc, visible);
        return null;
    }

    public void visitAttribute(Attribute attr) {
        System.out.printf("attr: %s%n", attr.type);
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        System.out.printf("visitAttribute -> name: %s, outerName: %s, outerName: %s, access: %d%n", name, outerName, innerName, access);
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        System.out.printf("visitField -> access: %d, name: %s, desc: %s, signature: %s, value: %s%n", access, name, desc, signature, value);
        if (desc.startsWith("Ljava/lang/") || desc.startsWith("Ljava/util/") || FlatJson.PRIMITIVES_ARRAY_PATTERN.matcher(desc).matches() ||
                FlatJson.IMPLICITS_ARRAY_PATTERN.matcher(desc).matches() || FlatJson.PRIMITIVES_LIST.contains(desc)) {
            properties.put(prefix + ":" + name, PropAttr.builder().access(access).desc(desc).name(name).build());
        }
        else if(desc.startsWith("L/") || desc.startsWith("Ljava/util/Map") || desc.startsWith("Ljava/util/List")
        || desc.startsWith("Ljava/util/Set")){
            try {
                String className = desc.replaceAll("[L;]+", "");
                ClassInspect cp = new ClassInspect(prefix, properties, getters);
                ClassReader cr = new ClassReader(className);
                cr.accept(cp, 0);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        else if(desc.startsWith("[L")){
            try {
                String className = desc.replaceAll("(\\[L).+(;)", "");
                ClassInspect cp = new ClassInspect(prefix, properties, getters);
                ClassReader cr = new ClassReader(className);
                cr.accept(cp, 0);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.printf("visitMethod -> access: %d, name: %s, desc: %s, signature: %s, exceptions: %s%n", access, name, desc, signature, exceptions);
        if (access == 1 && desc.startsWith("get")) {
            String property = Character.toUpperCase(name.charAt(3)) + name.substring(4);
            getters.add(prefix + ":" + property);
        }
        return null;
    }

    public void visitEnd() {
        System.out.println("}");
    }

    @Data
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Builder
    static class PropAttr {

        private int access;
        private String name;
        private String desc; //type
    }
}

