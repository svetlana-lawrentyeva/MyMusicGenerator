package ivko.lana.util;

/**
 * @author Lana Ivko
 */
public class Pair<T, U>
{
    private T first_;
    private U second_;

    public Pair(T first, U second)
    {
        first_ = first;
        second_ = second;
    }

    public void setFirst(T first)
    {
        first_ = first;
    }

    public void setSecond(U second)
    {
        second_ = second;
    }

    public T getFirst()
    {
        return first_;
    }

    public U getSecond()
    {
        return second_;
    }

    @Override
    public String toString()
    {
        return "Pair{" + first_ + ", " + second_ + '}';
    }
}
