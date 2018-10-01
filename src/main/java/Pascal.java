import org.apache.commons.math3.distribution.PascalDistribution;

public class Pascal implements GenericDistribution {
    private PascalDistribution d;

    public Pascal(double[] args) {
        d = new PascalDistribution((int)args[0], args[1]);
    }

    public double pdf(double[] args)
    {
        return d.probability((int)args[0]);
    }

    public double cdf(double[] args) {
        return d.cumulativeProbability((int)args[0]);
    }

    public double eventProbability(double[] args) { return pdf(args); }
}