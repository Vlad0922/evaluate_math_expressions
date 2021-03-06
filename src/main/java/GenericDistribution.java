public interface GenericDistribution {
    double pdf(double[] args);
    double cdf(double[] args);
    double eventProbability(double[] args);
}
