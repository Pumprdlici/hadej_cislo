package icp.algorithm.math;

public class SignalProcessing {

    public static double[] decimate(double[] inputSignal, int factor) {
        int decimatedLength = inputSignal.length / factor;
        double[] output = new double[decimatedLength];
        for (int i = 0; i < decimatedLength; i++) {
            output[i] = inputSignal[i * factor];
        }
        return output;
    }

    public static double[] normalize(double[] features) {
        double size = getSizeOfVector(features);
        for (int i = 0; i < features.length; i++) {
            features[i] = features[i] / size;
        }
        return features;
    }

    private static double getSizeOfVector(double[] features) {
        double size = 0;
        for (int i = 0; i < features.length; i++) {
            size += Math.pow(features[i], 2);
        }
        return Math.sqrt(size);
    }
}
