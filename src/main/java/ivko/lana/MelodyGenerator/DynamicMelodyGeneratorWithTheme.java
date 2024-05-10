package ivko.lana.MelodyGenerator;

import ivko.lana.entities.DurationProbability;
import ivko.lana.entities.RhythmPattern;

import javax.sound.midi.*;
import java.util.List;
import java.util.Random;

public class DynamicMelodyGeneratorWithTheme
{
    private static final int[] MAJOR_SCALE = {0, 2, 4, 5, 7, 9, 11}; // C major scale intervals
    private static final int[] MINOR_SCALE = {0, 2, 3, 5, 7, 8, 10};

    private static final Random random = new Random();

    private static Initializer Initializer_;
    private static RhythmPattern RhythmPattern_;

    public static void start(Initializer initializer)
    {
        Initializer_ = initializer;
        RhythmPattern_ = Initializer_.getRhythmPattern();
        try {
            int[] scale = Initializer_.isMajor() ? MAJOR_SCALE : MINOR_SCALE;

            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            MidiChannel channel = synthesizer.getChannels()[0];
            Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
            synthesizer.loadInstrument(instruments[Initializer_.getInstrumentCode()]);
            channel.programChange(Initializer_.getInstrumentCode());

            int baseNote = 60; // Middle C
            List<Integer> accents = RhythmPattern_.getAccents();
            int measureLength = RhythmPattern_.getBaseDuration() * accents.size(); // Length of one measure
            int note;


            for (int i = 0; i < 100; i ++)
            {
                int accentIndex = 0;
                int availableMeasure = measureLength;

                while (availableMeasure != 0)
                {
                    note = baseNote + scale[random.nextInt(scale.length)];
                    int duration = generateDuration();
                    duration = Math.min(duration, availableMeasure);

                    availableMeasure -= duration;
                    boolean needPause = random.nextInt(5) == 2;

                    channel.noteOn(note, accents.get(accentIndex)); // Play note with a dynamic level of 70
                    Thread.sleep(duration);   // Hold the note for the duration
                    channel.noteOff(note);
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

            synthesizer.close();
        } catch (MidiUnavailableException | InterruptedException ex) {
            ex.printStackTrace();
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
