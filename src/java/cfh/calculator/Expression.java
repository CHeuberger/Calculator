package cfh.calculator;

import static java.util.Objects.*;

import java.text.ParseException;
import java.util.Map;
import java.util.function.Function;

public sealed abstract class Expression<T extends Number> {

    // Grammar:
    // expresison = additive
    // additive = multiplicative | additive '+' multiplicative | additive  '-' multiplicative
    // multiplicative = unary | multiplicative '*' unary | multiplicative '/' unary | multiplicative '%' unary
    // unary = '+' unary | '-' unary | primary
    // primary = literal | variable | function '(' expression ')' | '(' expression ')'
    // function: sqrt | sin | cos
    public static Expression<Double> parse(String text) throws ParseException {
        requireNonNull(text);
        
        return new Object() {
            int pos = -1;
            int ch;

            void next() {
                ch  = (++pos < text.length() ? text.charAt(pos) : -1);
            }

            void skipSpace() {
                while (ch == ' ') {
                    next();
                }
            }

            boolean is(char expected) {
                if (ch == expected) {
                    next();
                    return true;
                } else {
                    return false;
                }
            }

            Expression<Double> parse() throws ParseException {
                next();
                skipSpace();
                var expr = parseExpression();
                if (pos < text.length()) {
                    throw new ParseException("Unexpected:" + (char)ch, pos);
                }
                System.out.println(expr);
                return expr;
            }

            // expresison = additive
            Expression<Double> parseExpression() throws ParseException {
                return parseAdditive();
            }

            // additive = multiplicative | additive '+' multiplicative | additive  '-' multiplicative
            Expression<Double> parseAdditive() throws ParseException {
                skipSpace();
                Expression<Double> x = parseMultiplicative();
                for (;;) {
                    skipSpace();
                    if (is('+')) x = new Add(x, parseMultiplicative());
                    else if (is('-')) x = new Subtract(x, parseMultiplicative());
                    else return x;
                }
            }

            // multiplicative = unary | multiplicative '*' unary | multiplicative '/' unary | multiplicative '%' unary
            Expression<Double> parseMultiplicative() throws ParseException {
                skipSpace();
                Expression<Double> x = parseUnary();
                for (;;) {
                    skipSpace();
                    if (is('*')) x = new Multiplicate(x, parseUnary());
                    else if (is('/')) x = new Divide(x, parseUnary());
                    else if (is('%')) x = new Remainder(x, parseUnary());
                    else return x;
                }
            }

            // unary = '+' primary | '-' primary | primary
            Expression<Double> parseUnary() throws ParseException {
                skipSpace();
                if (is('+')) return parsePrimary();
                if (is('-')) return new Negate(parsePrimary());
                return parsePrimary();
            }

            // primary = literal | function '(' expression ')' | variable | '(' expression ')'
            Expression<Double> parsePrimary() throws ParseException {
                skipSpace();
                // literal
                if (isDigit()) {
                    var start = pos;
                    while (isDigit()) {
                        next();
                    }
                    String literal = text.substring(start, pos);
                    try {
                        return new Literal<>(Double.valueOf(literal));
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
                        var function = switch (name) {
                            case "sqrt" -> new Call(name, parseExpression(), Math::sqrt);
                            case "sin" -> new Call(name, parseExpression(), Math::sin);
                            case "cos" -> new Call(name, parseExpression(), Math::cos);
                            default -> throw new ParseException("unrecognized function: " + name, start);
                        };
                        if (is(')')) {
                            return function;
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
            
            boolean isDigit() {
                return (ch >= '0' && ch <= '9') || ch == '.';
            }
            
            boolean isLetter() {
                return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
            }
        }.parse();
    }


    private Expression() {
    }

    public abstract T eval(Map<String, T> variables) throws EvalException;

    //----------------------------------------------------------------------------------------------

    static final class Literal<T extends Number> extends Expression<T> {
        private final T value;

        private Literal(T value) { 
            this.value = requireNonNull(value); 
        }

        @Override
        public T eval(Map<String, T> variables) {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    //----------------------------------------------------------------------------------------------

    static final class Variable<T extends Number> extends Expression<T> {
        private final String name;

        private Variable(String name) {
            this.name = requireNonNull(name);
        }
        
        @Override
        public T eval(Map<String, T> variables) throws EvalException {
            if (!variables.containsKey(name)) {
                throw new EvalException("Unknown variable: " + name);
            }
            return variables.get(name);
        }
        
        @Override
        public String toString() {
            return name;
        }
    }

    //----------------------------------------------------------------------------------------------

    static final class Negate extends Expression<Double> {
        private final Expression<Double> expression;

        private Negate(Expression<Double> expression) {
            this.expression = requireNonNull(expression);
        }

        @Override
        public Double eval(Map<String, Double> variables) throws EvalException {
            return -(Double) expression.eval(variables);
        }

        @Override
        public String toString() {
            return "-(" + expression + ")";
        }
    }

    //----------------------------------------------------------------------------------------------

    static final class Add extends Expression<Double> {
        private final Expression<Double> a;
        private final Expression<Double> b;

        private Add(Expression<Double> a, Expression<Double> b) {
            this.a = requireNonNull(a);
            this.b = requireNonNull(b);
        }

        @Override
        public Double eval(Map<String, Double> variables) throws EvalException {
            return a.eval(variables) + b.eval(variables);
        }

        @Override
        public String toString() {
            return "(" + a + "+" + b + ")";
        }
    }

    //----------------------------------------------------------------------------------------------

    static final class Subtract extends Expression<Double> {
        private final Expression<Double> a;
        private final Expression<Double> b;

        private Subtract(Expression<Double> a, Expression<Double> b) {
            this.a = requireNonNull(a);
            this.b = requireNonNull(b);
        }

        @Override
        public Double eval(Map<String, Double> variables) throws EvalException {
            return a.eval(variables) - b.eval(variables);
        }

        @Override
        public String toString() {
            return "(" + a + "-" + b + ")";
        }
    }

    //----------------------------------------------------------------------------------------------

    static final class Multiplicate extends Expression<Double> {
        private final Expression<Double> a;
        private final Expression<Double> b;

        private Multiplicate(Expression<Double> a, Expression<Double> b) {
            this.a = requireNonNull(a);
            this.b = requireNonNull(b);
        }

        @Override
        public Double eval(Map<String, Double> variables) throws EvalException {
            return a.eval(variables) * b.eval(variables);
        }

        @Override
        public String toString() {
            return "(" + a + "*" + b + ")";
        }
    }

    //----------------------------------------------------------------------------------------------

    static final class Divide extends Expression<Double> {
        private final Expression<Double> a;
        private final Expression<Double> b;

        private Divide(Expression<Double> a, Expression<Double> b) {
            this.a = requireNonNull(a);
            this.b = requireNonNull(b);
        }

        @Override
        public Double eval(Map<String, Double> variables) throws EvalException {
            return a.eval(variables) / b.eval(variables);
        }

        @Override
        public String toString() {
            return "(" + a + "/" + b + ")";
        }
    }

    //----------------------------------------------------------------------------------------------

    static final class Remainder extends Expression<Double> {
        private final Expression<Double> a;
        private final Expression<Double> b;

        private Remainder(Expression<Double> a, Expression<Double> b) {
            this.a = requireNonNull(a);
            this.b = requireNonNull(b);
        }

        @Override
        public Double eval(Map<String, Double> variables) throws EvalException {
            return a.eval(variables) % b.eval(variables);
        }

        @Override
        public String toString() {
            return "(" + a + "%" + b + ")";
        }
    }
    
    //----------------------------------------------------------------------------------------------
    
    static final class Call extends Expression<Double> {
        private final String name;
        private Expression<Double> argument;
        private final Function<Double, Double> function;
        
        private Call(String name, Expression<Double> argument, Function<Double, Double> function) {
            this.name = requireNonNull(name);
            this.argument = requireNonNull(argument);
            this.function = requireNonNull(function);
        }
        
        @Override
        public Double eval(Map<String, Double> variables) throws EvalException {
            return function.apply(argument.eval(variables));
        }
        
        @Override
        public String toString() {
            return name + "(" + argument + ")";
        }
    }
}
