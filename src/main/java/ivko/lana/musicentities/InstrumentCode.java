package ivko.lana.musicentities;

/**
 * @author Lana Ivko
 */
// class for individual instrument codes
public class InstrumentCode
{
    private int code;
    private String name;
    private boolean meditative;

    // Getters and setters
    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isMeditative()
    {
        return meditative;
    }

    public void setMeditative(boolean meditative)
    {
        this.meditative = meditative;
    }
}
