package ivko.lana.generators;

import ivko.lana.musicentities.Channel;
import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.Note;
import ivko.lana.musicentities.Part;
import ivko.lana.util.MusicUtil;
import ivko.lana.util.PitchBendCalculator;
import ivko.lana.yaml.RhythmPattern;

import javax.sound.midi.InvalidMidiDataException;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class HertzChannelGenerator extends AccompanimentChannelGenerator
{
    public static final int HEALING = 285;
    public static final int FEAR = 396;
    public static final int DIFFICULTY = 417;
    public static final int DNA = 528;
    public static final int HEART = 639;
    public static final int ELECTROMAGNET = 741;
    public static final int INTUITION = 852;
    public static final int PINEAL_GLAND = 963;

    public static final int INSTRUMENT_CODE = 52;
    public static final int ACCENT = 30;
    private int midiNote_;
    private Note hertzNote_;
    private int noteCounter_;

    public HertzChannelGenerator(Initializer initializer)
    {
        super(initializer);
        midiNote_ = PitchBendCalculator.findClosestMidiNote(initializer.getHertz());
        RhythmPattern rhythmPattern = initializer.getMelodyRhythmPattern();
        int totalLength = initializer.getMinutes() * 60 * rhythmPattern.getBaseDurationMultiplier();
        int noteDuration = rhythmPattern.getBaseDuration() * rhythmPattern.getAccents().size();
        noteCounter_ = totalLength / noteDuration;
        hertzNote_ = new Note(midiNote_, noteDuration, ACCENT, MusicUtil.HERTZ_CHANNEL_NUMBER, initializer_.getMelodyRhythmPattern().getBaseDurationMultiplier());
    }

    @Override
    protected int getChannelNumber()
    {
        return MusicUtil.HERTZ_CHANNEL_NUMBER;
    }

    @Override
    protected AccompanimentRhythmGenerator createRhythmGenerator(Initializer initializer)
    {
        return new HertzRhythmGenerator(initializer);
    }

    @Override
    protected Channel generateImpl(List<Part> parts)
    {
        return new HertzChannel(parts);
    }

    private class HertzRhythmGenerator extends AccompanimentRhythmGenerator
    {
        public HertzRhythmGenerator(Initializer initializer)
        {
            super(initializer, MusicUtil.HERTZ_CHANNEL_NUMBER);
        }

        @Override
        protected RhythmPattern getRhythmPattern()
        {
            return initializer_.getMelodyRhythmPattern();
        }

        @Override
        protected int generateDuration()
        {
            return hertzNote_.getDuration();
        }

        @Override
        protected ISound createNewSound(int tone, int duration, int accentIndex, int channel_)
        {
            return hertzNote_;
        }
    }


    private class HertzChannel extends Channel
    {
        public HertzChannel(List<Part> parts)
        {
            super(parts, INSTRUMENT_CODE, MusicUtil.HERTZ_CHANNEL_NUMBER);
        }

        @Override
        protected void initInstrument()
        {
            try
            {
                PitchBendCalculator.setPitchBendRange(this, 1, 0);
                super.initInstrument();
                int pitchBendValue = PitchBendCalculator.calculatePitchBendValue(initializer_.getHertz());
                channel_.setPitchBend(pitchBendValue + 8192);

            } catch (InvalidMidiDataException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
