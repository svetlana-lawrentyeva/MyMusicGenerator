package ivko.lana.entities;

import java.util.*;

/**
 * @author Lana Ivko
 */
public class MajorScale implements IScale
{
    private static final int[] MAJOR_SCALE = {0, 2, 4, 5, 7, 9, 11}; // C major scale intervals

    private static final List<Integer[]> CHORDS = initializeChords();
    @Override
    public int[] getScales()
    {
        return MAJOR_SCALE;
    }

    @Override
    public List<Integer[]> getChords()
    {
        return CHORDS;
    }

    // Метод для инициализации словаря аккордов
    private static List<Integer[]> initializeChords() {
        List<Integer[]> chords = new ArrayList<>();
        chords.add(new Integer[]{0, 4, 7});
        chords.add(new Integer[]{2, 5, 9});
        chords.add(new Integer[]{4, 7, 11});
        chords.add(new Integer[]{5, 9, 0});
        chords.add(new Integer[]{7, 11, 2});
        chords.add(new Integer[]{9, 0, 4});
        chords.add(new Integer[]{11, 2, 5});
        return chords;
    }

}
