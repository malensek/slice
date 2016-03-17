package io.sigpipe.sing.stat;

public class SummaryStatistics {

    private long num;
    private double min;
    private double max;
    private double mean;
    private double std;
    private double var;

    public SummaryStatistics(RunningStatistics rs) {
        this.num = rs.n();
        this.min = rs.min();
        this.max = rs.max();
        this.mean = rs.mean();
        this.var = rs.var();
        this.std = rs.std();
    }

    public double num() {
        return this.num;
    }

    public double min() {
        return this.min;
    }

    public double max() {
        return this.max;
    }

    public double mean() {
        return this.mean;
    }

    public double std() {
        return this.std;
    }

    public double var() {
        return this.var;
    }

    @Override
    public String toString() {
        String str = "";
        str += "Number of Samples: " + num + System.lineSeparator();
        str += "Mean: " + mean + System.lineSeparator();
        str += "Variance: " + var() + System.lineSeparator();
        str += "Std Dev: " + std() + System.lineSeparator();
        str += "Min: " + min + System.lineSeparator();
        str += "Max: " + max;
        return str;
    }
}
