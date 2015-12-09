package uk.co.nickthecoder.ichneutae.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import uk.co.nickthecoder.ichneutae.Configuration;

/**
 * Placed into request scope to hold all information about a query.
 */

public class QueryBean
{
    private static final Logger logger = LogManager.getLogger(QueryBean.class);

    public String queryString;

    public Query query;

    public List<String> keywords;

    public QueryBean(String queryString, String[] categoryCodes)
    {
        logger.trace("Query string : '" + queryString + "'");

        this.queryString = queryString;
        this.keywords = new ArrayList<String>();

        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        StringTokenizer st = new StringTokenizer(queryString);
        while (st.hasMoreTokens()) {

            BooleanClause.Occur occur = Configuration.defaultOccur;
            String word = st.nextToken();

            if (word.startsWith("-")) {
                occur = BooleanClause.Occur.MUST_NOT;
                word = word.substring(1);
            } else if (word.startsWith("+")) {
                occur = BooleanClause.Occur.MUST;
                word = word.substring(1);
            } else if (word.startsWith("?")) {
                occur = BooleanClause.Occur.SHOULD;
                word = word.substring(1);
            }

            logger.trace("Query word : '" + word + "'");

            String keyword = Configuration.analyzeWord(word);
            this.keywords.add(keyword);
            logger.trace("Query term : '" + keyword + "'");

            builder.add(new TermQuery(new Term("content", keyword)), occur);

            if (occur != BooleanClause.Occur.MUST) {
                builder.add(new TermQuery(new Term("title", keyword)), occur);
            }
        }

        Query textQuery = builder.build();

        if ((categoryCodes != null) && (categoryCodes.length != 0)) {
            this.query = andQuery(textQuery, createCategoryQuery(categoryCodes));
        } else {
            this.query = textQuery;
        }
    }

    public BooleanQuery createCategoryQuery(String[] categoryCodes)
    {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        for (String categoryCode : categoryCodes) {
            builder.add(new TermQuery(new Term("category", categoryCode)), BooleanClause.Occur.SHOULD);
        }

        return builder.build();
    }

    public BooleanQuery andQuery(Query... queries)
    {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        for (Query q : queries) {
            builder.add(q, BooleanClause.Occur.MUST);
        }

        return builder.build();
    }

}
