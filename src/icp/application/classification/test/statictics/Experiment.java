package icp.application.classification.test.statictics;

/**
 * Created by stebjan on 23.6.2015.
 */
public class Experiment {
    private String institution;
    private String dataset;
    private int age;
    private char gender;
    private int numberThought;
    private int firstGuess;
    private int secondGuess = 0;
    private int thirdGuess = 0;
    private char handedness;
    private String other;


    public Experiment(String institution, String dataset, int age, char gender, int numberThought) {
        this.institution = institution;
        this.dataset = dataset;
        this.age = age;
        this.gender = gender;
        this.numberThought = numberThought;
    }

    public Experiment(String institution, String dataset, int age, char gender, int numberThought, int firstGuess, int secondGuess, int thirdGuess, char handedness, String other) {
        this.institution = institution;
        this.dataset = dataset;
        this.age = age;
        this.gender = gender;
        this.numberThought = numberThought;
        this.firstGuess = firstGuess;
        this.secondGuess = secondGuess;
        this.thirdGuess = thirdGuess;
        this.handedness = handedness;
        this.other = other;
    }

    public Experiment() {
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public int getNumberThought() {
        return numberThought;
    }

    public void setNumberThought(int numberThought) {
        this.numberThought = numberThought;
    }

    public int getFirstGuess() {
        return firstGuess;
    }

    public void setFirstGuess(int firstGuess) {
        this.firstGuess = firstGuess;
    }

    public int getSecondGuess() {
        return secondGuess;
    }

    public void setSecondGuess(int secondGuess) {
        this.secondGuess = secondGuess;
    }

    public int getThirdGuess() {
        return thirdGuess;
    }

    public void setThirdGuess(int thirdGuess) {
        this.thirdGuess = thirdGuess;
    }

    public char getHandedness() {
        return handedness;
    }

    public void setHandedness(char handedness) {
        this.handedness = handedness;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "institution='" + institution + '\'' +
                ", dataset='" + dataset + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", numberThought=" + numberThought +
                ", firstGuess=" + firstGuess +
                ", secondGuess=" + secondGuess +
                ", thirdGuess=" + thirdGuess +
                ", handedness=" + handedness +
                ", other='" + other + '\'' +
                '}';
    }
}
