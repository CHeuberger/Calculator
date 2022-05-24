package cfh.calculator.expr;


import static java.util.Objects.requireNonNull;

import java.util.function.BinaryOperator;

import cfh.calculator.data.Data;
import cfh.calculator.data.Environment;

final class Binary<D extends Data<D>> extends ExpressionImpl<D> {
    
    private final BinaryName name;
    private final ExpressionImpl<D> expression1;
    private final ExpressionImpl<D> expression2;
    private final BinaryOperator<D> function;

    Binary(BinaryName name, ExpressionImpl<D> expression1, ExpressionImpl<D> expression2, BinaryOperator<D> function) {
        this.name = requireNonNull(name);
        this.expression1 = requireNonNull(expression1);
        this.expression2 = requireNonNull(expression2);
        this.function = requireNonNull(function);
    }

    @Override
    public D eval(Environment<D> environment) throws EvalException {
        return function.apply(expression1.eval(environment), expression2.eval(environment));
    }

    @Override
    public String toString() {
        return "(" + expression1 + name + expression2 + ")";
    }
}
