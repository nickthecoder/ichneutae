package uk.co.nickthecoder.ichneutae.filter;

import java.util.LinkedList;
import java.util.List;

public abstract class CompoundFilter<T> extends FluentFilter<T>
{
    protected List<Filter<T>> filters;

    public CompoundFilter()
    {
        this.filters = new LinkedList<Filter<T>>();
    }

    public CompoundFilter(Filter<T> a, Filter<T> b)
    {
        this.filters = new LinkedList<Filter<T>>();
        this.filters.add(a);
        this.filters.add(b);
    }

    @SafeVarargs
    public CompoundFilter(Filter<T>... filters)
    {
        this.filters = new LinkedList<Filter<T>>();
        for (Filter<T> filter : filters) {
            this.filters.add(filter);
        }
    }

    public void add(Filter<T> filter)
    {
        filters.add(filter);
    }

    @Override
    public abstract boolean accept(T t);

}
