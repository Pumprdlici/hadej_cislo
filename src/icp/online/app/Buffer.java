package icp.online.app;

import icp.Const;
import icp.algorithm.math.Baseline;
import java.util.Arrays;
import java.util.LinkedList;

import icp.online.tcpip.objects.RDA_Marker;
import icp.online.tcpip.objects.RDA_MessageData;

/**
 * Název úlohy: Jednoduché BCI Tøída: Buffer
 *
 * @author Bohumil Podlesák První verze vytvoøena: 28.3.2010
 * @version 2.0
 *
 * Tøída, do které se mohou prùbìžnì ukládat datové objekty RDA MessageData.
 * Tyto objekty jsou následnì zpracovány a mohou být vráceny jako pole typu
 * float.
 */
public class Buffer {

    /**
     * Index elektrody FZ. Tato elektroda je v poli zapsána jako šestnáctá v
     * poøadí.
     */
    private int indexFz;
    /**
     * Index elektrody PZ. Tato elektroda je v poli zapsána jako sedmnáctá v
     * poøadí.
     */
    private int indexPz;
    /**
     * Index elektrody FZ. Tato elektroda je v poli zapsána jako osmnáctá v
     * poøadí.
     */
    private int indexCz;

    private int numChannels;

    private float[] dataFZ;
    private float[] dataCZ;
    private float[] dataPZ;

    private int endIndex;
    private int size;
    private final int preMarker;
    private final int postMarker;
    private LinkedList<Integer> indexes;
    private final LinkedList<Integer> stimuli;

    /**
     * Konstruktor Z prvu je nutné zaèínat plnit buffer až od indexu
     * predMarkerem, protože jinak by v pøípadì brzkého pøíchodu markeru bylo
     * nutno vybírat hodnoty mimo rozsah pole. Délka pole hodnot v Bufferu by
     * mìla být mnohem vìtší než predMarkerem + zaMarkerem
     *
     * @param size - poèáteèní délka pole, do kterého Buffer ukládá hodnoty
     * @param preMarker - poèet položek pole, které se budou vybírat pøed
     * markerem
     * @param postMarker - poèet položek pole, které se budou vybírat za
     * markerem
     */
    public Buffer(int size, int preMarker, int postMarker) {
        this.size = size;

        this.dataFZ = new float[size];
        this.dataCZ = new float[size];
        this.dataPZ = new float[size];

        for (int i = 0; i < this.size; i++) {
            this.dataFZ[i] = Float.MAX_VALUE;
            this.dataCZ[i] = Float.MAX_VALUE;
            this.dataPZ[i] = Float.MAX_VALUE;
        }
        this.endIndex = preMarker;
        this.preMarker = preMarker;
        this.postMarker = postMarker;
        this.indexes = new LinkedList<>();
        this.stimuli = new LinkedList<>();
    }

    public int getIndexCz() {
        return indexCz;
    }

    public int getIndexFz() {
        return indexFz;
    }

    public int getIndexPz() {
        return indexPz;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public void setIndexCz(int indexCz) {
        this.indexCz = indexCz;
    }

    public void setIndexFz(int indexFz) {
        this.indexFz = indexFz;
    }

    public void setIndexPz(int indexPz) {
        this.indexPz = indexPz;
    }

    public void setNumChannels(int numChannels) {
        this.numChannels = numChannels;
    }

    private float[] getFz(float[] array) {
        float[] vals = new float[Const.ELECTROD_VALS];
        for (int i = 0; i < Const.ELECTROD_VALS; i++) {
            vals[i] = array[indexFz + numChannels * i];
        }
        return vals;
    }

    private float[] getPz(float[] array) {
        float[] vals = new float[Const.ELECTROD_VALS];
        for (int i = 0; i < Const.ELECTROD_VALS; i++) {
            vals[i] = array[indexPz + numChannels * i];
        }
        return vals;
    }

    private float[] getCz(float[] array) {
        float[] vals = new float[Const.ELECTROD_VALS];
        for (int i = 0; i < Const.ELECTROD_VALS; i++) {
            vals[i] = array[indexCz + numChannels * i];
        }
        return vals;
    }

    /**
     * Zápis dat do bufferu (do pole data) a pøidání markeru do fronty. Pokud by
     * byl buffer pøíliš zaplnìn a nevešly by se do nìj informace o dalším
     * prvku, pak se jeho velikost zdvojnásobí. V ideálním pøípadì se ale bude
     * používat tak, aby jeho zvìtšování nemuselo nastávat.
     *
     * @param data - objekt, obsahující pole dat pro zápis do bufferu
     */
    public void write(RDA_MessageData data) {
        float[] fz = getFz(data.getfData());
        float[] cz = getCz(data.getfData());
        float[] pz = getPz(data.getfData());

        /* pokud zbyva malo mista v bufferu */
        if (this.size - this.endIndex <= fz.length) {
            this.dataFZ = resize(this.dataFZ);//pak zvetsi pole hodnot
            this.dataCZ = resize(this.dataCZ);
            this.dataPZ = resize(this.dataPZ);
        }
        /* write hodnot do pole bufferu */
        System.arraycopy(fz, 0, this.dataFZ, endIndex, fz.length);
        System.arraycopy(cz, 0, this.dataCZ, endIndex, cz.length);
        System.arraycopy(pz, 0, this.dataPZ, endIndex, pz.length);

        RDA_Marker[] markers = data.getMarkers();
        if (markers != null) {
            for (RDA_Marker marker : markers) {
                /* promenna index znaci index prave vybraneho markeru;
                 je to aktualni pozice v poli (neaktualizovany this.konec po nahrani novych dat)
                 + relativni pozice markeru uvnitr datoveho objektu */
                int index = this.endIndex + (int) marker.getnPosition();
                this.indexes.addLast(index);
                /* nutno ulozit zaroven index stimulu do fronty */
                this.stimuli.addLast(Integer.parseInt(marker.getsTypeDesc().substring(11, 13).trim()) - 1);
            }
        }

        this.endIndex += fz.length;
    }

    /**
     * Dvojnásobné zvìtšení stávajícího pole dat. Zároveò dojde k nastavení
     * všech potøebných atributù tøídy (délka, index zaèátku a konce)
     *
     * @return - nové dvojnásobnì zvìtšené pole
     */
    private float[] resize(float[] array) {
        System.out.println("*** VOLANA METODA ZVETSI Z INTANCE BUFFERU ***");
        int newSize = 2 * this.size;
        float[] newData = new float[newSize];
        System.arraycopy(array, 0, newData, 0, this.endIndex);
        for (int i = this.endIndex; i < newSize; i++) {
            newData[i] = Float.MAX_VALUE;
        }
        this.size = newSize;
        return newData;
    }

    /**
     * Výbìr z Bufferu. Pokud není v bufferu žádný marker, pak v nìm jistì
     * nejsou už žádná data, která by mìla nìjakou hodnotu (ve smyslu vybírání
     * pole dat pro Epochu) a metoda vrati null. Pokud neni v bufferu zapsanych
     * dost hodnot za poslednim markerem, metoda take vrati null. Tato metoda
     * bude vybírat hodnoty z bufferu podle markerù postupnì (FIFO).
     *
     * @return - pole floatù o délce (this.pred + this.po), obsahující hodnoty z
     * bufferu kolem posledního markeru plus èíslo Stimulu (0 - 9)
     */
    public EpochDataCarrier get() {
        if (this.indexes.isEmpty()) {
            return null;
        }
        /* pokud neni k vybrani epochy kolem markeru jeste nacteno dostatek hodnot
         (moznost navraceni Float.MAX_VALUE nebo prekroceni delky pole) */
        if (this.indexes.peek() + this.postMarker > this.endIndex) {
            /* pak se take vrati null */
            return null;
        }

        float[] fz = new float[this.preMarker + this.postMarker];
        float[] cz = new float[fz.length];
        float[] pz = new float[fz.length];

        /* index markeru minus pocet hodnot pred markerem je indexem prvni polozky, kterou vybereme */
        /* nutno kvuli zaznamenani, na ktery stimul byla tato reakce zaznamenana */
        int waveType = this.stimuli.removeFirst();
        int index = this.indexes.removeFirst() - this.preMarker;
        for (int i = 0; i < (this.preMarker + this.postMarker); i++) {
            fz[i] = this.dataFZ[index + i];
            cz[i] = this.dataCZ[index + i];
            pz[i] = this.dataPZ[index + i];
        }

        Baseline.correct(fz, this.preMarker);
        Baseline.correct(cz, this.preMarker);
        Baseline.correct(pz, this.preMarker);

        float[] baselineFZ = Arrays.copyOfRange(fz, this.preMarker, fz.length);
        float[] baselineCZ = Arrays.copyOfRange(cz, this.preMarker, cz.length);
        float[] baselinePZ = Arrays.copyOfRange(pz, this.preMarker, pz.length);

        return new EpochDataCarrier(baselineFZ, baselineCZ, baselinePZ, waveType);
    }

    /**
     * Metoda pro kontrolu zaplnìnosti bufferu. Slouži k tomu, aby tøída, která
     * s ním bude pracovat vìdela, kdy má zaèít vybírat prvky
     *
     * @return - true, když je buffer plný (zbývá v nìm ménì místa než REZERVA)
     */
    public boolean isFull() {
        return (this.size - this.endIndex <= Const.RESERVE);
    }

    /**
     * Uvolní buffer. Doporuèeno provádìt pokaždé, když se vyberou všechny
     * hodnoty, ktere jdou pomoci metody get(). Tato metoda nastaví položky pole
     * float[] data na Float.MAX_VALUE. Nebudou však vymazány všechy položky,
     * ponechá se nìkolik položek pøed koncem (this.konec) toto je z dùvodu, aby
     * v pøípadì brzkého pøíchodu markeru mohl tento odkazovat na staré položky.
     * Marker oznaèuje pozici, pøed kterou se berou data v metodì get(). Je
     * nutno pro nejhorší pøípad (marker pøijde hned v prvním objektu) ponechat
     * nejménì this.predMarkerem starých hodnot. Pokud v bufferu zustal nejaky
     * marker, tak se musi ponechat vice polozek. V takovem pripade se v
     * promazanem bufferu musi na pocatku objevit this.predMarkerem hodnot pred
     * indexem markeru ze stareho bufferu a zaroven i vsechna data po nem. Toto
     * se provede z duvodu, aby dalsi prichozi data mohla navazat na minula data
     * a neztratil se tak zbytecne jeden blok. Z tohoto duvodu vyplyva, ze tato
     * metoda vymaze pouze minimum polozek, pokud se vola velmi casto,
     * respektive kdyz je fronta markeru skoro plna (probihalo malo vyberu).
     */
    public void clear() {
        /* indexPredMarkerem znaci pozici v poli, kde byl nalezen marker, ktery je na rade ve fronte,
         minus pocet hodnot pred timto markerem, ktere se musi zachovat;
         jinymi slovy, je to index prvniho nevraceneho datoveho bloku (vetsinou
         nekompletniho), ktery se pouzije jako pole hodnot pro konstrukci epochy */
        int preMarkerIndex;
        if (this.indexes.peekFirst() == null) {
            /* pokud zadny marker ve fronte neni, bude se brat nejhorsi pripad a to ten, ze
             marker prijde prave nasledujici polozku po posledni hodnote (this.data[this.konec])*/
            preMarkerIndex = this.endIndex - this.preMarker;
        } else {
            /* jinak standardne this.predMarkerem hodnot pred markerem */
            preMarkerIndex = this.indexes.peek() - this.preMarker;
        }

        /* this.konec minus index je delka bloku (nebo vice bloku dohromady) */
        for (int i = 0; i < this.endIndex - preMarkerIndex; i++) {
            /* tento se prekopiruje na pocatek pole, pouzitim in-place algoritmu */
            this.dataFZ[i] = this.dataFZ[preMarkerIndex + i];
            this.dataCZ[i] = this.dataCZ[preMarkerIndex + i];
            this.dataPZ[i] = this.dataPZ[preMarkerIndex + i];
        }
        /* zbytek pole se vymaze - nastavi se hodnota Float.MAX_VALUE */
        for (int i = this.endIndex - preMarkerIndex; i < this.size; i++) {
            this.dataFZ[i] = Float.MAX_VALUE;
            this.dataCZ[i] = Float.MAX_VALUE;
            this.dataPZ[i] = Float.MAX_VALUE;
        }

        /* pokud zustaly indexy markeru ve fronte indexu, musi se prepsat na nove hodnoty */
        LinkedList<Integer> newIndexes = new LinkedList<>();
        while (!this.indexes.isEmpty()) {
            /* všechny indexy markerù z fronty se musí pøepsat -> odeèíst od nich
             indexPredMarkerem; tím se všechny posunou na zaèátek  */
            int indexMarkeru = this.indexes.removeFirst() - preMarkerIndex;
            newIndexes.add(indexMarkeru);
        }
        this.indexes = newIndexes;

        /* novy konec bude nyni o index prvniho nezpracovaneho datoveho bloku mensi
         lze si to predstavit, jako ze zadne polozky pole pred timto blokem jiz
         neexistuji, a tak se tento blok zacne cislovat od nuly */
        this.endIndex = this.endIndex - preMarkerIndex;
    }

    public int getIndexesCount() {
        return this.indexes.size();
    }

    public int getStimulusCount() {
        return this.stimuli.size();
    }
}
