package ivko.lana.entities;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lana Ivko
 */
public interface IScale
{
    int BASE_NOTE = 60; // Middle C
    int[] getScales();

    List<Integer[]> getChords();

    // Метод для поиска аккорда, содержащего данную ноту
    default int[] findChord(int note, List<Integer[]> chords)
    {
        int[] result = Arrays.stream(chords.get(0)).mapToInt(Integer::intValue).toArray();
        for (Integer[] chord : chords)
        {
            if (Arrays.asList(chord).contains(note))
            {
                result = Arrays.stream(chord).mapToInt(Integer::intValue).toArray();
            }
        }
        for (int i = 0; i < result.length; ++i)
        {
            result[i] += (BASE_NOTE);
        }
        return result;
    }
}
