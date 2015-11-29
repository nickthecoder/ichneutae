package uk.co.nickthecoder.ichneutae.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.nickthecoder.ichneutae.Configuration;

/**
 * The base class for all servlets in Ichneutae. Override processGet and/or
 * processPost rather than doGet and doPost.
 * 
 * Ensures that the configuration file has been loaded, and adds common info
 * into the request score.
 */
public abstract class BaseServlet extends HttpServlet
{
    private static final Logger logger = LogManager.getLogger(BaseServlet.class);

    private static final long serialVersionUID = 1L;

    private static boolean loadedConfiguration = false;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ensureConfigurationLoaded();
        request.setAttribute("template", Configuration.template);
        processGet(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ensureConfigurationLoaded();
        request.setAttribute("template", Configuration.template);
        processPost(request, response);
    }

    protected void forward(HttpServletRequest request, HttpServletResponse response, String page)
                    throws ServletException, IOException
    {
        RequestDispatcher rd = getServletContext().getRequestDispatcher(page);
        rd.forward(request, response);
    }

    protected void ensureConfigurationLoaded()
    {
        if (!loadedConfiguration) {
            String scriptFilename = getServletContext().getInitParameter("ichneutae.initscript");
            if (scriptFilename == null) {
                logger.error("Parameter ichneutae.initscript not specified. Using a default configuration.");
                scriptFilename = "exampleConfig.groovy";
            }
            try {
                Configuration.load(scriptFilename);
            } catch (Exception e) {
                logger.error("Failed to load groovy script : " + scriptFilename + ". " + e);
            }
            loadedConfiguration = true;
        }
    }

    protected int getIntParameter(HttpServletRequest request, String name, int defaultValue)
    {
        try {
            return Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            return defaultValue;
        }

    }

    protected void processGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException
    {
        forward(request, response, getPage(request, response));
    }

    protected void processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException
    {
        forward(request, response, getPage(request, response));
    }

    public abstract String getPage(HttpServletRequest request, HttpServletResponse response);

}
