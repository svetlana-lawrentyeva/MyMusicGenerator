package ivko.lana.musicentities;

import ivko.lana.yaml.Frequencies;
import ivko.lana.yaml.FrequencyLoader;

import javax.sound.midi.MidiChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class MusicTibetan implements IPlayable, IMusic
{
    private List<Channel> channels_;
    private Map<Integer, List<Double>> frequencies_;

    public MusicTibetan(List<Channel> channels)
    {
        channels_ = channels;
        Frequencies loadedFrequencies = FrequencyLoader.load();
        frequencies_ = loadedFrequencies.getFrequencies();
    }

    public List<Channel> getChannels()
    {
        return channels_;
    }

    public void addChannel(Channel channel)
    {
        channels_.add(channel);
    }

    @Override
    public int getChannelNumber()
    {
        return -1;
    }

    private double[][] convertToNotes()
    {
        List<ISound> sounds = getAllSounds();
        double[][] notes = new double[sounds.size()][];
        for (int i = 0; i < sounds.size(); ++i)
        {
            ISound sound = sounds.get(i);
            notes[i] = new double[2];
            notes[i][0] = frequencies_.get(sound.getTone()).get(0);
            notes[i][1] = sound.getDuration();
        }
        return notes;
    }

    public void play()
    {
//        double[][] notes = convertToNotes();
//        TibetanMusicHandler tibetanMusicHandler = new TibetanMusicHandler();
//        tibetanMusicHandler.getByteArray(notes);
    }

    public void save(String fileName)
    {
//        String filename = fileName + ".wav";
//        double[][] notes = convertToNotes();
//        TibetanMusicHandler tibetanMusicHandler = new TibetanMusicHandler();
//        tibetanMusicHandler.getByteArray(notes);
    }

    @Override
    public void play(MidiChannel channel, Metronom metronom) throws InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return channels_.stream()
                .map(channel -> (IPlayable) channel)
                .collect(Collectors.toList());
    }

    public List<Integer> getAllNotes()
    {
        Channel melodyChannel = channels_.stream()
                .filter(channel -> channel.isMelody())
                .findFirst()
                .orElse(null);
        List<Integer> result;
        if (melodyChannel != null)
        {
            result = melodyChannel.getAllNotes();
        }
        else
        {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    @Override
    public List<ISound> getAllSounds()
    {
        List<ISound> sounds = new ArrayList<>();
        for (Channel channel : channels_)
        {
            sounds.addAll(channel.getAllSounds());
        }
        return sounds;
    }
}
