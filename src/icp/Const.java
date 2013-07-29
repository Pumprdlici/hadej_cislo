package icp;

import java.awt.*;

/**
 * Obsahuje konstanty používané v aplikaci.
 */
public class Const {

    /**
     * Název aplikace.
     */
    public static final String APP_NAME = "ERP Calculator";
    
    /**
     * Koncovka souboru ve formátu ascii.
     */
    public static final String ASCII_FILE_EXTENSION = "asc";
    
    /**
     * Koncovka souboru ve formátu edf.
     */
    public static final String EDF_FILE_EXTENSION = "rec";
    
    /**
     * Koncovka souboru ve formátu kiv edf.
     */
    public static final String EDF_FILE_EXTENSION2 = "edf";
    
    /**
     * Koncovka souboru ve formátu kiv.
     */
    public static final String KIV_FILE_EXTENSION = "xml";
    
    /**
     * Koncovka souboru projektu.
     */
    public static final String PROJECT_FILE_EXTENSION = "esp";
    
    /**
     * Koncovka souboru ve formátu BrainStudio.
     */
    public static final String KIV_ASCII = "-ascii";
    
    /**
     * Koncovka souboru ve formátu BrainStudio.
     */
    public static final String KIV_BINARY = "-binary";
    
    /**
     * Koncovka sloužící k pseudo generátoru signálu.
     */
    public static final String GENERATOR_EXTENSION = "generator";

    /**
     * Koncovka souboru ve formátu VDEF.
     */
    public static final String VHDR_EXTENSION = "vhdr";

    /**
     * Verze EDF formatu.
     */
    public static final int EDF_VERSION = 0;
    
    
    public static final Color[] DC_SIGNALS_COLORS = {Color.BLACK, Color.BLUE, Color.DARK_GRAY};
    
    public static final Color DC_PLAYBACK_POINTER_COLOR = Color.BLUE;
    
    public static final Color DC_GRID_COLOR = Color.GRAY;
    
    public static final Color DC_BACKGROUND_COLOR = Color.WHITE;

    public static final String DC_GRID_FONT_FAMILY = "SansSerif";
    public static final int DC_GRID_FONT_STYLE = Font.PLAIN;
    public static final int DC_GRID_FONT_SIZE = 12;
    public static final Color DC_GRID_FONT_COLOR = Color.RED;

    
    public static final Color SW_COLOR_CHECKBOX_PANEL = Color.LIGHT_GRAY;
    public static final Color SW_COLOR_DC_PANEL= Color.WHITE;
    
    
    
    
    /**
     * Šíøka hlavního okna.
     */
    public static final int MAIN_WINDOW_WIDTH = 1024;

    /**
     * Výška hlavního okna.
     */
    public static final int MAIN_WINDOW_HEIGHT = 400;
    
    /**
     * Urèuje možné intervaly vykreslované souøadnicové møížky v kreslicí kompoentì (v milisekundách).
     */
    public static final int[] DC_GRID_RANGE_TIMES = {50, 100, 250, 500, 1000, 2000, 5000, 10000};

    /**
     * Optimální vzdálenost mezi èarami souøadnicové møížky v kreslicí komponentì (v pixelech).
     */
    public static final int DC_OPTIMAL_GRID_RANGE = 100;
    /**
	 * Posunutí nulového indexu pøi procházení epochami. Pøi nastavení na "0" bude mít první epocha index "0", pøi 
	 * nastavení na "1" bude mít první epocha index "1" atd. Pro správnou funkènost je nutné, aby hodnota této konstanty 
	 * byla vždy kladné èíslo.
	 */
    public static final int ZERO_INDEX_SHIFT = 1;
    
    public static final int MS_IN_MIN = 60000;
    
    public static final int MS_IN_SEC = 1000;
    
    /**
     * Konstanty pro výbìr funkce, kterou chce uživatel využít pøi práci se signály.
     */
    public static final int SELECT_NOTHING = -1;
    public static final int SELECT_EPOCH = 0;
    public static final int UNSELECT_EPOCH = 1;
    public static final int SELECT_ARTEFACT = 2;
    public static final int UNSELECT_ARTEFACT = 3;
    public static final int SELECT_PLAYBACK = 4;
    public static final int BASELINE_CORRECTION = 5;
    
    public static final int DWT = 0;
    public static final int CWT = 1;
    
    public static final int PROGRESS_MAX = 100;
    
    public static final int COLOR_COUNT = 64;
    public static final int P300_LENGTH = 256;
    public static final int ROWS_COLS_COUNT_OF_ELEMENT = 8;
    
    /**
     * Možné polarity amplitud pøi detekci evokovaných potenciálù.
     */
    public static final int BOTH_POLARITY = 0;
    public static final int N_POLARITY = 1;
    public static final int P_POLARITY = 2;
    
    
    /**
     * Parametry signalu
     *  
     */
    public static final int NUMBER_OF_CHANNELS = 19;
}
