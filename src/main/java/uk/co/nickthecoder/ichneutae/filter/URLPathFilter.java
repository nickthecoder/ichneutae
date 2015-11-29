package uk.co.nickthecoder.ichneutae.filter;

import java.net.URL;

public class URLPathFilter extends URLFilter
{
    private Filter<String> filter;
    
    public URLPathFilter( Filter<String> filter )
    {
        this.filter = filter;
    }
    
    @Override
    public boolean accept( URL url)
    {
        return this.filter.accept( url.getPath() );
    }
}
