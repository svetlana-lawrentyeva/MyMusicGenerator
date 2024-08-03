package ivko.lana.generators;

import ivko.lana.musicentities.ChannelType;
import ivko.lana.musicentities.Part;
import ivko.lana.musicentities.Phrase;
import ivko.lana.musicentities.PhraseType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class PartGenerator implements IMusicGenerator<Part>
{
    private PhraseGenerator generatorQuestion_;
    private PhraseGenerator generatorAnswer_;
    private int channel_;

    private int phraseCount_;

    public PartGenerator(Initializer initializer, ChannelType channelType, int channel)
    {
        channel_ = channel;
        phraseCount_ = initializer.getPhraseCount();
        generatorQuestion_ = new PhraseGenerator(initializer, channelType, PhraseType.QUESTION, channel);
        generatorAnswer_ = new PhraseGenerator(initializer, channelType, PhraseType.ANSWER, channel);
    }

    @Override
    public Part generate() throws InterruptedException
    {
        List<Phrase> phrases = new ArrayList<>();
        Phrase question = generatorQuestion_.generate();
        Phrase answer = generatorAnswer_.generate();
        for (int i = 0; i < phraseCount_; i++)
        {
            phrases.add(question);
            phrases.add(answer);
        }
        return new Part(phrases, channel_);
    }
}
