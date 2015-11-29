package uk.co.nickthecoder.ichneutae;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

/**
 * Adds an abstraction layer over Lucene's IndexWriter.
 * 
 * Lucene's IndexWriter is thread safe, and only one writer can be open at a
 * time. In contrast, you can open multiple StorageWriters simultaneously.
 * StorageWriter uses a single static IndexWriter (which is shared by multiple
 * StorageWriters) along with a counter, which is incremented on each open and
 * decremented on close. The underlying IndexWriter is closed when the counter
 * reaches zero.
 */
public class StorageWriter
{
    private static final Logger logger = LogManager.getLogger(StorageWriter.class);

    /**
     * There is only ever one IndexWriter open at a given time.
     */
    private static IndexWriter writer = null;

    /**
     * A count of the number of StorageWriters that are currently using the
     * IndexWriter
     */
    private static int openCount = 0;

    private boolean opened = false;

    public StorageWriter()
    {
    }

    private static synchronized void staticOpen() throws IOException
    {
        openCount++;
        if (writer == null) {
            Directory index = Configuration.index;
            IndexWriterConfig config = new IndexWriterConfig(Configuration.analyzer);
            writer = new IndexWriter(index, config);
        }
    }

    private static synchronized void staticClose() throws IOException
    {
        openCount--;
        if (openCount == 0) {
            writer.close();
            writer = null;
        }
    }

    /**
     * 
     * @throws IOException
     *             If the underlying IndexWriter cannot be opened, or if this
     *             StorageWriter is already open.
     */
    public void open() throws IOException
    {
        if (this.opened) {
            throw new IOException("Already open");
        }

        staticOpen();
        this.opened = true;
    }

    /**
     * 
     * @throws IOException
     *             If the underlying IndexWriter cannot be closed, or if this
     *             StorageWriter is not open.
     */
    public void close() throws IOException
    {
        if (! this.opened) {
            throw new IOException("Not open");
        }
        staticClose();
        this.opened = false;
    }

    /**
     * Removes all documents from the index who's lastUpdate is smaller than
     * <code>time</code>.
     * 
     * @param lastUpdate
     *            Based on the number of milliseconds since January 1, 1970,
     *            00:00:00 GMT. See {@link java.util.Date#time()}
     */
    public void purge(long time)
    {
        try {
            Query query = NumericRangeQuery.newLongRange("lastUpdate", 0l, time - 1, true, true);
            writer.deleteDocuments(query);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to purge. " + e);
        }
    }

    /**
     * Removes a single document from the index.
     * 
     * @param url
     *            The URL of the web page.
     * @throws IOException
     */
    public void deleteDocument(URL url) throws IOException
    {
        writer.deleteDocuments(new Term("url", url.toString()));
    }

    /**
     * Adds a document to the index.
     * 
     * @param url
     *            The URL of the web page
     * @param parser
     *            The title and content of the web page (neither of which can be null).
     * @throws IOException
     */
    public void addDocument(URL url, Parser parser) throws IOException
    {
        logger.trace( "Adding document " + url);

        Document doc = new Document();
        String title = parser.getTitle();
        String content = parser.getContent();

        if (url == null) {
            logger.error("Cannot store a null url");
            return;
        }
        if (title == null) {
            logger.error("Cannot store a null title for " + url);
            return;
        }
        if (content == null) {
            logger.error("Cannot store a null content for " + url);
            return;
        }
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        doc.add(new LongField("lastUpdate", new Date().getTime(), Field.Store.YES));

        String urlString = url.toString();
        // use a string field because we don't want it tokenized
        doc.add(new StringField("url", urlString, Field.Store.YES));

        for (Category category : Configuration.categories) {
            if (category.matches(url)) {
                logger.trace( "Matched category " + category.code );;
                doc.add(new StringField("category", category.code, Field.Store.YES));
            }
        }

        writer.updateDocument(new Term("url", urlString), doc);
    }
}
