package uk.co.nickthecoder.ichneutae.model;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import uk.co.nickthecoder.ichneutae.Configuration;
import uk.co.nickthecoder.ichneutae.StorageReader;

/**
 * Wraps the Hits object in a class more suitable for jsp
 */

public class HitsBean
{
    public ScoreDoc[] scoreDocs;

    public QueryBean queryBean;

    public int pageNumber;

    public int hitCount;
    
    public int pageCount;
    
    public IndexSearcher searcher;

    public HitsBean(QueryBean queryBean, StorageReader reader, int pageNumber) throws IOException
    {
        this.queryBean = queryBean;
        this.searcher = reader.getIndexSearcher();
        this.pageNumber = pageNumber;

        TopDocs topDocs = reader.query(queryBean.query, pageNumber);
        this.hitCount = topDocs.totalHits;
        this.pageCount = (topDocs.totalHits + Configuration.hitsPerPage - 1) / Configuration.hitsPerPage;
        this.scoreDocs = topDocs.scoreDocs;
    }

    public Iterator<HitBean> getHits()
    {
        return new HitsIterator();
    }

    public int getHitCount()
    {
        return this.hitCount;
    }
    
    public int getPageCount()
    {
        return this.pageCount;
    }
    // -------------------- [[Inner Class HitsIterator]] --------------------

    public class HitsIterator implements Iterator<HitBean>
    {

        int nextIndex = 0;

        public boolean hasNext()
        {
            return nextIndex < scoreDocs.length;
        }

        public HitBean next()
        {
            ScoreDoc sc = scoreDocs[nextIndex];
            Document doc = null;
            try {
                doc = searcher.doc(sc.doc);
            } catch (IOException e) {
                // Do nothing
            }

            nextIndex++;
            return new HitBean(HitsBean.this.queryBean, doc, sc.score);

        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

    }

    // -------- [[End Of Inner Class HitsIterator]] --------
}
