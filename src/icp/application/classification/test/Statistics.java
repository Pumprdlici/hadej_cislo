package icp.application.classification.test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stebjan on 20.1.2015.
 */
public class Statistics {

    private int expectedResult;
    private int thoughtResult;
    private int pts = 0;
    private int rank;
    private static int totalPts = 0;
    public static final int MAX_POINT = 10;

    private Map<Integer, Integer> pointsForRank;

    public Statistics() {
        pointsForRank = new HashMap<Integer, Integer>();
        pointsForRank.put(1, MAX_POINT);
        pointsForRank.put(2, 5);
        pointsForRank.put(3, 1);
    }

    public int getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(int expectedResult) {
        this.expectedResult = expectedResult;
    }

    public int getThoughtResult() {
        return thoughtResult;
    }

    public void setThoughtResult(int thoughtResult) {
        this.thoughtResult = thoughtResult;
    }

    public int getPts() {
        return pts;
    }



    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
        if (rank <= 3) {
            pts = pointsForRank.get(rank);
            Statistics.setTotalPts(pts);
        }
    }
    public static int getTotalPts() {
        return totalPts;
    }

    public static void setTotalPts(int totalPts) {
        Statistics.totalPts += totalPts;
    }


    @Override
    public String toString() {
        return "I think: " + thoughtResult + " ,expected result is: " + expectedResult + "\n" +
                "Rank: " + rank + "\nGet points: " + pts;
    }
}
