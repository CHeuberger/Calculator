package cfh.calculator;

import java.text.ParseException;
import cfh.calculator.data.*;
import cfh.calculator.expr.*;


public interface Expression<D extends Data<D>> {

    // Grammar:
    // expresison = additive
    // additive = multiplicative | additive '+' multiplicative | additive  '-' multiplicative
    // multiplicative = unary | multiplicative '*' unary | multiplicative '/' unary | multiplicative '%' unary
    // unary = '+' unary | '-' unary | primary
    // primary = literal | variable | function '(' expression ')' | '(' expression ')'
    // function: sqrt | sin | cos
    
    static <D extends Data<D>> Expression<D> parse(DataType<D> type, String text) throws ParseException {
        return new Parser<>(type, text).parse();
    }
    
    static Expression<DoubleData> parseDouble(String text) throws ParseException {
        return new Parser<>(DataType.DOUBLE, text).parse();
    }
    
    //==============================================================================================
    
    D eval(Environment<D> environment) throws EvalException;
    
    //----------------------------------------------------------------------------------------------
    
    default Number evalNumber(Environment<D> variables) throws EvalException {
        return eval(variables).number();
    }
    
    default double evalDouble(Environment<D> variables) throws EvalException {
        return eval(variables).doubleValue();
    }
}
