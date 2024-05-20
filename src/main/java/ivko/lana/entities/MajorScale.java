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
    public int[] getScale()
    {
        return MAJOR_SCALE;
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

//    // Метод для инициализации словаря аккордов
//    private static List<Integer[]> initializeChords() {
//        List<Integer[]> chords = new ArrayList<>();
//        chords.add(new Integer[]{0, 4, 7, 4});
//        chords.add(new Integer[]{2, 5, 9, 5});
//        chords.add(new Integer[]{4, 7, 11, 7});
//        chords.add(new Integer[]{5, 9, 0, 9});
//        chords.add(new Integer[]{7, 11, 2, 11});
//        chords.add(new Integer[]{9, 0, 4, 0});
//        chords.add(new Integer[]{11, 2, 5, 2});
//        return chords;
//    }



   //  Метод для инициализации словаря аккордов
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
