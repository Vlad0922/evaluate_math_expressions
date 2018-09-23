import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class main {

    public static void main(String[] args) {

//        test_generator();
//        test_logical();
        test_distributions();
    }

    private static void test_distributions() {
        GenericDistribution standardGaussian = DistributionFabric.generateDistribution("Gaussian", new double[]{0., 1.});

        double[] xData = new double[120];
        double[] pdfData = new double[120];
        double[] cdfData = new double[120];

        double[] args = new double[1];

        args[0] = -3;

        for(int i = 0; i < 120; ++i) {
            xData[i] = args[0];
            pdfData[i] = standardGaussian.pdf(args);
            cdfData[i] = standardGaussian.cdf(args);

            args[0] += 0.05;
        }


        XYChart chartPDF = QuickChart.getChart("Sample Chart", "X", "Prob", "PDF", xData, pdfData);

        // Show it
        new SwingWrapper(chartPDF).displayChart();

        XYChart chartCDF = QuickChart.getChart("Sample Chart", "X", "Prob", "CDF", xData, cdfData);

        // Show it
        new SwingWrapper(chartCDF).displayChart();
    }

    private static void test_generator() {
        MathExpression expr = MathFunctionFabric.generateFunction("divisionProbability",
                "exp(-pow(1 - GABA, 2))",
                "GABA");

        double[] xData = new double[120];
        double[] yData = new double[120];

        double[] args = new double[1];

        args[0] = -2;

        for(int i = 0; i < 120; ++i) {
            xData[i] = args[0];
            yData[i] = expr.compute(args);

            args[0] += 0.05;
        }

        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);

        // Show it
        new SwingWrapper(chart).displayChart();
    }

    private static void test_logical() {
        LogicalExpression expr = LogicalFunctionFabric.generateFunction("canDivide",
                "(GABA > 0.2) AND (GABA < 0.9)",
                "GABA");

        double[] xData = new double[100];
        double[] yData = new double[100];

        double[] args = new double[1];

        args[0] = 0;

        for(int i = 0; i < 100; ++i) {
            xData[i] = args[0];
            yData[i] = expr.compute(args) ? 1. : 0.;

            args[0] += 0.01;
        }

        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);

        // Show it
        new SwingWrapper(chart).displayChart();
    }



}
