package back_propagation;

import java.util.Vector;

public class Training {
    Vector<Double>x;
    Vector<Double> output;

    public Training(Vector<Double> x, Vector<Double> output) {
        this.x = x;
        this.output = output;
    }

    public Training() {
        x=new Vector<>();
        output =new Vector<>();
    }

    public Vector<Double> getX() {
        return x;
    }

    public void setX(Vector<Double> x) {
        this.x = x;
    }

    public Vector<Double> getOutput() {
        return output;
    }

    public void setOutput(Vector<Double> output) {
        this.output = output;
    }
}
