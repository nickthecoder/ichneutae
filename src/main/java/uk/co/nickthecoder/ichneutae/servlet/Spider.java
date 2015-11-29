package uk.co.nickthecoder.ichneutae.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.nickthecoder.ichneutae.Ichneutae;

/**
 * Initiates a crawl of the web site(s).
 */
public class Spider extends BaseServlet
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(Spider.class);

    private static boolean spidering = false;

    protected void processGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute("running", spidering);
        super.processPost(request, response);
    }
    
    protected void processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.trace("Spider");

        request.setAttribute("running", spidering);

        if (spidering) {
            request.setAttribute("message", "Spider already in progress");
            
        } else {
            
            request.setAttribute("message", "Spider started.");
            new Thread() {
                public void run() {
                    try {
                        spidering = true;
                        Ichneutae ichneutae = new Ichneutae();
                        ichneutae.spider();
                    } catch (Exception e) {
                        logger.error("Spider failed " + e);
                    } finally {
                        spidering = false;
                    }
                    
                }
            }.start();
        }
        
        super.processPost(request, response);
    }

    @Override
    public String getPage(HttpServletRequest request, HttpServletResponse response)
    {
        return "/spider.jsp";
    }

}
