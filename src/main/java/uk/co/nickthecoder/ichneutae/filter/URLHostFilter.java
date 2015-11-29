package uk.co.nickthecoder.ichneutae.filter;

import java.net.URL;

public class URLHostFilter extends URLFilter
{
    private Filter<String> filter;
    
    public URLHostFilter( Filter<String> filter )
    {
        this.filter = filter;
    }
    
    @Override
    public boolean accept( URL url)
    {
        return this.filter.accept( url.getHost() );
    }

}
