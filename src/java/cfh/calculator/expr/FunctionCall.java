package cfh.calculator.expr;

import static java.util.Objects.requireNonNull;

import java.util.function.UnaryOperator;

import cfh.calculator.data.Data;
import cfh.calculator.data.Environment;


final class FunctionCall<D extends Data<D>> extends ExpressionImpl<D> {
    
    private final FunctionName name;
    private final ExpressionImpl<D> argument;
    private final UnaryOperator<D> operator;
    
    FunctionCall(FunctionName name, ExpressionImpl<D> argument, UnaryOperator<D> operator) {
        this.name = requireNonNull(name);
        this.argument = requireNonNull(argument);
        this.operator = requireNonNull(operator);
    }
    
    @Override
    public D eval(Environment<D> environment) throws EvalException {
        return operator.apply(argument.eval(environment));
    }
    
    @Override
    public String toString() {
        return name + "(" + argument + ")";
    }
}
