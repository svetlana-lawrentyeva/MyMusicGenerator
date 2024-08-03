package ivko.lana.entities;

import java.util.List;

/**
 * @author Lana Ivko
 */
public interface IScale
{
    int BASE_NOTE = 60; // Middle C

    String getName();

    int[] getScale();

    int getBaseNote();

    String getRhythmSize();

    int getSoloInstrument();

    List<Integer[]> getChords(int tone);

    // Метод для поиска аккорда, содержащего данную ноту
    default Integer[] findChord(int note, List<Integer[]> chords)
    {
        int index = (int) (Math.random() * chords.size());
        Integer[] result = chords.get(index);
//        for (int i = 0; i < result.length; ++i)
//        {
//            result[i] += (getBaseNote());
//        }
        return result;
    }
}
