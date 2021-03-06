import net.openhft.compiler.CompilerUtils;

public class MathFunctionFabric {
    final static String BASE_CLASS_NAME = "MathExpression";
    public static MathExpression generateFunction(String funcName, String function, String arguments) {
        String prepared = prepareExpression(function, arguments);

        String className = funcName + "Expression";

        String source = "import static java.lang.Math.*;\n" +
                "public final class " + className + " implements " + BASE_CLASS_NAME + " {\n"
                + "public double compute(double[] args) {\n"
                + "\treturn " + prepared + ";}\n}\n";

        try {
            Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, source);
            MathExpression expr = (MathExpression) aClass.newInstance();

            return expr;
        } catch(Exception e) {
            return null;
        }
    }

    private static String prepareExpression(String expr, String arguments) {
        String prepared = expr;
        String[] argumentsList = arguments.split(",");

        for(int i = 0; i < argumentsList.length; ++i) {
            prepared = prepared.replace(argumentsList[i], String.format("args[%d]", i));
        }

        return prepared;
    }
}
