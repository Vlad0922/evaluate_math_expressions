import com.udojava.evalex.*;

import java.io.FileWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

import net.openhft.compiler.CompilerUtils;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.mariuszgromada.math.mxparser.*;

import parsii.eval.*;

import static com.udojava.evalex.Expression.e;
import static java.lang.Math.*;

public class main {
    public static double target_fun1(double a, double b) {
        return sqrt(a*a + b*b);
    }

    public static void main(String[] args) {

//        test_generator();
        test_logical();
//        test_time();
//        generate_csv();
    }

    private static void test_generator() {
        MathExpression expr = MathFunctionFabric.generateFunction("divisionProbability",
                "exp(-pow(1 - GABA, 2))",
                "GABA");

        double[] xData = new double[120];
        double[] yData = new double[120];

        double[] args = new double[1];

        args[0] = -2;

        for(int i = 0; i < 120; ++i) {
            xData[i] = args[0];
            yData[i] = expr.compute(args);

            args[0] += 0.05;
        }

        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);

        // Show it
        new SwingWrapper(chart).displayChart();
    }

    private static void test_logical() {
        LogicalExpression expr = LogicalFunctionFabric.generateFunction("canDivide",
                "(GABA > 0.2) AND (GABA < 0.9)",
                "GABA");

        double[] xData = new double[100];
        double[] yData = new double[100];

        double[] args = new double[1];

        args[0] = 0;

        for(int i = 0; i < 100; ++i) {
            xData[i] = args[0];
            yData[i] = expr.compute(args) ? 1. : 0.;

            args[0] += 0.01;
        }

        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);

        // Show it
        new SwingWrapper(chart).displayChart();
    }


    private static void test_time() {
        final int RUN_NUMBER = 1000;

        com.udojava.evalex.Expression expr_evalex = new com.udojava.evalex.Expression("SQRT(a^2 + b^2)");

        for(int i = 0; i < RUN_NUMBER; ++i) {
            BigDecimal res1 = expr_evalex.with("a", "2.4").and("b", "3.3").eval();
        }

        long startTime = System.nanoTime();

        for(int i = 0; i < RUN_NUMBER; ++i) {
            BigDecimal res1 = expr_evalex.with("a", "2.4").and("b", "3.3").eval();
        }

        long endTime = System.nanoTime();
        long evalexTime = endTime - startTime;

        org.mariuszgromada.math.mxparser.Argument mx_a = new org.mariuszgromada.math.mxparser.Argument("a");
        org.mariuszgromada.math.mxparser.Argument mx_b = new org.mariuszgromada.math.mxparser.Argument("b");
        org.mariuszgromada.math.mxparser.Expression exp_mx = new org.mariuszgromada.math.mxparser.Expression("sqrt(a^2 + b^2)", mx_a, mx_b);

        for(int i = 0; i < RUN_NUMBER; ++i) {
            mx_a.setArgumentValue(2.4);
            mx_b.setArgumentValue(3.3);
            double val = exp_mx.calculate();
        }

        startTime = System.nanoTime();

        for(int i = 0; i < RUN_NUMBER; ++i) {
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

            for(int i = 0; i < RUN_NUMBER; ++i) {
                a.setValue(2.4);
                b.setValue(3.3);
                double res3 = parsii_expr.evaluate();
            }

            startTime = System.nanoTime();

            for(int i = 0; i < RUN_NUMBER; ++i) {
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

        BiMathFunction pure_func = new CustomImplementation();

        for(int i = 0; i < RUN_NUMBER; ++i) {
            double res2 = pure_func.compute(2.4, 3.3);
        }

        startTime = System.nanoTime();

        for(int i = 0; i < RUN_NUMBER; ++i) {
            double res2 = pure_func.compute(2.4, 3.3);
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

            for(int i = 0; i < RUN_NUMBER; i++) {
                double result = func.compute(2.3, 3.4);
            }

            startTime = System.nanoTime();

            for(int i = 0; i < RUN_NUMBER; i++) {
                double result = func.compute(2.3, 3.4);
            }

            endTime = System.nanoTime();

            compileTime = endTime - startTime;
        }
        catch(Exception e) {
            System.out.println("wtf3");
        }

        double evalexAverage = 1.0*evalexTime/RUN_NUMBER;
        double mxAverage = 1.0*mxTime/RUN_NUMBER;
        double parsiiAverage = 1.0*parsiiTime/RUN_NUMBER;
        double compiledAverage = 1.0*compileTime/RUN_NUMBER;
        double pureAverage = 1.0*funcTime/RUN_NUMBER;

        System.out.format("Evalex result: %.5fns\n",    evalexAverage);
        System.out.format("mxParser result: %.5fns\n",  mxAverage);
        System.out.format("Parsii result: %.5fns\n",    parsiiAverage);
        System.out.format("Compiled function result: %.5fns\n", compiledAverage);
        System.out.format("Pure function result: %.5fns\n",     pureAverage);


        System.out.format("Evalex to pure function ratio: %.3f\n",      evalexAverage/pureAverage);
        System.out.format("mxParser to pure function ratio: %.3f\n",    mxAverage/pureAverage);
        System.out.format("Parsii to pure function ratio: %.3f\n",      parsiiAverage/pureAverage);
        System.out.format("compiled function to pure ratio: %.3f\n",    compiledAverage/pureAverage);

    }

    private static void generate_csv() {
        final int RUN_NUMBER = 1000000;

        com.udojava.evalex.Expression expr_evalex = new com.udojava.evalex.Expression("SQRT(a^2 + b^2)");

        for(int i = 0; i < RUN_NUMBER; ++i) {
            BigDecimal res1 = expr_evalex.with("a", "2.4").and("b", "3.3").eval();
        }

        long evalexResults[] = new long[RUN_NUMBER];
        for(int i = 0; i < RUN_NUMBER; ++i) {
            long startTime = System.nanoTime();
            BigDecimal res1 = expr_evalex.with("a", "2.4").and("b", "3.3").eval();
            long endTime = System.nanoTime();
            evalexResults[i] = (endTime - startTime);
        }

        org.mariuszgromada.math.mxparser.Argument mx_a = new org.mariuszgromada.math.mxparser.Argument("a");
        org.mariuszgromada.math.mxparser.Argument mx_b = new org.mariuszgromada.math.mxparser.Argument("b");
        org.mariuszgromada.math.mxparser.Expression exp_mx = new org.mariuszgromada.math.mxparser.Expression("sqrt(a^2 + b^2)", mx_a, mx_b);

        for(int i = 0; i < RUN_NUMBER; ++i) {
            mx_a.setArgumentValue(2.4);
            mx_b.setArgumentValue(3.3);
            double val = exp_mx.calculate();
        }

        long mxResults[] = new long[RUN_NUMBER];
        for(int i = 0; i < RUN_NUMBER; ++i) {
            long startTime = System.nanoTime();
            mx_a.setArgumentValue(2.4);
            mx_b.setArgumentValue(3.3);
            double val = exp_mx.calculate();
            long endTime = System.nanoTime();

            mxResults[i] = endTime - startTime;
        }

        parsii.eval.Scope scope = new parsii.eval.Scope();
        parsii.eval.Variable a = scope.getVariable("a");
        parsii.eval.Variable b = scope.getVariable("b");

        long parsiiResults[] = new long[RUN_NUMBER];

        try {
            parsii.eval.Expression parsii_expr = parsii.eval.Parser.parse("sqrt(a^2 + b^2)", scope);

            for(int i = 0; i < RUN_NUMBER; ++i) {
                a.setValue(2.4);
                b.setValue(3.3);
                double res3 = parsii_expr.evaluate();
            }


            for(int i = 0; i < RUN_NUMBER; ++i) {
                long startTime = System.nanoTime();

                a.setValue(2.4);
                b.setValue(3.3);
                double res3 = parsii_expr.evaluate();

                long endTime = System.nanoTime();

                parsiiResults[i] = endTime - startTime;
            }
        }
        catch(Exception e) {
            System.out.println("wtf");
        }

        for(int i = 0; i < RUN_NUMBER; ++i) {
            double res2 = target_fun1(2.4, 3.3);
        }

        BiMathFunction pure_func = new CustomImplementation();
        long pureResults[] = new long[RUN_NUMBER];
        for(int i = 0; i < RUN_NUMBER; ++i) {
            long startTime = System.nanoTime();

            double res2 = pure_func.compute(2.4, 3.3);

            long endTime = System.nanoTime();
            pureResults[i] = endTime - startTime;
        }


        long compileResults[] = new long[RUN_NUMBER];
        try {
            String className = "MathExpression";

            String source = "public final class MathExpression implements BiMathFunction {\n"
                    + "public double compute(double x, double y) {\n"
                    + "\treturn Math.sqrt(x*x + y*y);\n" + "}\n}\n";

            Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, source);
            BiMathFunction func = (BiMathFunction)aClass.newInstance();

            for(int i = 0; i < RUN_NUMBER; i++) {
                double result = func.compute(2.3, 3.4);
            }

            for(int i = 0; i < RUN_NUMBER; i++) {
                long startTime = System.nanoTime();
                double result = func.compute(2.3, 3.4);

                long endTime = System.nanoTime();

                compileResults[i] = endTime - startTime;
            }
        }
        catch(Exception e) {
            System.out.println("wtf3");
        }

        try {
            FileWriter writer = new FileWriter("test.csv");

            writer.append("time,library\n");

            for(long val : evalexResults) {
                writer.append(Long.toString(val));
                writer.append(',');
                writer.append("evalex\n");
            }

            for(long val : mxResults) {
                writer.append(Long.toString(val));
                writer.append(',');
                writer.append("mxParser\n");
            }

            for(long val : parsiiResults) {
                writer.append(Long.toString(val));
                writer.append(',');
                writer.append("parsii\n");
            }

            for(long val : compileResults) {
                writer.append(Long.toString(val));
                writer.append(',');
                writer.append("compiled\n");
            }

            for(long val : pureResults) {
                writer.append(Long.toString(val));
                writer.append(',');
                writer.append("pure\n");
            }

            writer.flush();
            writer.close();
        }
        catch(Exception e) {
            System.out.println("Error while writing in .csv!");
            e.printStackTrace();
        }
    }
}
