package ivko.lana.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class MinorScale implements IScale
{
    private static final int[] MINOR_SCALE = {0, 2, 3, 5, 7, 8, 10}; // /C minor scale intervals

    private static final List<Integer[]> CHORDS = initializeChords();
    public static final String SCALE_NAME = "Simple minor scale";

    @Override
    public String getName()
    {
        return SCALE_NAME;
    }

    @Override
    public int[] getScale()
    {
        return MINOR_SCALE;
    }

    @Override
    public int getBaseNote()
    {
        return 0;
    }

    @Override
    public String getRhythmSize()
    {
        return null;
    }

    @Override
    public int getSoloInstrument()
    {
        return 0;
    }

    @Override
    public List<Integer[]> getChords(int tone)
    {
        return CHORDS;
    }

    @Override
    public Integer[] findChord(int note, List<Integer[]> chords)
    {
        return IScale.super.findChord(note, chords);
    }

    // Метод для инициализации словаря аккордов
    private static List<Integer[]> initializeChords()
    {
        List<Integer[]> chords = new ArrayList<>();
        chords.add(new Integer[]{0, 3, 7});
        chords.add(new Integer[]{2, 5, 8});
        chords.add(new Integer[]{3, 7, 10});
        chords.add(new Integer[]{5, 8, 0});
        chords.add(new Integer[]{7, 10, 2});
        chords.add(new Integer[]{8, 0, 3});
        chords.add(new Integer[]{10, 2, 5});
        return chords;
    }
}
