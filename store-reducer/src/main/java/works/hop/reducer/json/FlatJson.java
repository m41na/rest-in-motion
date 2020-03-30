package works.hop.reducer.json;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.objectweb.asm.Opcodes.ASM7;

public class FlatJson extends ClassVisitor {

    public static Pattern PRIMITIVES_ARRAY_PATTERN = Pattern.compile("\\[[ZCBSIFDJ]");
    public static List<Character> PRIMITIVES_LIST = Arrays.asList('Z','C','B','S','I','F','D','J');
    public static Map<Class<?>, Character> PRIMITIVES_MAP = new HashMap<>(){
        {
            put(boolean.class, 'Z');
            put(char.class, 'C');
            put(byte.class, 'B');
            put(short.class, 'S');
            put(int.class, 'I');
            put(float.class, 'F');
            put(double.class, 'D');
            put(long.class, 'J');
        }
    };

    public static Pattern IMPLICITS_ARRAY_PATTERN = Pattern.compile("\\[Ljava/lang/.+");
    public static Map<Class<?>, String> IMPLICITS_MAP = new HashMap<>(){
        {
            put(Boolean.class, "Ljava/lang/Boolean;");
            put(Character.class, "Ljava/lang/Character;");
            put(Byte.class, "Ljava/lang/Byte;");
            put(Short.class, "Ljava/lang/Short;");
            put(Integer.class, "Ljava/lang/Integer;");
            put(Float.class, "Ljava/lang/Float;");
            put(Double.class, "Ljava/lang/Double;");
            put(String.class, "Ljava/lang/String;");
        }
    };

    public FlatJson() {
        super(ASM7);
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println(name + " extends " + superName + " {");
    }

    public static Map<String, String>  flatten(Object object)  {
        try {
            ClassReader reader = new ClassReader(object.getClass().getName());
            return Collections.emptyMap();
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        return null;
    }
}
