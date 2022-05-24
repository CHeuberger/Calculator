package cfh.calculator.expr;

import static java.util.Objects.*;

import java.text.ParseException;
import cfh.calculator.data.Data;
import cfh.calculator.data.DataType;


public class Parser<D extends Data<D>> {
    
    private final DataType<D> datatype;
    private final String text;
    
    private int pos = -1;
    private int ch;

    public Parser(DataType<D> datatype, String text) {
        this.datatype = requireNonNull(datatype);
        this.text = requireNonNull(text);
    }
    
    public synchronized ExpressionImpl<D> parse() throws ParseException {
        pos = -1;
        next();
        skipSpace();
        var expr = parseExpression();
        if (pos < text.length()) {
            throw new ParseException("Unexpected:" + (char)ch, pos);
        }
        System.out.println(expr);
        return expr;
    }
    
    private void next() {
        ch  = (++pos < text.length() ? text.charAt(pos) : -1);
    }

    private void skipSpace() {
        while (ch == ' ') {
            next();
        }
    }

    private boolean is(char expected) {
        if (ch == expected) {
            next();
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isDigit() {
        return (ch >= '0' && ch <= '9') || ch == '.';
    }
    
    private boolean isLetter() {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    // expresison = additive
    private ExpressionImpl<D> parseExpression() throws ParseException {
        return parseAdditive();
    }

    // additive = multiplicative | additive '+' multiplicative | additive  '-' multiplicative
    private ExpressionImpl<D> parseAdditive() throws ParseException {
        skipSpace();
        var x = parseMultiplicative();
        for (;;) {
            skipSpace();
            if (is('+')) x = new Binary<>(BinaryName.ADD, x, parseUnary(), D::add);
            else if (is('-')) x = new Binary<>(BinaryName.SUBTRACT, x, parseUnary(), D::subtract);
            else return x;
        }
    }

    // multiplicative = unary | multiplicative '*' unary | multiplicative '/' unary | multiplicative '%' unary
    private ExpressionImpl<D> parseMultiplicative() throws ParseException {
        skipSpace();
        var x = parseUnary();
        for (;;) {
            skipSpace();
            if (is('*')) x = new Binary<>(BinaryName.MULTIPLICATE, x, parseUnary(), D::multiply);
            else if (is('/')) x = new Binary<>(BinaryName.DIVIDE, x, parseUnary(), D::divide);
            else if (is('%')) x = new Binary<>(BinaryName.REMAINDER, x, parseUnary(), D::remainder);
            else return x;
        }
    }

    // unary = '+' primary | '-' primary | primary
    private ExpressionImpl<D> parseUnary() throws ParseException {
        skipSpace();
        if (is('+')) return new Unary<>(UnaryName.PLUS, parsePrimary(), D::identity);
        if (is('-')) return new Unary<>(UnaryName.MINUS, parsePrimary(), D::negative);
        return parsePrimary();
    }

    // primary = literal | function '(' expression ')' | variable | '(' expression ')'
    private ExpressionImpl<D> parsePrimary() throws ParseException {
        skipSpace();
        // literal
        if (isDigit()) {
            var start = pos;
            while (isDigit()) {
                next();
            }
            String literal = text.substring(start, pos);
            try {
                return new Literal<>(datatype.parse(literal));
            } catch (NumberFormatException ex) {
                throw (ParseException) new ParseException("Invalid literal: " + literal, start).initCause(ex);
            }

        // '(' expression ')'
        } else if (is('(')) {
            var expr = parseExpression();
            if (is(')')) {
                return expr;
            } else {
                throw new ParseException("Missing closing ')'", pos);
            }
        } else if (isLetter()) {
            var start = pos;
            while (isLetter() || isDigit()) {
                next();
            }
            var name = text.substring(start, pos);
            skipSpace();
            if (is('(')) {
                FunctionName function;
                try {
                    function = FunctionName.valueOf(name.toUpperCase());
                } catch (IllegalArgumentException ex) {
                    throw (ParseException) new ParseException("Unknown function: " + name, start).initCause(ex);
                }
                var call = new FunctionCall<>(function, parseExpression(), datatype.unaryFunction(function));
                skipSpace();
                if (is(')')) {
                    return call;
                } else {
                    throw new ParseException("Missing closing ')'",  pos);
                }
            } else {
                return new Variable<>(name);
            }
        } else {
            throw new ParseException("Unexpected: " + (char)ch, pos);
        }
    }
}
