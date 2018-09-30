import javafx.util.Pair;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class main {

    private static double clamp(double x, double l, double r) {
        return Math.max(l, Math.min(x, r));
    }

    public static void main(String[] args) {

//        test_generator();
//        test_logical();
//        test_distributions();
        simulateExperiment();
    }

    private static void simulateExperiment() {
        boolean simulateSpiking = false;
        boolean simulateDivision = true;

        if(simulateSpiking) {
            GenericDistribution d = DistributionFabric.generateDistribution("Pascal", new double[]{10, 0.5});

            int ticksTotal = 1000;

            int spikeCounter = 0;
            double[] args = new double[1];
            double[] spikeData = new double[ticksTotal];
            double[] ticksData = new double[ticksTotal];

            for(int i = 0; i < ticksTotal; ++i) {
                args[0] = spikeCounter;
                double spikeProb = d.cdf(args);
                if(spikeProb >= Math.random()) {
                    spikeData[i] = 1;
                    spikeCounter = 0;
                }
                else
                {
                    spikeData[i] = 0;
                    spikeCounter += 1;
                }
                args[0] = spikeCounter;
                ticksData[i] = i;
            }

            XYChart chart = QuickChart.getChart("Sample Chart", "X", "Prob", "CDF", ticksData, spikeData);
            new SwingWrapper(chart).displayChart("Spiking plot");
        }

        if(simulateDivision) {
            GenericDistribution d = DistributionFabric.generateDistribution("Gaussian", new double[]{0.6, 0.1});

            int ticksTotal = 1000;

            double currConcentration = 0.4;
            double[] args = new double[1];
            double[] divisionData = new double[ticksTotal];
            double[] cellCounter = new double[ticksTotal];
            double[] concentrations = new double[ticksTotal];
            double[] ticksData = new double[ticksTotal];
            double[] cellDivProbs = new double[ticksTotal];

            int totalDivisions = 0;

            for(int i = 0; i < ticksTotal; ++i) {
                args[0] = currConcentration;
                double spikeProb = d.eventProbability(args);

                int doDivision = spikeProb >= Math.random() ? 1 : 0;

                totalDivisions += doDivision;
                divisionData[i] = doDivision;
                cellCounter[i] = totalDivisions;
                concentrations[i] = currConcentration;
                cellDivProbs[i] = spikeProb;
                ticksData[i] = i;

                currConcentration += (Math.random() - 0.5)/10;
                currConcentration = clamp(currConcentration, 0, 1);
            }


            double[] xVals = new double[100];
            double[] probVals = new double[100];

            for(int i = 0; i < 100; ++i) {
                args[0] = 1.0*i/100;
                xVals[i] = 1.0*i/100;
                probVals[i] = d.eventProbability(args);
            }

            List<XYChart> chartList = new ArrayList<>();
            chartList.add(QuickChart.getChart("Concentration chart", "X", "Concentration", "CDF", ticksData, concentrations));
            chartList.add(QuickChart.getChart("Division probability chart", "X", "Division probability", "CDF", ticksData, cellDivProbs));
            chartList.add(QuickChart.getChart("Division event chart", "X", "Division event", "CDF", ticksData, divisionData));
            chartList.add(QuickChart.getChart("Cell count chart", "X", "Cell count", "PDF", ticksData, cellCounter));
            chartList.add(QuickChart.getChart("Division probability", "X", "Probability", "CDF", xVals, probVals));

            // Show it
            new SwingWrapper(chartList).displayChartMatrix();
        }
    }

    private static void test_distributions() {
        double[] args = new double[1];

        List<Pair<String, GenericDistribution>> distroList = new ArrayList<>();
        distroList.add(new Pair<>("Gaussian", DistributionFabric.generateDistribution("Gaussian", new double[]{0.6, 0.1})));
        distroList.add(new Pair<>("Exponential", DistributionFabric.generateDistribution("Exponential", new double[]{1.5})));
        distroList.add(new Pair<>("Poisson", DistributionFabric.generateDistribution("Poisson", new double[]{0.5})));
        distroList.add(new Pair<>("Binomial", DistributionFabric.generateDistribution("Binomial", new double[]{3., 0.25})));


        // data is saved as reference
        // so I have to instanitiate array for each of the distributions
        double[][] xData = new double[distroList.size()][];
        double[][] pdfData = new double[distroList.size()][];
        double[][] cdfData = new double[distroList.size()][];

        for(int i = 0; i < distroList.size(); ++i) {
            xData[i] = new double[120];
            pdfData[i] = new double[120];
            cdfData[i] = new double[120];
        }


        for(int i = 0; i < distroList.size(); ++i) {
            String name = distroList.get(i).getKey();
            GenericDistribution d = distroList.get(i).getValue();

            args[0] = 0;

            for (int j = 0; j < 120; ++j) {
                xData[i][j] = args[0];
                pdfData[i][j] = d.pdf(args);
                cdfData[i][j] = d.cdf(args);

                args[0] += 0.05;
            }

            List<XYChart> chartList = new ArrayList<>();
            chartList.add(QuickChart.getChart("Sample Chart", "X", "Prob", "PDF", xData[i], pdfData[i]));
            chartList.add(QuickChart.getChart("Sample Chart", "X", "Prob", "CDF", xData[i], cdfData[i]));

            // Show it
            new SwingWrapper(chartList).displayChartMatrix(name);
        }


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
