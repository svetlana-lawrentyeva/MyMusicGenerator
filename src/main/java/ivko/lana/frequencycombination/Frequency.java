package ivko.lana.frequencycombination;

/**
 * @author Lana Ivko
 */

public class Frequency
{
    private double value;
    private String description;

    public Frequency()
    {
    }

    // Конструктор
    public Frequency(double value, String description)
    {
        this.value = value;
        this.description = description;
    }

    // Геттеры и сеттеры
    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return "Frequency{" +
                "value='" + value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
