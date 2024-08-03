package ivko.lana.generators;

import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.MusicType;
import ivko.lana.musicentities.Note;
import ivko.lana.musicentities.RhythmType;
import ivko.lana.util.Pair;
import ivko.lana.yaml.RhythmDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class DrumRhythmGenerator extends AccompanimentRhythmGenerator
{
    protected List<Integer> drums_;
    //    private List<Integer> drumSequence_ = new ArrayList<>();
    private int currentDrumIndex = 0;
    private List<Pair<Integer, Integer>> drumSequence_; // first = toneCode, second = duration
    private int drumDuration_;

    public DrumRhythmGenerator(Initializer initializer, int channel)
    {
        super(initializer, channel);
        drums_ = initializer.getMusicType() == MusicType.EPIC
                ? rhythmDetails_.getDrums(initializer.getMusicType())
                : initializer_.getDrumCombinations();
        RhythmDetails rhythmDetails = initializer_.getChordPrimaryRhythmDetails();
        RhythmPattern rhythmPattern = new RhythmPattern(rhythmDetails.getDurations(), rhythmDetails.getBaseDuration(),
                rhythmDetails.getAccents().size(), RhythmType.PEAK);

        List<Integer> pattern = rhythmPattern.getPattern();
        drumSequence_ = new ArrayList<>();
        for (int i = 0; i < pattern.size(); ++i)
        {
            int drumsIndex = (int) (Math.random() * drums_.size());
            drumSequence_.add(new Pair<>(drums_.get(drumsIndex), pattern.get(i)));
        }


//        int baseDuration = rhythmDetails.getBaseDuration();
//        int baseCounter = rhythmDetails.getAccents().size();
//        int measureLength = baseCounter * baseDuration;
//        drumDuration_ = baseDuration / 2;
//        int drumTickCounter = accents_.size() * 2;
//        Collections.shuffle(drums_);
//        for (int i = 0; i < accents_.size(); ++i)
//        {
////            int drumsIndex = (int) (Math.random() * drums_.size());
//            drumSequence_.add(drums_.get(0));
//            drumSequence_.add(drums_.get(1));
//        }
    }

    @Override
    protected RhythmDetails getRhythmDetails()
    {
        return initializer_.getMelodyPrimaryRhythmDetails();
    }

    @Override
    protected int generateLastDuration(int availableMeasure)
    {
        return generateSimpleDuration();
    }

    @Override
    protected int generateSimpleDuration()
    {
        return drumSequence_.get(currentDrumIndex).getSecond();
    }

    @Override
    protected void addNewSound(List<ISound> sounds, ISound newSound)
    {
        super.addNewSound(sounds, newSound);
        currentDrumIndex++;
        if (currentDrumIndex >= drumSequence_.size())
        {
            currentDrumIndex = 0;
        }
    }

    @Override
    protected boolean needPause(int availableMeasure)
    {
        return false;
    }

    @Override
    protected ISound createNewSound(int tone, int duration, int accentIndex, int channel_)
    {
        Note note = new Note(drumSequence_.get(currentDrumIndex).getFirst(), duration, accents_.get(accentIndex), getChannel(), initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier());
        note.setShouldDebug(false);
        return note;
    }
}
