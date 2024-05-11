package ivko.lana.generators;

import ivko.lana.musicentities.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class PartGenerator implements IMusicGenerator<Part>
{
    private PhraseGenerator generatorQuestion_;
    private PhraseGenerator generatorAnswer_;

    private int phraseCount_;
    private ChannelType channelType_;

    public PartGenerator(Initializer initializer, ChannelType channelType)
    {
        channelType_ = channelType;
        phraseCount_ = initializer.getPhraseCount();
        generatorQuestion_ = new PhraseGenerator(initializer, channelType, PhraseType.QUESTION);
        generatorAnswer_ = new PhraseGenerator(initializer, channelType, PhraseType.ANSWER);
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
        return new Part(phrases);
    }
}
