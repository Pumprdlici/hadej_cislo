package icp.application.classification.test.statictics;

import java.io.*;
import java.util.*;

/**
 * Created by stebjan on 23.6.2015.
 */
public class StatisticsComputer {



    public static void main(String[] args) {
        StatisticsComputer stat = new StatisticsComputer();
        List<Experiment> list = stat.parseCSV("C:\\studium\\pre-seed\\guessTheNumber.csv");
        stat.printAgeHistogram(list);
        stat.printGenderHistogram(list);

    }

    public void printAgeHistogram(List<Experiment> list) {
        int minAge = Integer.MAX_VALUE;
        int maxAge = 0;
        for (Experiment ex: list) {
            if (ex.getAge() > 0 && ex.getAge() > maxAge) {
                maxAge = ex.getAge();
            }
            if (ex.getAge() > 0 && ex.getAge() < minAge) {
                minAge = ex.getAge();
            }
        }
        int[] histogram = new int[maxAge - minAge + 1];
        Arrays.fill(histogram, 0);
        for (Experiment ex: list) {
            if (ex.getAge() > 0) {
                histogram[ex.getAge() - minAge]++;
            }
        }
        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] > 0) {
                System.out.println("age: " + (i + minAge) + " - " + histogram[i] + "x");
            }
        }

    }

    public void printGenderHistogram(List<Experiment> list) {
        int male = 0;
        int female = 0;
        for (Experiment ex: list) {
            if (ex.getGender() == 'M') {
                male++;
            } else {
                female++;
            }
        }
        System.out.println("male: " + male + ", female: " + female);

    }

    public List<Experiment> parseCSV(String path) {
        List<Experiment> list = new ArrayList<>();
        File file = new File(path);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                int age;
                int numberThought = Integer.parseInt(parts[4]);
                int firstGuess = Integer.parseInt(parts[5]);
                int secondGuess;
                int thirdGuess;
                char handedness = 'U';
                String other = null;
                if (parts.length > 9) {
                    other = parts[9];
                }
                if (parts.length > 8 && parts[8].length() < 0) {
                    handedness = parts[8].charAt(0);
                }
                try {
                    age = Integer.parseInt(parts[2]);
                } catch (Exception ex) {
                    age = 0;
                }
                try {
                    secondGuess = Integer.parseInt(parts[6]);
                } catch (Exception ex) {
                    secondGuess = 0;
                }
                try {
                    thirdGuess = Integer.parseInt(parts[7]);
                } catch (Exception ex) {
                    thirdGuess = 0;
                }
                Experiment exp = new Experiment(parts[0], parts[1], age, parts[3].charAt(0), numberThought, firstGuess, secondGuess,
                        thirdGuess, handedness, other);
                list.add(exp);

            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;

    }
}
