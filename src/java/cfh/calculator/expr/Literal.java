package cfh.calculator.expr;

import static java.util.Objects.requireNonNull;

import cfh.calculator.data.Data;
import cfh.calculator.data.Environment;


final class Literal<D extends Data<D>> extends ExpressionImpl<D> {
    
    private final D value;

    Literal(D value) { 
        this.value = requireNonNull(value); 
    }

    @Override
    public D eval(Environment<D> environment) throws EvalException {
        return value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
