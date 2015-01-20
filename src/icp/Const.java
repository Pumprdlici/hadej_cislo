package icp;

import java.awt.Font;

/**
 * Obsahuje konstanty používané v aplikaci.
 */
public class Const {

    /**
     * Koncovka souboru ve formátu VDEF.
     */
    public static final String VHDR_EXTENSION = ".vhdr";
    public static final String VMKR_EXTENSION = ".vmkr";

    //----------------------Settings constants---------------------------
    public static final String DEF_IP_ADDRESS = "147.228.127.95";
    public static final String[] DEF_PORTS = {"51244"};
    public static final int DEF_PORT = 51244;
    public static final String IPADDRESS_PATTERN
            = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    //-------------------------Filter settings-----------------------
    public static final int[] CHANNELS = {1, 2, 3}; /* EEG channels to be transformed to feature vectors */

    public static final int EPOCH_SIZE = 512; /* number of samples to be used - Fs = 1000 Hz expected */

    public static final int DOWN_SMPL_FACTOR = 32;  /* subsampling factor */

    public static final int SKIP_SAMPLES = 0; /* skip initial samples in each epoch */

    /* low pass 0 - 8 Hz, M = 19  */
    public static final double[] lowPassCoeffs = {0.000308, 0.001094, 0.002410,
        0.004271, 0.006582, 0.009132, 0.011624, 0.013726, 0.015131, 0.015625,
        0.015131, 0.013726, 0.011624, 0.009132, 0.006582, 0.004271, 0.002410,
        0.001094, 0.000308};

    //-------------------------MLP classifier------------------------
    public static final int DEFAULT_OUTPUT_NEURONS = 1; /* number of output neurons */

    public static final double LEARNING_RATE = 0.1;     /* learning step */

    public static final int NUMBER_OF_ITERATIONS = 2000;

    //------------------------Classifier training-----------------------
    public static final String TRAINING_RAW_DATA_FILE_NAME = "data/train/set2.eeg";
    public static final String TRAINING_FILE_NAME = "data/classifier.txt";

    //----------------------Epoch------------------------
    public static final int PREEPOCH_VALUES = 100;
    public static final int POSTEPOCH_VALUES = 700;

    //----------------------Buffer-----------------------
    public static final int BUFFER_SIZE = 10000;
    public static final int NUMBER_OF_STIMULUS = 400;

    //----------------------Main window------------------
    public static final String APP_NAME = "Guess the number";

    public static final int MAIN_WINDOW_WIDTH = 640;

    public static final int MAIN_WINDOW_HEIGHT = 320;

    public static final String UNKNOWN_RESULT = "?";

    public static final String RESULT_FONT_NAME = "Arial";

    public static final int RESULT_FONT_SIZE = 250;

    public static final int RESULT_FONT_STYLE = Font.BOLD;

    public static final String[] TABLE_COLUMN_NAMES = {"Number", "Score"};
    
    
    //----------------------Experiment---------------------
    public static final int USED_CHANNELS = 3;
    public static final int GUESSED_NUMBERS = 9;
    
	public static final int SAMPLES_BEFORE_STIMULUS = 100;
    public static final int SAMPLES_AFTER_STIMULUS = 750;
}
