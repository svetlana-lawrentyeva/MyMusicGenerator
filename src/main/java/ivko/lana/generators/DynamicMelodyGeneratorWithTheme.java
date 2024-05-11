package ivko.lana.generators;

import ivko.lana.yaml.DurationProbability;
import ivko.lana.yaml.RhythmPattern;
import ivko.lana.entities.IScale;

import javax.sound.midi.*;
import java.util.List;
import java.util.Random;

public class DynamicMelodyGeneratorWithTheme
{
    private static final Random random = new Random();

    private static Initializer Initializer_;
    private static RhythmPattern RhythmPattern_;

    public static void start(Initializer initializer)
    {
        Initializer_ = initializer;
        RhythmPattern_ = Initializer_.getRhythmPattern();
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            MidiChannel channel = synthesizer.getChannels()[0];
            Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
            synthesizer.loadInstrument(instruments[Initializer_.getInstrumentCode()]);
            channel.programChange(Initializer_.getInstrumentCode());

            List<Integer> accents = RhythmPattern_.getAccents();
            int measureLength = RhythmPattern_.getBaseDuration() * accents.size(); // Length of one measure
            int[] scales = Initializer_.getScale().getScales();
            generateMusic(measureLength, scales, accents, channel);
            synthesizer.close();
        } catch (MidiUnavailableException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private static void generateMusic(int measureLength, int[] scales, List<Integer> accents, MidiChannel channel) throws InterruptedException
    {
        int note;
        for (int i = 0; i < 100; i ++)
        {
            int accentIndex = 0;
            int availableMeasure = measureLength;

            while (availableMeasure != 0)
            {
                note = IScale.BASE_NOTE + scales[random.nextInt(scales.length)];
                int duration = generateDuration();
                duration = Math.min(duration, availableMeasure);

                availableMeasure -= duration;
                boolean needPause = random.nextInt(5) == 2;

                Integer accent = accents.get(accentIndex);
                int[] chord = generateChord(note);
                play(channel, new int[]{note}, accent, duration);
//                    play(channel, chord, accent, duration);
                if (needPause)
                {
                    duration = generateDuration();
                    duration = Math.min(duration, availableMeasure);
                    availableMeasure -= duration;
                    Thread.sleep(duration);      // Pause after the note
                }
                accentIndex = accents.size() - availableMeasure / RhythmPattern_.getBaseDuration();
                int b = 0;
            }
            int a = 0;
        }
    }

    private static int[] generateChord(int mainNote) {
        IScale scale = Initializer_.getScale();
        int[] chord = scale.findChord(mainNote - IScale.BASE_NOTE, scale.getChords());
        return chord;
    }

    private static void play(MidiChannel channel, int[] notes, Integer accent, int duration) throws InterruptedException
    {
        for (int note : notes) {
            channel.noteOn(note, accent);
        }
        Thread.sleep(duration);   // Hold the note for the duration
        for (int note : notes) {
            channel.noteOff(note, accent);
        }
        }

        private static int generateDuration()
        {
    //        return DURATIONS[random.nextInt(DURATIONS.length)];

            double randomValue = random.nextDouble(); // Случайное число от 0.0 до 1.0
            double cumulativeProbability = 0.0;

            List<DurationProbability> durations = RhythmPattern_.getDurations();
            for (int i = 0; i < durations.size(); i++) {
                DurationProbability durationProbability = durations.get(i);
                int duration = durationProbability.getDuration();
                cumulativeProbability += durationProbability.getProbability();
                if (randomValue < cumulativeProbability) {
                    return duration;
                }
            }
            return durations.get(durations.size() - 1).getDuration(); // На случай числовых ошибок, возвращаем последний элемент

        }
}
