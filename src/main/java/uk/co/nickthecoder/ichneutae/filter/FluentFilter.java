package uk.co.nickthecoder.ichneutae.filter;


/**
 * A fluent api, allowing a natural, and simple creation of complex filters by
 * combining "or" "and" and "not". e.g. <code>
 * StringFilter filter = new RegexStringFilter( "[a-f]*" ).or( new RegexStringFilter( "[0-9]*" ) );
 * </code>
 */
public abstract class FluentFilter<T> implements Filter<T>
{

    public abstract boolean accept(T t);

    public FluentFilter<T> or(FluentFilter<T> other)
    {
        return new OrFilter<T>(this, other);
    }

    public FluentFilter<T> and(FluentFilter<T> other)
    {
        return new AndFilter<T>(this, other);
    }

    public FluentFilter<T> not()
    {
        return new NotFilter<T>(this);
    }

    /**
     * Create a new filter, where this filter must be true, and the other filter must be false for items to be accepted.
     * 
     * Note, it is common for the other parameter to be a CompoundFilter, in which case take care to use the
     * OrFilter/AndFilter the right way round. If you want to exclude all items from the compound, then it should be an
     * OrFilter. 
     */
    public FluentFilter<T> exclude(FluentFilter<T> other)
    {
        return new AndFilter<T>(this, new NotFilter<T>(other));
    }
}
