package ivko.lana.generators;

import ivko.lana.musicentities.*;
import ivko.lana.util.MusicUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class MusicGenerator implements IMusicGenerator<IMusic>
{
    private MelodyChannelGenerator melodyChannelGenerator_ = null;
    private DrumsChannelGenerator drumsChannelGenerator_ = null;
    private HertzChannelGenerator hertzChannelGenerator_ = null;
    private List<ChordChannelGenerator> chordsChannelGenerators_ = new ArrayList<>();
    private Initializer initializer_;

    public MusicGenerator(Initializer initializer)
    {
        initializer_ = initializer;
        melodyChannelGenerator_ = new MelodyChannelGenerator(initializer, 0);
        if (initializer.useDrums())
        {
            drumsChannelGenerator_ = new DrumsChannelGenerator(initializer);
        }
        if (initializer.getHertz() > 0)
        {
            hertzChannelGenerator_ = new HertzChannelGenerator(initializer);
        }
        for (int i = 0; i < initializer.getChannelCount(); ++i)
        {
//            ChannelType channelType = defineChannelType(i, initializer);
            int channelNumber = findChannelNumber();
            ChordChannelGenerator channelGenerator = new ChordChannelGenerator(initializer, channelNumber);
            chordsChannelGenerators_.add(channelGenerator);
        }
    }

    private static ChannelType defineChannelType(int iteration, Initializer initializer)
    {
        switch (iteration)
        {
            case 0:
                return ChannelType.MELODY;
            case 1:
                if (initializer.useDrums())
                {
                    return ChannelType.DRUM;
                }
            case 2:
                if (initializer.getHertz() > 0)
                {
                    return ChannelType.HERTZ;
                }
            default:
                return ChannelType.CHORD;
        }
    }

    protected int findChannelNumber()
    {
        return MusicUtil.getInstance().getFreeChannelNumber();
    }

    @Override
    public IMusic generate() throws InterruptedException
    {
        List<Channel> channels = new ArrayList<>();
        Channel melodyChannel = melodyChannelGenerator_.generate();
        channels.add((melodyChannel));
        if (drumsChannelGenerator_ != null)
        {
            drumsChannelGenerator_.setMelodyChannel(melodyChannel);
            channels.add(drumsChannelGenerator_.generate());
        }
        if (hertzChannelGenerator_ != null)
        {
            hertzChannelGenerator_.setMelodyChannel(melodyChannel);
            channels.add(hertzChannelGenerator_.generate());
        }
        for (ChordChannelGenerator generator : chordsChannelGenerators_)
        {
            generator.setMelodyChannel(melodyChannel);
            channels.add(generator.generate());
        }
        List<Integer> allSounds = new ArrayList<>();
        for (Channel channel : channels)
        {
            List<ISound> sounds = channel.getAllSounds();
            List<Integer> tones = sounds.stream()
                    .filter(sound -> !sound.isSilent())
                    .map(ISound::getTone)
                    .collect(Collectors.toList());
            allSounds.addAll(tones);
        }
        int min = Collections.min(allSounds);
        int max = Collections.max(allSounds);

        return MusicFactory.getMusic(channels, initializer_);
    }
}
