import org.apache.commons.math3.distribution.ExponentialDistribution;

public class Exponential implements GenericDistribution {
    private ExponentialDistribution d;

    public Exponential(double[] args) {
        d = new ExponentialDistribution(args[0]);
    }

    public double pdf(double[] args)
    {
        return d.density(args[0]);
    }

    public double cdf(double[] args) {
        return d.cumulativeProbability(args[0]);
    }

    public double eventProbability(double[] args) { return pdf(args); };
}