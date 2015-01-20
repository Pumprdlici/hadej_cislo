package icp.algorithm.math;

public class Baseline {

    public static void correct(float[] epoch, int prefix) {

        float baseline = 0;

        for (int i = 0; i < prefix; i++) {
            baseline += epoch[i];
        }

        baseline /= prefix;

        for (int i = 0; i < epoch.length; i++) {
            epoch[i] -= baseline;
        }
    }
}
