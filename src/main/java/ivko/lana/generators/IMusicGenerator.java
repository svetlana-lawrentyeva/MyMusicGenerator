package ivko.lana.generators;

import ivko.lana.musicentities.IPlayable;

/**
 * @author Lana Ivko
 */
public interface IMusicGenerator<T extends IPlayable>
{
    T generate() throws InterruptedException;
}
