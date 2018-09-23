public class Gaussian implements GenericDistribution {
    private double mu, sigma;

    public Gaussian(double m, double s) {
        mu = m;
        sigma = s;
    }

    // return pdf(x) = standard Gaussian pdf
    private static double pdfImpl(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }

    // return pdf(x, mu, signma) = Gaussian pdf with mean mu and stddev sigma
    private static double pdfImpl(double x, double mu, double sigma) {
        return pdfImpl((x - mu) / sigma) / sigma;
    }

    public double pdf(double[] args)
    {
        return pdfImpl(args[0], mu, sigma);
    }

    // return cdf(z) = standard Gaussian cdf using Taylor approximation
    private static double cdfImpl(double z) {
        if (z < -8.0)
        {
            return 0.0;
        }
        if (z >  8.0) {
            return 1.0;
        }

        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum  = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * pdfImpl(z);
    }

    // return cdf(z, mu, sigma) = Gaussian cdf with mean mu and stddev sigma
    private static double cdfImpl(double z, double mu, double sigma) {
        return cdfImpl((z - mu) / sigma);
    }

    public double cdf(double[] args) {
        return cdfImpl(args[0], mu, sigma);
    }
}