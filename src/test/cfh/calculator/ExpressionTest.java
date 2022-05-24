package cfh.calculator;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ExpressionTest {

    static class Unspecific {
        @ParameterizedTest
        @CsvSource({
            "2 * 3 + 5, 11",
            "2 + 3 * 5, 17",
            "(2+3) * 5, 25"
        })
        void test(String text, double expected) throws Exception {
            var expr = Expression.parse(text);
            assertEquals(expected, expr.eval(null));
        }
    }
    
    static class ParseTests {
        
        // primary = literal | variable | function | '(' expression ')'
        static class PrimaryTests {
            @ParameterizedTest
            @CsvSource({
                    "1,   1.0", 
                    "2.3, 2.3", 
                    ".4,  0.4"
            })
            void testLiteral(String text, double expected) throws Exception {
                var expr = Expression.parse(text);
                assertInstanceOf(Expression.Literal.class, expr);
                assertEquals(expected, expr.eval(null));
            }

            @Test()
            void testLiteralException() throws Exception {
                ParseException ex = assertThrows(
                    ParseException.class,
                    () -> Expression.parse("1.2.3")
                    );
                assertEquals(0, ex.getErrorOffset());
                assertInstanceOf(NumberFormatException.class, ex.getCause());
            }

            @ParameterizedTest
            @CsvSource({
                    "x,     12.3", 
                    "y,     -1.0",
                    "x + y, 11.3"
            })
            void testVariable(String text, double expected) throws Exception {
                var expr = Expression.parse(text);
                assertEquals(expected, expr.eval(Map.of("x", 12.3, "y", -1.0)));
            }

            @ParameterizedTest
            @CsvSource({
                    "sqrt(9.0),  3.0", 
                    "sqrt (25),  5.0", 
                    "sqrt( 36 ), 6.0",
                    "sin(PI),    0.0",
                    "cos(PI),   -1.0",
                    "sin(PI/2),  1.0",
                    "cos(PI/2),  0.0"
            })
            void testFunction(String text, double expected) throws Exception {
                var expr = Expression.parse(text);
                assertInstanceOf(Expression.Call.class, expr);
                assertEquals(expected, expr.eval(Map.of("PI", Math.PI)), 0.001);
            }
            
            @Test
            void testFunctionException() throws Exception {
                ParseException ex = assertThrows(
                    ParseException.class,
                    () -> Expression.parse("nonexistent(1)")
                    );
                assertEquals(0, ex.getErrorOffset());
                assertTrue(ex.getMessage().contains("nonexisten"));
            }

            @Test
            void testParenthesis() throws Exception {
                var expr = Expression.parse("(10.0)");
                assertInstanceOf(Expression.Literal.class, expr);
                assertEquals(10.0, expr.eval(null));
            }
        }
        
        // unary = '+' unary | '-' unary | primary
        static class UnaryTests {
            @ParameterizedTest
            @CsvSource({
                "+10.0,  10.0", 
                "+ 11.0, 11.0"
            })
            void testUnaryPos(String text, double expected) throws Exception {
                var expr = Expression.parse(text);
                assertInstanceOf(Expression.Literal.class, expr);
                assertEquals(expected, expr.eval(null));
                
            }
            
            @ParameterizedTest
            @CsvSource({
                "-20.0,  -20.0", 
                "- 21.0, -21.0"
            })
            void testUnaryNeg(String text, double expected) throws Exception {
                var expr = Expression.parse(text);
                assertInstanceOf(Expression.Negate.class, expr);
                assertEquals(expected, expr.eval(null));
            }
            
            @Test
            void testUnaryNone() throws Exception {
                var expr = Expression.parse("30.0");
                assertInstanceOf(Expression.Literal.class, expr);
                assertEquals(30.0, expr.eval(null));
            }
            
            @ParameterizedTest
            @ValueSource(strings = {"++1", "--2", "+-3", "-+4"})
            void testUnaryException(String text) {
                ParseException ex = assertThrows(
                    ParseException.class,
                    () -> Expression.parse(text)
                    );
                assertEquals(1, ex.getErrorOffset());
            }
        }
        
        // multiplicative = unary | multiplicative '*' unary | multiplicative '/' unary | multiplicative '%' unary
        static class MultiplicativeTests {
            @ParameterizedTest
            @CsvSource({
                "2 * 3,  6",
                "3*4*5, 60"
            })
            void testMultiplication(String text, double expected) throws Exception {
                var expr = Expression.parse(text);
                assertInstanceOf(Expression.Multiplicate.class, expr);
                assertEquals(expected, expr.eval(null));
            }
            
            @ParameterizedTest
            @CsvSource({
                "6 / 3,  2",
                "60/5/4, 3"
            })
            void testDivision(String text, double expected) throws Exception {
                var expr = Expression.parse(text);
                assertInstanceOf(Expression.Divide.class, expr);
                assertEquals(expected, expr.eval(null));
            }
            
            @ParameterizedTest
            @CsvSource({
                "8 % 5,   3",
                "28 % 10, 8",
                "28%10%3, 2"
            })
            void testRemainder(String text, double expected) throws Exception {
                var expr = Expression.parse(text);
                assertInstanceOf(Expression.Remainder.class, expr);
                assertEquals(expected, expr.eval(null));
            }
        }
        
        // additive = multiplicative | additive '+' multiplicative | additive  '-' multiplicative
        static class AdditionTests {
            @ParameterizedTest
            @CsvSource({
                "2 + 3,  5",
                "3+4+5, 12"
            })
            void testAdd(String text, double expected) throws Exception {
                var expr = Expression.parse(text);
                assertInstanceOf(Expression.Add.class, expr);
                assertEquals(expected, expr.eval(null));
            }
            
            @ParameterizedTest
            @CsvSource({
                "6 - 3,   3",
                "60-5-4, 51"
            })
            void testSubtract(String text, double expected) throws Exception {
                var expr = Expression.parse(text);
                assertInstanceOf(Expression.Subtract.class, expr);
                assertEquals(expected, expr.eval(null));
            }
        }
    }
}
