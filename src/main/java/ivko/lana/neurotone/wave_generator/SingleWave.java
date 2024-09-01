package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.Sound;

/**
 * @author Lana Ivko
 */
public class SingleWave
{
    private Sound[] sounds_;

    public SingleWave(int notesLength)
    {
        sounds_ = new Sound[notesLength];
    }

    public void addNote(Sound note, int index)
    {
        sounds_[index] = note;
    }

    public short[] getSamples()
    {
        int totalSamplesLength = 0;

        // Рассчитываем общую длину результирующего массива семплов
        int fadeOutLength = Util.convertMsToSampleLength(Constants.FadeOutDurationMs_);
        for (int j = 0; j < sounds_.length; ++j)
        {
            Sound note = sounds_[j];
            totalSamplesLength += (note.getSamples().length - fadeOutLength);
        }
        totalSamplesLength += fadeOutLength;

        short[] samples = new short[totalSamplesLength];
        int lastEndIndex = 0;
        for (int i = 0; i < sounds_.length; i++)
        {
            Sound sound = sounds_[i];

            if (i == 0)
            {
                // Записываем первую ноту
                System.arraycopy(sound.getSamples(), 0, samples, lastEndIndex, sound.getSamples().length);
                lastEndIndex += sound.getSamples().length;
            }
            else
            {
                int startFadeOutIndex = lastEndIndex - fadeOutLength;

                for (int j = 0; j < fadeOutLength; j++)
                {
                    short previousValue = samples[startFadeOutIndex + j];
                    short currentValue = sound.getSamples()[j];
                    samples[startFadeOutIndex + j] = (short) Math.max(Math.min(previousValue + currentValue, Short.MAX_VALUE), Short.MIN_VALUE);
                }

                System.arraycopy(sound.getSamples(), fadeOutLength, samples, lastEndIndex, sound.getSamples().length - fadeOutLength);
                lastEndIndex += sound.getSamples().length - fadeOutLength;
            }
        }

        return samples;
    }
}
