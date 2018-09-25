import org.apache.commons.math3.distribution.BinomialDistribution;

public class Binomial implements GenericDistribution {
    private BinomialDistribution d;

    public Binomial(double[] args) {
        d = new BinomialDistribution((int)args[0], args[1]);
    }

    public double pdf(double[] args)
    {
        return d.probability((int)args[0]);
    }

    public double cdf(double[] args) {
        return d.cumulativeProbability((int)args[0]);
    }
}