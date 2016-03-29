package icp.online.app;

public class EpochDataCarrier {

    private final float[] fzValues;
    private final float[] czValues;
    private final float[] pzValues;
    private final int stimulusType;

    public EpochDataCarrier(float[] valsFz, float[] valsCz, float[] valsPz, int stimulusType) {
        this.fzValues = valsFz;
        this.czValues = valsCz;
        this.pzValues = valsPz;
        this.stimulusType = stimulusType;
    }

    public float[] getCzValues() {
        return czValues;
    }

    public float[] getFzValues() {
        return fzValues;
    }

    public float[] getPzValues() {
        return pzValues;
    }

    public int getStimulusType() {
        return stimulusType;
    }
}
