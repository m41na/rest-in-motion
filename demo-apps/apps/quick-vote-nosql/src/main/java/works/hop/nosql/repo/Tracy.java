package works.hop.nosql.repo;

import java.util.HashSet;
import java.util.Set;

public class Tracy {

    public static String unpackStack(Throwable e) {
        return unpackStackHelper(e, new HashSet<>(), new HashSet<>());
    }

    private static String unpackStackHelper(Throwable e, Set<String> builder, Set<Throwable> checked) {
        if (e == null) {
            return builder.stream().reduce("", (left, right) -> left.concat("\n").concat(right));
        } else if (!checked.contains(e)) {
            builder.add(e.getMessage().replaceAll("(\\b[\\w.]+?.(Exception|Error)\\b:?\\s?)", ""));
            checked.add(e);
            return unpackStackHelper(e.getCause(), builder, checked);
        } else {
            return unpackStackHelper(e.getCause(), builder, checked);
        }
    }
}
