package cfh.calculator.expr;

import static java.util.Objects.requireNonNull;

import java.util.function.UnaryOperator;

import cfh.calculator.data.Data;
import cfh.calculator.data.Environment;


final class Unary<D extends Data<D>> extends ExpressionImpl<D> {
    
    private final UnaryName name;
    private final ExpressionImpl<D> expression;
    private final UnaryOperator<D> function;

    Unary(UnaryName name, ExpressionImpl<D> expression, UnaryOperator<D> function) {
        this.name = requireNonNull(name);
        this.expression = requireNonNull(expression);
        this.function = requireNonNull(function);
    }

    @Override
    public D eval(Environment<D> environment) throws EvalException {
        return function.apply(expression.eval(environment));
    }

    @Override
    public String toString() {
        return name + "(" + expression + ")";
    }
}
