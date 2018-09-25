import org.apache.commons.math3.distribution.NormalDistribution;

public class Gaussian implements GenericDistribution {
    private NormalDistribution d;

    public Gaussian(double[] args) {
        d = new NormalDistribution(args[0], args[1]);
    }

    public double pdf(double[] args)
    {
        return d.density(args[0]);
    }

    public double cdf(double[] args) {
        return d.cumulativeProbability(args[0]);
    }
}