package uk.co.nickthecoder.ichneutae;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * The spider, which scans the website(s), indexing the pages.
 * 
 * There is a {@link #main(String[])} method, so can be run stand-alone.
 * Alternatively, you can {@link #spider()} to begin spidering the whole
 * website(s), or call {@link #index(URL)} to (re)index a single web page.
 * 
 * This class is NOT thread safe, therefore create a new instance each time you
 * call index or spider.
 */
public class Ichneutae
{
    private static final Logger logger = LogManager.getLogger(Ichneutae.class);

    private static final long MILLIS_PER_DAY = 86400000l;

    private Set<URL> pending;

    private Set<URL> processed;

    private HashMap<String, Parser> parsers;

    private StorageWriter storageWriter;

    private RobotsTXT robotsTxt;

    public Ichneutae() throws Exception
    {
        this.robotsTxt = new RobotsTXT("ichneutae");

        this.parsers = new HashMap<String, Parser>();

        HTMLParser htmlParser = new HTMLParser(this);
        this.parsers.put("text/html", htmlParser);
        this.parsers.put("text/xhtml", htmlParser);
        // TODO Add a plain-text parser.
        // TODO Add "do nothing" parsers for mime types which are not to be
        // parsed. e.g. images.
    }

    public void spider() throws IOException
    {
        logger.trace("Spidering");
        this.pending = new HashSet<URL>();
        this.processed = new HashSet<URL>();
        this.storageWriter = new StorageWriter();
        this.storageWriter.open();

        long startTime = new Date().getTime();

        try {

            for (int i = 0; i < Configuration.origins.size(); i++) {
                addUrl(Configuration.origins.get(i));
            }

            while (!this.pending.isEmpty()) {

                for (Iterator<URL> i = this.pending.iterator(); i.hasNext();) {
                    try {
                        URL url = i.next();
                        i.remove();
                        this.processed.add(url);
                        process(url);
                    } catch (ConcurrentModificationException e) {
                        // Break - The while loop will kick in and a new
                        // Iterator object will be created.
                        break;
                    }
                }
                logger.trace("Pending queue : " + this.pending.size());
            }

            if (Configuration.purgeDays >= 0) {
                this.storageWriter.purge(startTime + Configuration.purgeDays * MILLIS_PER_DAY);
            }

        } finally {
            this.storageWriter.close();
        }

        logger.trace("Spidering complete");

    }

    public void index(URL url) throws IOException
    {
        logger.trace("(Re-)indexing " + url);

        this.pending = new HashSet<URL>();
        this.processed = new HashSet<URL>();
        this.storageWriter = new StorageWriter();
        this.storageWriter.open();

        try {
            process(url);

        } finally {
            this.storageWriter.close();
        }

    }

    private void process(URL url)
    {
        logger.trace("Process " + url);

        if (this.robotsTxt.disallow(url)) {
            return;
        }

        try {

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url.toString());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                // TODO Check for a redirection, and use the new URL in all
                // future calls.
                // TODO Add the new URL to this.processed too.
                if (entity == null) {
                    logger.error("No Entity for " + url);
                } else {
                    Header contentType = entity.getContentType();
                    if (contentType == null) {
                        logger.info("No Mime Type for " + url);
                    } else {
                        String mimeType = contentType.getValue().split(";")[0].trim();
                        Parser parser = this.parsers.get(mimeType);
                        if (parser == null) {
                            logger.info("No Parser for Mime Type " + mimeType + " for " + url);
                        } else {
                            InputStream in = entity.getContent();
                            try {
                                parser.parse(url, in);
                                store(url, parser);
                            } catch (Exception e) {
                                e.printStackTrace();
                                logger.error("Failed to read/store " + url);
                            } finally {
                                in.close();
                            }
                        }
                    }

                }
            } finally {
                response.close();
            }

        } catch (IOException e) {
            logger.info("Failed to process " + url + " " + e);
        }
    }

    private void store(URL url, Parser parser) throws IOException
    {
        logger.info("Storing " + url + " : " + parser.getTitle());
        this.storageWriter.addDocument(url, parser);
    }

    public void addUrl(URL url)
    {
        // Strip the reference ("#...") if there is one.
        try {
            if (url.getRef() != null) {
                url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
            }
        } catch (MalformedURLException e) {
            // Do nothing, keep the url with the # reference
        }

        if (this.processed.contains(url)) {
            return;
        }
        if (this.pending.contains(url)) {
            return;
        }

        if (Configuration.filter.accept(url)) {
            this.pending.add(url);
        }
    }

    public static void main(String[] argv) throws Exception
    {
        if ((argv.length == 0) || (argv.length > 2)) {
            System.out.println("Usage : Ichneutae CONFIG_FILE [LOG4j2_FILE]");

        } else {
            if (argv.length == 2) {
                Configurator.initialize(null, argv[1]);
            }
            Configuration.load(argv[0]);
            new Ichneutae().spider();
        }
    }

}
