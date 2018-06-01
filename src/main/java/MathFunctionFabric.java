import net.openhft.compiler.CompilerUtils;

public class MathFunctionFabric {
    final static String BASE_CLASS_NAME = "MathExpression";
    public static MathExpression generateFunction(String funcName, String function, String arguments) {
        String prepared = function;

        String[] argumentsList = arguments.split(",");

        for(int i = 0; i < argumentsList.length; ++i) {
            prepared = function.replace(argumentsList[i], String.format("args[%d]", i));
        }

        String className = funcName + "Expression";

        String source = "public final class " + className + " implements " + BASE_CLASS_NAME + " {\n"
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
}
