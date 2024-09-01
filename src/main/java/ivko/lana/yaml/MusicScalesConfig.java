package ivko.lana.yaml;

import ivko.lana.entities.IScale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MusicScalesConfig
{

    public MusicScalesConfig()
    {
    }

    public static class ScaleConfig implements IScale
    {
        private String name;
        private String rhythmSize = null;
        private int soloInstrument;
        private Map<Integer, List<List<Integer>>> notes;

        public ScaleConfig()
        {
        }

        // Getters and Setters
        public String getRhythmSize()
        {
            return rhythmSize;
        }

        public void setRhythmSize(String rhythmSize)
        {
            this.rhythmSize = rhythmSize;
        }

        public int getSoloInstrument()
        {
            return soloInstrument;
        }

        public void setSoloInstrument(int soloInstrument)
        {
            this.soloInstrument = soloInstrument;
        }

        @Override
        public String toString()
        {
            return "ScaleConfig{" +
                    "name='" + name + '\'' +
                    ", rhythmSize='" + rhythmSize + '\'' +
                    ", soloInstrument=" + soloInstrument +
                    ", notes=" + notes +
                    '}';
        }

        @Override
        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public Map<Integer, List<List<Integer>>> getNotes()
        {
            return notes;
        }

        public void setNotes(Map<Integer, List<List<Integer>>> notes)
        {
            this.notes = notes;
        }

        @Override
        public int[] getScale()
        {
            return notes.keySet().stream()
                    .sorted()
                    .mapToInt(Integer::intValue)
                    .toArray();
        }

        @Override
        public int getBaseNote()
        {
            return getScale()[0];
        }

        @Override
        public List<Integer[]> getChords(int tone)
        {
            List<List<Integer>> chords = notes.get(tone);
            if (chords == null)
            {
                ArrayList<Integer> allNotes = new ArrayList<>(notes.keySet());
                Collections.sort(allNotes);
                chords = notes.get(allNotes.get(0));
            }
            return chords.stream()
                    .map(list -> list.toArray(new Integer[0]))
                    .collect(Collectors.toList());
        }

        @Override
        public Integer[] findChord(int note, List<Integer[]> chords)
        {
            return IScale.super.findChord(note, chords);
        }
    }

    private List<ScaleConfig> scales;

    // Getters and Setters
    public List<ScaleConfig> getScales()
    {
        return scales;
    }

    public void setScales(List<ScaleConfig> scales)
    {
        this.scales = scales;
    }

    @Override
    public String toString()
    {
        return "MeditativeMusicConfig{" +
                "scales=" + scales +
                '}';
    }
}
