package pt.isec.cubiqua.recognition.model;

public class TupleResultAccuracy {

    private String result;
    private double accuracy;

    public TupleResultAccuracy() {
        this.result = "";
        this.accuracy = 0.0;
    }

    public TupleResultAccuracy(TupleResultAccuracy otherTuple) {
        this.result = otherTuple.getResult();
        this.accuracy = otherTuple.getAccuracy();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}
