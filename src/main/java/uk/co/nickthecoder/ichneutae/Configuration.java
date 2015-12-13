package uk.co.nickthecoder.ichneutae;

import groovy.lang.GroovyShell;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.store.Directory;

import uk.co.nickthecoder.webwidgets.filter.AndFilter;
import uk.co.nickthecoder.webwidgets.filter.FalseFilter;
import uk.co.nickthecoder.webwidgets.filter.Filter;
import uk.co.nickthecoder.webwidgets.filter.URLFilter;
import uk.co.nickthecoder.webwidgets.filter.URLHostFilter;
import uk.co.nickthecoder.webwidgets.filter.URLPathFilter;

/**
 * Holds configuration information in static fields. The configuration can be
 * loaded using a groovy script (see {@link #load(String)}).
 */
public class Configuration
{
    private static final Logger logger = LogManager.getLogger(Configuration.class);

    /**
     * A list of URL, which are the starting points when spidering (creating the
     * Lucene index).
     */
    public static List<URL> origins = new ArrayList<URL>();

    public static List<Category> categories = new ArrayList<Category>();

    /**
     * Decides whether or not a given URL should be opened, parsed and added to
     * the index. Typically you will want to use an {@link AndFilter}, with a
     * {@link URLHostFilter} and some {@link URLPathFilter}s.
     * 
     * Note. This filter is in addition to filtering done by robots.txt file.
     */
    public static Filter<URL> filter = new FalseFilter<URL>();

    /**
     * The number of hits (query results) per page.
     */
    public static int hitsPerPage = 20;

    /**
     * The name of the jsp page which contains the 'tiles' layout. This allows
     * for easy changes to the look and feel of the page.
     */
    public static String template = "/templates/default/layout.jsp";

    /**
     * The Lucene index
     */
    public static Directory index;

    /**
     * The lucene analyzer
     */
    public static Analyzer analyzer;

    /**
     * The default behaviour when more than one word is given as a query string.
     * Use SHOULD if one or more words must match. Use MUST if all words must
     * match.
     * 
     * The user can force "MUST" by prefixing each word with a plus "+", and can
     * force "SHOULD" by prefixing each word with a question mark "?".
     */
    public static BooleanClause.Occur defaultOccur = BooleanClause.Occur.SHOULD;

    /**
     * At the end of spidering, a purge is performed, which deletes documents
     * from the index which were not found during spidering. A value of 0 will
     * purge all old documents not found during the current spidering. However,
     * maybe some pages were only <b>temporarily</b> unavailable, and should be
     * kept in the index.
     * 
     * A negative value will disable purging (This is the default).
     * 
     * Example. If you spider once a week, then a value of 15 will purge
     * documents which have been unavailable 3 times in a row.
     */
    public static int purgeDays = -1;

    /**
     * There is no need to create a Configuration object, use it in a static
     * context only.
     */
    private Configuration()
    {
    }

    /**
     * Load configuration info using a groovy script.
     * 
     * A variable called "logger" is added to the shell, which is a log4j2
     * logger. So the configuration script can contain logging such as :
     * 
     * <pre>
     * logger.trace(&quot;Beginning foo...&quot;);
     * </pre>
     * 
     * and this will be displayed if log4j2 has been set up like so :
     * 
     * <pre>
     * {@code
     *     <Logger name="uk.co.nickthecoder.ichneutae.Configuration" level="TRACE"/>
     * }
     * </pre>
     */
    public static void load(String groovyFilename) throws Exception
    {
        logger.info("Loading configuration file " + groovyFilename );
        InputStream input = new FileInputStream(groovyFilename);

        GroovyShell shell = new GroovyShell();
        shell.setVariable("logger", logger);
        shell.evaluate(new InputStreamReader(input));
    }

    public static void add(Category category)
    {
        categories.add(category);
    }

    public static void setFilter(Filter<URL> filter)
    {
        Configuration.filter = filter;
    }

    public static void setOrigins(String... origins) throws MalformedURLException
    {
        for (String origin : origins) {
            Configuration.origins.add(new URL(origin));
        }
    }

    public static String analyzeWord(String word)
    {
        Reader reader = new StringReader(word);
        TokenStream tokenStream = null;
        try {
            tokenStream = Configuration.analyzer.tokenStream("content", reader);
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                return charTermAttribute.toString();
            }
        } catch (Exception e) {
            logger.error("Failed to filter a keyword. " + e);
        } finally {
            try {
                if (tokenStream != null) {
                    tokenStream.end();
                    tokenStream.close();
                }
                reader.close();
            } catch (Exception e) {
                // Do nothing
                logger.error("Failed to close during analyzeWord " + e);
            }
        }
        return null;
    }

    /**
     * This is an example of how to configure Ichneutae.
     * 
     * @throws MalformedURLException
     */
    public static void example() throws MalformedURLException
    {

        Configuration.setFilter((URLFilter.equalsHost("nickthecoder.co.uk").or(
                        URLFilter.equalsHost("nickandnalin.co.uk")).or(URLFilter.regexPath("/recipe/.*")))
                        .exclude(URLFilter.regexPath(".*/edit/.*").or(URLFilter.regexPath(".*/info/.*"))
                                        .or(URLFilter.regexPath(".*/raw/.*")).or(URLFilter.regexPath(".*/delete/.*"))
                                        .or(URLFilter.regexPath(".*/rename/.*"))));

        Configuration.setOrigins("http://nickthecoder.co.uk/", "http://nickandnalin.co.uk/");

        Configuration.add(new Category("music", "Music").setFilter(URLFilter.regexPath("/gidea/listMusic.*").or(
                        URLFilter.regexPath("/gidea/showMusic.*"))));

        Configuration.add(new Category("recipe", "Recipes").setFilter(URLFilter.regexPath("/recipe/.*")));

    }
}
