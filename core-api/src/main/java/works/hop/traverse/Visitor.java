package works.hop.traverse;

public interface Visitor<PARENT, CHILD> {

    void visitParent(PARENT visitable);

    void visitChild(CHILD visitable);
}
