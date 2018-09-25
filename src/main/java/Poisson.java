import org.apache.commons.math3.distribution.PoissonDistribution;

public class Poisson implements GenericDistribution {
    private PoissonDistribution d;

    public Poisson(double[] args) {
        d = new PoissonDistribution(args[0]);
    }

    public double pdf(double[] args)
    {
        return d.probability((int)args[0]);
    }

    public double cdf(double[] args) {
        return d.cumulativeProbability((int)args[0]);
    }
}