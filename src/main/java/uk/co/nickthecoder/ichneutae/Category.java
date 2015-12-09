package uk.co.nickthecoder.ichneutae;

import java.net.URL;

import uk.co.nickthecoder.webwidgets.filter.FalseFilter;
import uk.co.nickthecoder.webwidgets.filter.Filter;

public class Category
{

    /**
     * A code for this category - must be alphanumeric characters only.
     * This will be used as URL parameter names, as well as values stored in the Lucene database.
     */
    public String code;

    /**
     * Human readable label for this category.
     */
    public String label;

    public Filter<URL> filter;

    public Category(String code)
    {
        this( code, code);
    }

    public Category(String code, String label)
    {
        this(code, label, new FalseFilter<URL>() );
    }

    public Category(String code, String label, Filter<URL> filter)
    {
        this.code = code;
        this.label = label;
        this.filter = filter;
    }

    /**
     * A Fluent API to set the filter for this category.
     * @return this
     */
    public Category setFilter(Filter<URL> filter)
    {
        this.filter = filter;
        return this;
    }
    
    public boolean matches( URL url )
    {
        return this.filter.accept( url );
    }
    
    public String getCode() 
    {
        return this.code;
    }
    
    public String getLabel()
    {
        return this.label;
    }

}
