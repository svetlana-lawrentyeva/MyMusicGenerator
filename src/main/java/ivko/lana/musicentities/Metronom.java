package ivko.lana.musicentities;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Lana Ivko
 */
public class Metronom extends CountDownLatch
{
    private final AtomicBoolean stopFlag_;

    /**
     * Constructs a {@code CountDownLatch} initialized with the given count.
     *
     * @param count the number of times {@link #countDown} must be invoked
     *              before threads can pass through {@link #await}
     * @throws IllegalArgumentException if {@code count} is negative
     */
    public Metronom(int count)
    {
        super(count);
        stopFlag_ = new AtomicBoolean(false);
    }

    public boolean shouldStop()
    {
        return stopFlag_.get();
    }

    public void stop()
    {
        stopFlag_.set(true);
    }
}
