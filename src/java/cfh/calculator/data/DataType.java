package cfh.calculator.data;

import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

import cfh.calculator.expr.FunctionName;


public abstract sealed class DataType<D extends Data<D>> {
    
    public static final DataType<DoubleData> DOUBLE = new DoubleType();

    //==============================================================================================
    
    public abstract D parse(String literal);
    
    public abstract UnaryOperator<D> unaryFunction(FunctionName name);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final class DoubleType extends DataType<DoubleData> {
        @Override
        public DoubleData parse(String literal) {
            return new DoubleData(Double.parseDouble(literal));
        }

        @Override
        public UnaryOperator<DoubleData> unaryFunction(FunctionName name) {
            return switch (name) {
                case SQRT -> unary(Math::sqrt);
                case COS  -> unary(Math::cos);
                case SIN  -> unary(Math::sin);
            };
        }
        
        private UnaryOperator<DoubleData> unary(DoubleUnaryOperator op) {
            return arg -> new DoubleData(op.applyAsDouble(arg.number()));
        }
    }
}
