package uk.co.nickthecoder.ichneutae;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

public class HTMLParser implements Parser
{
    private static final Logger logger = LogManager.getLogger(HTMLParser.class);

    private Ichneutae ichneutae;

    private String title;

    private StringBuffer content;

    private HtmlCleaner cleaner;

    public HTMLParser(Ichneutae ichneutae)
    {
        this.ichneutae = ichneutae;

        cleaner = new HtmlCleaner();
        // CleanerProperties props = cleaner.getProperties();
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getContent()
    {
        return this.content.toString().replaceAll("\\s\\s+", " ");
    }

    public void parse(final URL url, InputStream is) throws IOException
    {
        this.title = "";
        this.content = new StringBuffer();

        TagNode node = this.cleaner.clean(is);

        node.traverse(new TagNodeVisitor()
        {

            public boolean visit(TagNode tagNode, HtmlNode htmlNode)
            {
                if (htmlNode instanceof TagNode) {
                    TagNode tag = (TagNode) htmlNode;
                    String tagName = tag.getName();
                    if ("a".equals(tagName)) {
                        String href = tidyHTML(tag.getAttributeByName("href"));
                        if (href != null) {
                            followLink(url, href);
                        }
                    }
                } else if (htmlNode instanceof ContentNode) {
                    ContentNode contentNode = ((ContentNode) htmlNode);
                    if ("title".equals(tagNode.getName())) {
                        HTMLParser.this.title = tidyHTML(contentNode.getContent());
                    } else {
                        // We need to add whitespace between html blocks that have no whitespace between them.
                        if (HTMLParser.this.content.length() > 0) {
                            HTMLParser.this.content.append( " " );
                        }
                        HTMLParser.this.content.append( tidyHTML( contentNode.getContent() ));
                    }
                }
                // tells visitor to continue traversing the DOM tree
                return true;
            }

        });

    }

    private String tidyHTML(String html)
    {
        if (html == null) {
            return "";
        }
        return StringEscapeUtils.unescapeHtml3(html.trim());
    }

    private void followLink(URL context, String link)
    {
        try {
            this.ichneutae.addUrl(new URL(context, link));
        } catch (MalformedURLException e) {
            logger.info("MalformedURL from " + context + " : '" + link + "'");
        }
    }
}
