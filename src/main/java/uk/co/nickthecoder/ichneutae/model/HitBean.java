package uk.co.nickthecoder.ichneutae.model;

import org.apache.lucene.document.Document;

import uk.co.nickthecoder.ichneutae.Configuration;
import uk.co.nickthecoder.webwidgets.filter.Filter;
import uk.co.nickthecoder.webwidgets.util.SummaryMaker;

/**
 * A single search result i.e. a hit
 */

public class HitBean
{
    public QueryBean queryBean;

    public float score;

    private Document document;

    public HitBean(QueryBean queryBean, Document document, float score)
    {
        this.queryBean = queryBean;
        this.document = document;
        this.score = score;
    }

    public int getScorePercentage()
    {
        return (int) (this.score * 100);
    }

    protected String getField(String fieldName)
    {
        try {
            String result = document.get(fieldName);
            return result == null ? "" : result;
        } catch (Exception e) {
            return "";
        }
    }

    public String getTitle()
    {
        return getField("title");
    }

    public String getURLString()
    {
        return getField("url");
    }

    public String getMimeType()
    {
        return getField("mimetype");
    }

    public String getContent()
    {
        return getField("content");
    }

    public String[] getCategoryCodes()
    {
        return this.document.getValues("category");
    }

    public SummaryMaker getSummary()
    {
        return new SummaryMaker(getContent(), new Filter<String>() {

            @Override
            public boolean accept(String word)
            {
                String aword = Configuration.analyzeWord( word );
                for ( String keyword: queryBean.keywords ) {
                    if ( keyword.equals(aword) ) {
                        return true;
                    }
                }
                return false;
            }
            
        });
    }
    
    public String toString()
    {
        return "URL: " + this.getURLString() + "\nTitle: " + this.getTitle();
    }

}
