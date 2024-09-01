package ivko.lana.neurotone.util;

/**
 * @author Lana Ivko
 */
public class Pair<T, U>
{
    private T first_;
    private U second_;

    public Pair(T first, U second)
    {
        this.first_ = first;
        this.second_ = second;
    }

    public T getFirst()
    {
        return first_;
    }

    public void setFirst(T first)
    {
        this.first_ = first;
    }

    public U getSecond()
    {
        return second_;
    }

    public void setSecond(U second)
    {
        this.second_ = second;
    }

    @Override
    public String toString()
    {
        return "Pair{" +
                "first_=" + first_ +
                ", second_=" + second_ +
                '}';
    }
}

