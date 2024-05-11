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

    @Override
    public int[] getScales()
    {
        return MINOR_SCALE  ;
    }

    @Override
    public List<Integer[]> getChords()
    {
        return CHORDS;
    }

    // Метод для инициализации словаря аккордов
    private static List<Integer[]> initializeChords() {
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
