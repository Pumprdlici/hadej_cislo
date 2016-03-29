package icp.online.gui;

import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Chart extends JFrame {

    private XYDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    public Chart(final String title) {
        super(title);
        dataset = null;
        chart = createChart(dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    /**
     * Creates a chart.
     *
     * @param dataset the data for the chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart(XYDataset dataset) {

        // create the chart...
        final JFreeChart chartComponent = ChartFactory.createTimeSeriesChart(
                "Pz channel average", // chart title
                "Sample [ms]", // x axis label
                "Value  [microV]", // y axis label
                dataset, // data
                //PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chartComponent.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chartComponent.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chartComponent;
    }

    public void update(double[][] pzAvg) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < pzAvg.length; i++) {
            final XYSeries series = new XYSeries("Number " + (i + 1));
            for (int j = 0; j < pzAvg[i].length; j++) {
                series.add(j, pzAvg[i][j]);

            }
            dataset.addSeries(series);
        }
        chart = createChart(dataset);
        chartPanel.setChart(chart);
        chartPanel.repaint();

        chart.fireChartChanged();
    }
    
    public void update(double[] pzAvg) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        final XYSeries series = new XYSeries("");
        for (int i = 0; i < pzAvg.length; i++) {
                series.add(i, pzAvg[i]);

        }
        dataset.addSeries(series);
        
        chart = createChart(dataset);
        chartPanel.setChart(chart);
        chartPanel.repaint();

        chart.fireChartChanged();
    }
}
