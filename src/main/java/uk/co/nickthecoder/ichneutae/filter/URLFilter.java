package uk.co.nickthecoder.ichneutae.filter;

import java.net.URL;

/**
 * Adds convenience static methods for making various Filter<URL> objects.
 * 
 * Note, you should decalare methods and variables using Filter<URL> rather than
 * URLFilter, because Filter<URL> can also include filters such as
 * TrueFilter<URL> and EqualsFilter<URL>, which are NOT subclasses of URLFilter.
 */
public abstract class URLFilter extends FluentFilter<URL>
{
    public static URLPathFilter regexPath(String regex)
    {
        return new URLPathFilter(new RegexFilter(regex));
    }

    public static URLPathFilter equalsPath(String matching)
    {
        return new URLPathFilter(new EqualsFilter<String>(matching));
    }

    public static URLHostFilter regexHost(String regex)
    {
        return new URLHostFilter(new RegexFilter(regex));
    }

    public static URLHostFilter equalsHost(String matching)
    {
        return new URLHostFilter(new EqualsFilter<String>(matching));
    }
}
