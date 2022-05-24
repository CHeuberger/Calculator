package cfh.calculator.expr;

import java.util.Objects;


public enum BinaryName {

    ADD("+"),
    SUBTRACT("-"),
    MULTIPLICATE("*"),
    DIVIDE("/"),
    REMAINDER("%"),
    ;
    
    private final String name;
    
    private BinaryName(String name) {
        this.name = Objects.requireNonNull(name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
