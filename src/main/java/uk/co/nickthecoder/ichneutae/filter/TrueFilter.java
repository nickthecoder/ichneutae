package uk.co.nickthecoder.ichneutae.filter;

public class TrueFilter<T> extends FluentFilter<T>
{
    public TrueFilter()
    {
    }

    @Override
    public boolean accept( T t )
    {
        return true;
    }

    @Override
    public FluentFilter<T> or( FluentFilter<T> other )
    {
        return this;
    }

    @Override
    public FluentFilter<T> and( FluentFilter<T> other )
    {
        return other;
    }
}
