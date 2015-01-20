package icp.online.gui;

import java.util.Comparator;

public class ProbabilityComparator implements Comparator<Integer> {

    private final double[] grades;

    public ProbabilityComparator(double[] arr) {
        grades = arr;
    }

    @Override
    public int compare(Integer i, Integer j) {
        return Double.compare(grades[j], grades[i]);
    }
}
