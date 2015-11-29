package uk.co.nickthecoder.ichneutae;

import java.io.InputStream;
import java.net.URL;

public interface Parser
{
    public String getTitle();

    public String getContent();

    public void parse(URL url, InputStream is) throws Exception;
}
