package uk.co.nickthecoder.ichneutae;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RobotsTXT
{
    private static final Logger logger = LogManager.getLogger(RobotsTXT.class);

    private static String[] none = {};

    private String name;

    private Map<String, String[]> disallowedPrefixesMap;

    public RobotsTXT(String name)
    {
        this.name = name;
        this.disallowedPrefixesMap = new HashMap<String, String[]>();
    }

    private String[] parse(String site)
    {
        logger.trace("Parsing robots.txt for " + site);
        String urlString = site + "/robots.txt";

        CloseableHttpClient httpclient = null;
        InputStream in = null;
        CloseableHttpResponse response = null;

        try {
            ArrayList<String> list = new ArrayList<String>();

            httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(urlString);
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            in = entity.getContent();

            LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
            String line = reader.readLine();
            boolean mySection = false;
            while (line != null) {
                if (line.startsWith("User-agent:")) {
                    String agent = line.substring(11).trim();
                    mySection = (("*".equals(agent)) || (this.name.equals(agent)));
                } else if (mySection && (line.startsWith("Disallow:"))) {
                    String prefix = line.substring(9).trim();
                    list.add(prefix);
                    logger.trace("Disallowing : " + prefix);
                }
                line = reader.readLine();
            }

            String[] results = new String[list.size()];
            list.toArray(results);
            return results;

        } catch (Exception e) {
            logger.error("Failed to read robots.txt : " + urlString);
            return none;

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                }
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private String[] getDisallowed(String site)
    {
        String[] found = this.disallowedPrefixesMap.get(site);
        if (found == null) {
            String[] parsed = this.parse(site);
            this.disallowedPrefixesMap.put(site, parsed);
            return parsed;
        } else {
            return found;
        }
    }

    public boolean disallow(URL url)
    {
        String site;

        if ((url.getPort() < 0) || (url.getPort() == url.getDefaultPort())) {
            site = url.getProtocol() + "://" + url.getHost();
        } else {
            site = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
        }

        String path = url.getPath();

        String[] disallowedPrefixes = getDisallowed(site);
        for (String disallowedPrefix : disallowedPrefixes) {
            if (path.startsWith(disallowedPrefix)) {
                logger.trace("Disallow due to prefix " + disallowedPrefix);
                return true;
            }
        }

        return false;
    }
}
