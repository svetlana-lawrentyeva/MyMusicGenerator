package ivko.lana.entities;

/**
 * @author Lana Ivko
 */

public class DurationProbability {
    private int duration;
    private double probability;

    public DurationProbability()
    {}

    public DurationProbability(int duration, double probability) {
        this.duration = duration;
        this.probability = probability;
    }

    // Геттеры и сеттеры
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
