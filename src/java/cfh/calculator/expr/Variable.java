package cfh.calculator.expr;

import static java.util.Objects.requireNonNull;

import cfh.calculator.data.Data;
import cfh.calculator.data.Environment;


final class Variable<D extends Data<D>> extends ExpressionImpl<D> {
    
    private final String name;

    Variable(String name) {
        this.name = requireNonNull(name);
    }
    
    @Override
    public D eval(Environment<D> environment) throws EvalException {
        if (!environment.contains(name)) {
            throw new EvalException("Unknown variable: " + name);
        }
        return environment.get(name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
