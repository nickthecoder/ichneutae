package uk.co.nickthecoder.ichneutae.filter;

public class NotFilter<T> extends FluentFilter<T>
{
    private Filter<T> filter;

    public NotFilter(Filter<T> filter)
    {
        this.filter = filter;
    }

    @Override
    public FluentFilter<T> not()
    {
        if (this.filter instanceof FluentFilter) {
            return (FluentFilter<T>) this.filter;
        }
        return super.not();
    }

    @Override
    public boolean accept(T t)
    {
        return !this.filter.accept(t);
    }
    
}
