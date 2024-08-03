package ivko.lana.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class ChordCommonSequences
{
    public static final String FILE_NAME = "chordFinder/chordSequence.csv";

    private static List<List<Chord>> Sequences_;

    private List<List<Chord>> get() throws IOException
    {
        if (Sequences_ == null)
        {
            Sequences_ = loadSequence();
        }
        return Sequences_;
    }

    private List<List<Chord>> loadSequence() throws IOException
    {
        List<List<Chord>> sequence = new ArrayList<>();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
        InputStreamReader reader = new InputStreamReader(inputStream);
        CSVParser parser = CSVFormat.DEFAULT.parse(reader);

        for (CSVRecord record : parser)
        {
            Iterator<String> iterator = record.stream().iterator();
            List<Chord> chords = new ArrayList<>();
            while (iterator.hasNext())
            {
                String chordCode = iterator.next();
                chords.add(MusicUtil.getInstance().getChordLibrary().getChord(chordCode));
            }
            sequence.add(chords);
        }
        return sequence;
    }

    public List<List<Chord>> getSequences()
    {
        return Sequences_;
    }

    @Override
    public String toString()
    {
        return "PairLibrary{chordPairs=" + Sequences_ + '}';
    }
}
