package cfh.calculator.expr;

import java.util.Objects;


public enum UnaryName {

    PLUS("+"),
    MINUS("-"),
    ;
    
    private final String name;
    
    private UnaryName(String name) {
        this.name = Objects.requireNonNull(name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
