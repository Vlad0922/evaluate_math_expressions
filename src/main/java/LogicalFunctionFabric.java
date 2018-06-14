import net.openhft.compiler.CompilerUtils;

import java.util.HashMap;
import java.util.Map;

public class LogicalFunctionFabric {
    final static String BASE_CLASS_NAME = "LogicalExpression";

    final static HashMap<String, String> operatorMap = createOperatorMap();

    public static LogicalExpression generateFunction(String funcName, String function, String arguments) {
        String prepared = prepareExpression(function, arguments);

        String className = funcName + "Expression";

        String source = "import static java.lang.Math.*;\n" +
                "public final class " + className + " implements " + BASE_CLASS_NAME + " {\n"
                + "public boolean compute(double[] args) {\n"
                + "\treturn " + prepared + ";}\n}\n";

        try {
            Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, source);
            LogicalExpression expr = (LogicalExpression) aClass.newInstance();

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

        for(Map.Entry<String, String> entry : operatorMap.entrySet()) {
            prepared = prepared.replace(entry.getKey(), entry.getValue());
        }

        return prepared;
    }

    private static HashMap<String, String> createOperatorMap() {
        HashMap<String, String> res = new HashMap<>();

        res.put("AND", "&&");
        res.put("OR", "||");

        return res;
    }
}
