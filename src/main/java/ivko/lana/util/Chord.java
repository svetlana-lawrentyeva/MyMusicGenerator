package ivko.lana.util;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class Chord
{
    private String name;
    private List<Integer> notes;

    public Chord()
    {
    }

    public Chord(String name, List<Integer> notes)
    {
        this.name = name;
        this.notes = notes;
    }

    public String getName()
    {
        return name;
    }

    public List<Integer> getNotes()
    {
        return notes;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setNotes(List<Integer> notes)
    {
        this.notes = notes;
    }

    @Override
    public String toString()
    {
        return "Chord{name='" + name + "', notes=" + notes + '}';
    }
}
