package uk.co.nickthecoder.ichneutae.filter;

public class FalseFilter<T> extends FluentFilter<T>
{

    public FalseFilter()
    {
    }
    
    @Override
    public boolean accept( T t)
    {
        return false;
    }
    
    @Override
    public FluentFilter<T> or( FluentFilter<T> other )
    {
        return other;
    }
    
    @Override
    public FluentFilter<T> and( FluentFilter<T> other )
    {
        return this;
    }
}
