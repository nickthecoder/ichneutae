package uk.co.nickthecoder.ichneutae.servlet;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.nickthecoder.ichneutae.Ichneutae;

/**
 * Updates the Lucene index for a single page. You can use this to keep the index up to date by calling
 * this whenever a web page is updated.
 * 
 * Takes a single parameter 'url'.
 */
public class Update extends BaseServlet
{
    private static final Logger logger = LogManager.getLogger(Update.class);

    private static final long serialVersionUID = 1L;

    protected void processPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        String urlString = request.getParameter("url");
        if ( urlString != null ) {
            try {
                logger.trace( "Updating page : " + urlString );
                Ichneutae ichneutae = new Ichneutae();
                URL url = new URL( urlString );
                ichneutae.index(url);
                request.setAttribute("message", "Updated");
                request.setAttribute("url", urlString);
            } catch (Exception e) {
                logger.error( "Failed to update URL : " + urlString + ". " + e );
            }
        }
        super.processPost(request, response);
    }

    @Override
    public String getPage(HttpServletRequest request, HttpServletResponse response)
    {
        return "/update.jsp";
    }

}
