package works.hop;

import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.*;

public class AsmApp {
    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException {
        simpleClassReader();
        LocalClassLoader local = new LocalClassLoader();
//        Class quantity = local.defineClass("works.hop.Quantity", simpleClassGenerator());
//        Measurable obj = (Measurable) quantity.newInstance();
//        assert obj != null;
        //SomeClass someClass = new SomeClass();
        //someClass.setValue(1);

        //parse SomeClass into byte[]
        ClassReader cr = new ClassReader("works.hop.SomeClass");
        ClassWriter cw = new ClassWriter(cr, 0);
        cr.accept(cw, 0);
        byte[] b1 = cw.toByteArray();

        //write new byte[] from value generated
        ClassReader cr2 = new ClassReader(b1);
        ClassWriter cw2 = new ClassWriter(0);
        cr2.accept(cw2, 0);
        byte[] b2 = cw2.toByteArray(); // b2 represents the same class as b1

        // introduce a ClassVisitor between the class reader and the class writer:
        ClassReader cr3 = new ClassReader(b1);
        ClassWriter cw3 = new ClassWriter(0);
        // cv forwards all events to cw - allows transformations before events reach the class writer
        ClassVisitor cv3 = new ClassVisitor(ASM6, cw3) {
        };
        cr3.accept(cv3, 0);
        byte[] b3 = cw3.toByteArray();

        //optimizing transformation
        ClassReader cr4 = new ClassReader(b1);
        ClassWriter cw4 = new ClassWriter(cr4, 0);
        ClassVisitor cv4 = new ClassVisitor(ASM6, cw4) {
        }; //subclass to define transformations
        cr4.accept(cv4, 0);
        byte[] b4 = cw4.toByteArray();

        //try the remove method transformation
        ClassReader cr5 = new ClassReader(b1);
        ClassWriter cw5 = new ClassWriter(cr5, 0);
        RemoveMethodAdapter cv5 = new RemoveMethodAdapter(cw5, "getValue", "()I");
        cr5.accept(cv5, 0);
        byte[] b5 = cw5.toByteArray();

        //try load transformed class
        Class someClass2 = local.defineClass("works.hop.SomeClass", b5);
        Object someClassObj = someClass2.newInstance();
        assert someClassObj != null;

        //using a trace visitor to visualize generated class
        ClassReader cr6 = new ClassReader(b5);
        ClassWriter cw6 = new ClassWriter(cr6, 0);
        TraceClassVisitor cv6 = new TraceClassVisitor(cw6, new PrintWriter(System.out));
        cr6.accept(cv6, 0);
        byte[] b6 = cw6.toByteArray();

        //using a class checker for the generated class
        ClassReader cr7 = new ClassReader(b6);
        ClassWriter cw7 = new ClassWriter(cr7, 0);
        TraceClassVisitor tcv7 = new TraceClassVisitor(cw7, new PrintWriter(System.out));
        CheckClassAdapter cv7 = new CheckClassAdapter(tcv7);
        cr7.accept(cv7, 0);
        byte[] b7 = cw7.toByteArray();

        //using asmifier to generate asm code
        ASMifier.main(new String[]{SomeClass.class.getName()});
    }

    public static void simpleClassReader() throws IOException {
        ClassPrinter cp = new ClassPrinter();
        ClassReader cr = new ClassReader("java.lang.Runnable");
        cr.accept(cp, 0);
    }

    public static byte[] simpleClassGenerator() {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "works/hop/Quantity", null, "java/lang/Object",
                new String[]{"works/hop/Measurable"});
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I",
                null, Integer.valueOf(-1)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I",
                null, Integer.valueOf(0)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I",
                null, Integer.valueOf(1)).visitEnd();
        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "compareTo",
                "(Ljava/lang/Object;)I", null, null).visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }

    public static byte[] transformedClass(byte[] b1) {
        ClassReader cr = new ClassReader(b1);
        ClassWriter cw = new ClassWriter(cr, 0);
        ChangeVersionAdapter ca = new ChangeVersionAdapter(cw);
        cr.accept(ca, 0);
        return cw.toByteArray();
    }

    static class LocalClassLoader extends ClassLoader {
        protected LocalClassLoader() {
            super(Thread.currentThread().getContextClassLoader());
        }

        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    static class ChangeVersionAdapter extends ClassVisitor {
        public ChangeVersionAdapter(ClassVisitor cv) {
            super(ASM6, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            cv.visit(V1_6, access, name, signature, superName, interfaces);
        }
    }

    static class ClassVersionTransformer {
        public static void premain(String agentArgs, Instrumentation inst) {
            inst.addTransformer(new ClassFileTransformer() {
                public byte[] transform(ClassLoader l, String name, Class c, ProtectionDomain d, byte[] b) throws IllegalClassFormatException {
                    ClassReader cr = new ClassReader(b);
                    ClassWriter cw = new ClassWriter(cr, 0);
                    ClassVisitor cv = new ChangeVersionAdapter(cw);
                    cr.accept(cv, 0);
                    return cw.toByteArray();
                }
            });
        }
    }

    static class RemoveMethodAdapter extends ClassVisitor {
        private String mName;
        private String mDesc;

        public RemoveMethodAdapter(ClassVisitor cv, String mName, String mDesc) {
            super(ASM4, cv);
            this.mName = mName;
            this.mDesc = mDesc;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(mName) && desc.equals(mDesc)) {
                // do not delegate to next visitor -> this removes the method
                return null;
            }
            return cv.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    static class AddFieldAdapter extends ClassVisitor {
        private int fAcc;
        private String fName;
        private String fDesc;
        private boolean isFieldPresent;

        public AddFieldAdapter(ClassVisitor cv, int fAcc, String fName, String fDesc) {
            super(ASM6, cv);
            this.fAcc = fAcc;
            this.fName = fName;
            this.fDesc = fDesc;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (name.equals(fName)) {
                isFieldPresent = true;
            }
            return cv.visitField(access, name, desc, signature, value);
        }

        @Override
        public void visitEnd() {
            if (!isFieldPresent) {
                FieldVisitor fv = cv.visitField(fAcc, fName, fDesc, null, null);
                if (fv != null) {
                    fv.visitEnd();
                }
            }
            cv.visitEnd();
        }
    }
}
