package icp.online.gui;

import java.util.*;
public class ProbabilityComparator implements Comparator<Integer> {
	    private double[] grades;
	    public ProbabilityComparator(double[] arr) {
	        grades = arr;
	    }
	    public int compare(Integer i, Integer j) {
	        return Double.compare(grades[j], grades[i]);
	    }
	}

