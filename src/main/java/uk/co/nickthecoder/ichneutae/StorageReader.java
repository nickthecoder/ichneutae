package uk.co.nickthecoder.ichneutae;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;

public class StorageReader
{
    private static final Logger logger = LogManager.getLogger(StorageReader.class);

    private IndexReader reader;

    private IndexSearcher searcher;

    public StorageReader()
    {
    }

    public void open() throws IOException
    {
        logger.trace("Opened");
        this.reader = DirectoryReader.open(Configuration.index);
        this.searcher = new IndexSearcher(this.reader);
    }

    public void close() throws IOException
    {
        if ( this.reader!= null) {
            this.reader.close();
            logger.trace("Closed");
        }
    }

    public IndexSearcher getIndexSearcher()
    {
        return this.searcher;
    }
    
    /**
     * 
     * @param q
     *            The Lucene query (See {@link #createQuery(String)})
     * @param pageNumber
     *            The page number (one based)
     * @return An array of scored documents
     * @throws IOException
     */
    public TopDocs query(Query q, int pageNumber) throws IOException
    {
        TopScoreDocCollector collector = TopScoreDocCollector.create(pageNumber * Configuration.hitsPerPage);
        this.searcher.search(q, collector);
        return collector.topDocs((pageNumber-1) * Configuration.hitsPerPage, Configuration.hitsPerPage);
    }

    /**
     * This is a quick and simple way to run test queries interactively, by
     * using a Groovy script.
     */
    public static void main(String[] argv) throws Exception
    {
        if ((argv.length == 0) || (argv.length > 2)) {
            System.out.println("Usage : StorageReader GROOVY_FILE [LOG4j2_FILE]");

        } else {
            if (argv.length == 2) {
                Configurator.initialize(null, argv[1]);
            }
            Configuration.load(argv[0]);
        }
    }

}
