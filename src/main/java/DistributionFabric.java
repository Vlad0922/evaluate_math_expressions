
import java.util.HashMap;
import java.util.function.Function;


public class DistributionFabric {
    final static HashMap<String, Function<double[], GenericDistribution>> classMap = createDistroMap();

    public static GenericDistribution generateDistribution(String type, double[] args) {
        Function<double[], GenericDistribution> func = classMap.get(type);

        if(func == null)
        {
            throw(new RuntimeException("Cannot find distribution of type: " + type));
        }
        else
        {
            return func.apply(args);
        }

    }

    private static HashMap<String, Function<double[], GenericDistribution>> createDistroMap() {
        HashMap<String, Function<double[], GenericDistribution>> res = new HashMap<>();

        res.put("Gaussian", Gaussian::new);
        res.put("Exponential", Exponential::new);
        res.put("Poisson", Poisson::new);
        res.put("Binomial", Binomial::new);

        return res;
    }
}
