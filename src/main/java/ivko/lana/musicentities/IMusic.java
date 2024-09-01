package ivko.lana.musicentities;

/**
 * @author Lana Ivko
 */
public interface IMusic extends IPlayable
{
    void play() throws InterruptedException;

    void save(String fileName);
}
