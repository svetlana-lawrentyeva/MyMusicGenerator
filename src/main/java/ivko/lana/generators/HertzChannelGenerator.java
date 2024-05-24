package ivko.lana.generators;

import ivko.lana.musicentities.*;
import ivko.lana.util.MusicUtil;
import ivko.lana.util.PitchBendCalculator;
import ivko.lana.yaml.RhythmPattern;

import javax.sound.midi.InvalidMidiDataException;
import java.util.Collections;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class HertzChannelGenerator implements IChannelGenerator
{
    public static final int INSTRUMENT_CODE = 52;
    public static final int ACCENT = 30;
    private Initializer initializer_;
    private int midiNote_;
    private Note hertzNote_;
    private int noteCounter_;

    public HertzChannelGenerator(Initializer initializer)
    {
        initializer_ = initializer;
        midiNote_ = PitchBendCalculator.findClosestMidiNote(initializer.getHertz());
        RhythmPattern rhythmPattern = initializer.getMelodyRhythmPattern();
        int totalLength = initializer.getMinutes() * 60 * 1000;
        int noteDuration = rhythmPattern.getBaseDuration() * rhythmPattern.getAccents().size();
        noteCounter_ = totalLength / noteDuration;
        hertzNote_ = new Note(midiNote_, noteDuration, ACCENT, MusicUtil.HERTZ_CHANNEL_NUMBER);
    }

    @Override
    public Channel generate() throws InterruptedException
    {
        return new HertzChannel(initializer_);
    }

    private List<Part> getParts(Initializer initializer)
    {
        return Collections.singletonList(
                new Part(Collections.singletonList(
                        new Phrase(Collections.singletonList(
                                new Rhythm(Collections.singletonList(hertzNote_))
                        )
                        )
                )
                )
        );
    }


    private class HertzChannel extends Channel
    {
        public HertzChannel(Initializer initializer)
        {
            super(getParts(initializer), INSTRUMENT_CODE, MusicUtil.HERTZ_CHANNEL_NUMBER);
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

        @Override
        protected void playParts(Metronom metronom) throws InterruptedException
        {
            for (int i = 0; i < noteCounter_; ++i)
            {
                if (metronom.shouldStop())
                {
                    return;
                }
                hertzNote_.play(getChannel(), metronom);
            }
        }
    }
}
