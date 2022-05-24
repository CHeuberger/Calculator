package cfh.calculator.expr;

import cfh.calculator.Expression;
import cfh.calculator.data.Data;


public sealed abstract class ExpressionImpl<D extends Data<D>> implements Expression<D>
permits Literal<D>, Unary<D>, Binary<D>, Variable<D>, FunctionCall<D> {

    ExpressionImpl() {
    }

}
