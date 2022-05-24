package cfh.calculator.expr;


public enum FunctionName {

    SQRT,
    SIN,
    COS,
    ;
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
