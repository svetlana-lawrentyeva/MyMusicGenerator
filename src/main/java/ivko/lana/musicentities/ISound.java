package ivko.lana.musicentities;

import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public interface ISound extends IPlayable
{
    int getDuration();

    boolean isSilent();

    int getTone();

    int getAccent();
}
