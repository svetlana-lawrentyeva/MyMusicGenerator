package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.IWaveGenerator;
import ivko.lana.neurotone.processing.NotesSerializer;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Lana Ivko
 */
public class SavedWaveSupplier implements IWaveGenerator
{
    private Iterator<double[][]> iterator_;

    private FrequencyConverter frequencyConverter_;

    public SavedWaveSupplier()
    {
        frequencyConverter_ = new FrequencyConverter(WaveType.SOLFEGE);
        iterator_ = getNewIterator();
    }

    private Iterator<double[][]> getNewIterator()
    {
        Iterator<double[][]> iterator = null;
        List<double[][]> nextParty = NotesSerializer.getInstance().getNext();
        if (nextParty != null)
        {
            iterator = nextParty.iterator();
        }
        return iterator;
    }

    @Override
    public boolean generateMusic()
    {
        boolean hasNext = iterator_.hasNext();
        if (!hasNext)
        {
            iterator_ = getNewIterator();
            hasNext = iterator_ != null && iterator_.hasNext();
        }
        if (hasNext)
        {
            frequencyConverter_.convert(iterator_.next());
        }
        return hasNext;
    }

    @Override
    public WaveDetail getLeftChannel()
    {
        return frequencyConverter_.getLeftChannel();
    }

    @Override
    public WaveDetail getRightChannel()
    {
        return frequencyConverter_.getRightChannel();
    }
}
