package uk.co.nickthecoder.ichneutae.filter;

import java.util.Iterator;

public class OrFilter<T> extends CompoundFilter<T>
{
    public OrFilter(Filter<T> a, Filter<T> b)
    {
        super( a, b );
    }

    @Override
    public boolean accept(T t)
    {
        for (Iterator<Filter<T>> i = this.filters.iterator(); i.hasNext();) {
            if (i.next().accept(t)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public OrFilter<T> or(FluentFilter<T> other)
    {
        this.add( other );
        return this;
    }
}
