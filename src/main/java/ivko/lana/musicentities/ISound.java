package ivko.lana.musicentities;

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
