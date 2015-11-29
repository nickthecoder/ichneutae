package uk.co.nickthecoder.ichneutae.filter;

import java.util.Iterator;

public class AndFilter<T> extends CompoundFilter<T>
{
    public AndFilter(Filter<T> a, Filter<T> b)
    {
        super( a, b );
    }

    @Override
    public boolean accept(T t)
    {
        for (Iterator<Filter<T>> i = this.filters.iterator(); i.hasNext();) {
            if (!i.next().accept(t)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public FluentFilter<T> and(FluentFilter<T> other)
    {
        this.add( other );
        return this;
    }
}
