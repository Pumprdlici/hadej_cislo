package icp.data.formats;


import icp.data.BufferCreator;
import icp.data.Channel;
import icp.data.Epoch;
import icp.data.Header;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Tøída slouží k testování. Generuje volitelný poèet funkcí sinus s volitelnou frekvencí framù
 * a délkou periody funkce. Dále vytvoøí plnohodnotnou hlavièku, jejíž hodnoty jsou tvoøeny konstantami
 * tøídy. <b>Tuto tøídu použije loader pro naètení fiktivního vstupního souboru právì tehdy, pokud je mu pøedán
 * libovolný soubor s pøíponou <i>generator</i><b>.
 * @author Tomáš Øondík, Jiøí Kuèera
 * @version 12. 11. 2007
 */
public class GeneratorLoader implements DataFormatLoader {

    /**
     * Instance tøídy Header nesoucí hlavièkové informace.
     */
    private Header header;
    //ZACATEK - údaje hlavièky souboru
	/*
     * U atributù a konstant, které nemají na zobrazení prùbìhu generovaného signálu žádný vliv je
     * toto výslovnì uvedeno.
     */
    /**
     * Informace o jednotlivych kanalech.
     */
    private ArrayList<Channel> channels;
    /**
     * Poèet signálù.
     */
    private static final int NUMBER_OF_CHANNELS = 5;
    /**
     * Pocet vzorku celkem.
     */
    private static final long NUMBER_OF_SAMPLES = 32768;
    /**
     * Zvìtšení signálu v funkèních hodnot.
     */
    private static final int VALUES_EXPANSION = 100;
    
    /**
     * Rok poøízení záznamu.
     */
    private static final int YEAR = 0;
    /**
     * Mìsíc poøízení záznamu.
     */
    private static final int MONTH = 0;
    /**
     * Den poøízení záznamu.
     */
    private static final int DAY = 0;
    /**
     * Hodina poøízení záznamu.
     */
    private static final int HOUR = 0;
    /**
     * Minuta poøízení záznamu.
     */
    private static final int MINUTE = 0;
    /**
     * Sekunda poøízení záznamu.
     */
    private static final int SECOND = 0;
    // KONEC - údaje o èase, kdy byl záznam poøízen
    /**
     * Poèet snímkù za sekundu.
     */
    private static final int FREQUENCY = 1000;
    /**
     * Délka periody funkce v sekundách.
     */
    private static final int FUNCTION_PERIOD = 10;
    /**
     * Delka segmentu.
     */
    /*
     * Nemá na zobrazení prùbìhu generovaného signálu žádný vliv
     */
    private static final int SEGMENT_LENGTH = 0;
    /**
     * Èíslo, které pøesnì identifikuje mìøenou osobu (nìjèastìji rodné èíslo).
     */
    /*
     * Nemá na zobrazení prùbìhu generovaného signálu žádný vliv
     */
    private static final String PERSONAL_NUMBER = "112233/4455";
    /**
     * Jméno a pøíjmení (v tomto poøadí) mìøené osoby (pacienta) v jednom øetìzci oddìlené mezerou
     */
    /*
     * Nemá na zobrazení prùbìhu generovaného signálu žádný vliv
     */
    private static final String SUBJECT_NAME = "Jan Novák";
    /**
     * Jméno a pøíjmení lékaøe v jednom øetìzci
     */
    /*
     * Nemá na zobrazení prùbìhu generovaného signálu žádný vliv
     */
    private static final String DOC_NAME = "Josef Trnka";
    /**
     * Délka mìøení. Formát èasu této konstanty není pøesnì specifikován.
     */
    /*
     * Nemá na zobrazení prùbìhu generovaného signálu žádný vliv.
     */
    private static final String LENGTH = ""; // 

    // KONEC - údaje hlavièky souboru
    
    private Random generator;
    
    /**
     * Konstruktor.
     */
    public GeneratorLoader() {
        header = new Header();
        generator = new Random();
    }

    /**
     * Metoda volaná instancí tøídy loader pro naètení vstupního souboru.
     * Její implementace je vynucená implementováním rozhraní DataFormatLoader.
     */
    public Header load(BufferCreator loader) throws IOException {
        try {
            // ZACATEK - vytvoøení informací o kanálech
            channels = new ArrayList<Channel>();

            for (int i = 0; i < NUMBER_OF_CHANNELS; i++) {
                Channel channel = new Channel();
                channel.setFrequency(FREQUENCY);
                channel.setName("channel " + (i + 1));
                
                channels.add(channel);
            }

//            header.setCalendar(new GregorianCalendar(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND));
            header.setDateOfAcquisition(new GregorianCalendar());
            header.setDocName(DOC_NAME);
            header.setChannels(channels);
            header.setNumberOfSamples(NUMBER_OF_SAMPLES);
            header.setPersonalNumber(PERSONAL_NUMBER);
            header.setSamplingInterval(1000000 / FREQUENCY); // FREQUENCY je v sekundách, sampling interval je v milisekundách

            header.setSegmentLength(SEGMENT_LENGTH);
            header.setSubjectName(SUBJECT_NAME);

            //KONEC - vytvoøení informací o kanálech

            //ZACATEK - generování funkèních hodnot signálù
            float[] values = new float[channels.size()];
            double angle = (2 * Math.PI) / FUNCTION_PERIOD;
            double actualAngle = 0;

            for (int i = 0; i < NUMBER_OF_SAMPLES; i++) {
                for (int j = 1; j < values.length - 2; j++) {
                    values[j] = (float) (Math.sin(actualAngle) * Math.sin(actualAngle / 10) * Math.sin(actualAngle / 100) * VALUES_EXPANSION);
                }

                values[0] =  (float) (Math.sin(actualAngle) * Math.sin(actualAngle / 10) * VALUES_EXPANSION);
                values[values.length - 2] = (float) (Math.sin(actualAngle) * Math.sin(actualAngle / (generator.nextInt(10) + 5)) * Math.sin(actualAngle / (generator.nextInt(100) + 50)) * VALUES_EXPANSION);
                values[values.length - 1] = (float) actualAngle / 50;

                //System.out.println(Arrays.toString(values));
                loader.saveFrame(values);
                actualAngle += angle;
            }
            // KONEC - generování funkèních hodnot signálù


            return header;
        } catch (Exception e) {
            // TODO - tohle je spatne
            e.printStackTrace();
            throw new IOException("Chyba v metodì load tøídy Generator.java");
        }
    }


    public ArrayList<Epoch> getEpochs() {
        ArrayList<Epoch> markers = new ArrayList<Epoch>();

        for (long pos = generator.nextInt(3000) + 3000; pos < NUMBER_OF_SAMPLES; pos += generator.nextInt(3000) + 3000) {
            Epoch marker = new Epoch(NUMBER_OF_CHANNELS);
            marker.setChannelNumber(0);
            marker.setDescription("Random marker");
            marker.setLength(1);
            marker.setType("Some type");
            marker.setPosition(pos);
            markers.add(marker);
        }
        
        return markers;
    }
}
