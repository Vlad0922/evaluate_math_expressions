import com.udojava.evalex.*;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Scanner;

import net.openhft.compiler.CompilerUtils;
import org.mariuszgromada.math.mxparser.*;

import parsii.eval.*;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class main {
    public static double target_fun1(double a, double b) {
        return Math.sqrt(a*a + b*b);
    }

    public static void main(String[] args) {

        com.udojava.evalex.Expression expr_evalex = new com.udojava.evalex.Expression("SQRT(a^2 + b^2)");

        for(int i = 0; i < 1000000; ++i) {
            BigDecimal res1 = expr_evalex.with("a", "2.4").and("b", "3.3").eval();
        }

        long startTime = System.nanoTime();

        for(int i = 0; i < 1000000; ++i) {
            BigDecimal res1 = expr_evalex.with("a", "2.4").and("b", "3.3").eval();
        }

        long endTime = System.nanoTime();
        long evalexTime = endTime - startTime;

        org.mariuszgromada.math.mxparser.Argument mx_a = new org.mariuszgromada.math.mxparser.Argument("a");
        org.mariuszgromada.math.mxparser.Argument mx_b = new org.mariuszgromada.math.mxparser.Argument("b");
        org.mariuszgromada.math.mxparser.Expression exp_mx = new org.mariuszgromada.math.mxparser.Expression("sqrt(a^2 + b^2)", mx_a, mx_b);

        for(int i = 0; i < 1000000; ++i) {
            mx_a.setArgumentValue(2.4);
            mx_b.setArgumentValue(3.3);
            double val = exp_mx.calculate();
        }

        startTime = System.nanoTime();

        for(int i = 0; i < 1000000; ++i) {
            mx_a.setArgumentValue(2.4);
            mx_b.setArgumentValue(3.3);
            double val = exp_mx.calculate();
        }

        endTime = System.nanoTime();
        long mxTime = endTime - startTime;

        parsii.eval.Scope scope = new parsii.eval.Scope();
        parsii.eval.Variable a = scope.getVariable("a");
        parsii.eval.Variable b = scope.getVariable("b");

        long parsiiTime = -1;

        try {
            parsii.eval.Expression parsii_expr = parsii.eval.Parser.parse("sqrt(a^2 + b^2)", scope);

            for(int i = 0; i < 1000000; ++i) {
                a.setValue(2.4);
                b.setValue(3.3);
                double res3 = parsii_expr.evaluate();
            }

            startTime = System.nanoTime();

            for(int i = 0; i < 1000000; ++i) {
                a.setValue(2.4);
                b.setValue(3.3);
                double res3 = parsii_expr.evaluate();
            }

            endTime = System.nanoTime();

            parsiiTime = endTime - startTime;
        }
        catch(Exception e) {
            System.out.println("wtf");
        }

        for(int i = 0; i < 1000000; ++i) {
            double res2 = target_fun1(2.4, 3.3);
        }

        startTime = System.nanoTime();

        for(int i = 0; i < 1000000; ++i) {
            double res2 = target_fun1(2.4, 3.3);
        }

        endTime = System.nanoTime();

        long funcTime = endTime - startTime;

        long compileTime = -1;
        try {
            String className = "MathExpression";

            String source = "public final class MathExpression implements BiMathFunction {\n"
                    + "public double compute(double x, double y) {\n"
                    + "\treturn Math.sqrt(x*x + y*y);\n" + "}\n}\n";

            Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, source);
            BiMathFunction func = (BiMathFunction)aClass.newInstance();

            for(int i = 0; i < 1000000; i++) {
                double result = func.compute(2.3, 3.4);
            }

            startTime = System.nanoTime();

            for(int i = 0; i < 1000000; i++) {
                double result = func.compute(2.3, 3.4);
            }

            endTime = System.nanoTime();

            compileTime = endTime - startTime;
        }
        catch(Exception e) {
            System.out.println("wtf3");
        }

        System.out.println(evalexTime*1e-9);
        System.out.println(mxTime*1e-9);
        System.out.println(parsiiTime*1e-9);
        System.out.println(compileTime*1e-9);
        System.out.println(funcTime*1e-9);


        System.out.println(1.0*evalexTime/funcTime);
        System.out.println(1.0*mxTime/funcTime);
        System.out.println(1.0*parsiiTime/funcTime);
        System.out.println(1.0*compileTime/funcTime);
    }


}
