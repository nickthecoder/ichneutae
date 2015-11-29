package uk.co.nickthecoder.ichneutae.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.nickthecoder.ichneutae.Category;
import uk.co.nickthecoder.ichneutae.Configuration;
import uk.co.nickthecoder.ichneutae.StorageReader;
import uk.co.nickthecoder.ichneutae.model.CategoryChoice;
import uk.co.nickthecoder.ichneutae.model.HitsBean;
import uk.co.nickthecoder.ichneutae.model.QueryBean;

public class Search extends BaseServlet
{
    private static final Logger logger = LogManager.getLogger(Search.class);

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ensureConfigurationLoaded();

        StorageReader reader = null;

        try {
            String queryString = request.getParameter("q");

            List<CategoryChoice> categoryChoices = new ArrayList<CategoryChoice>();
            String[] chosenCodes = request.getParameterValues("category");
            for (Category category : Configuration.categories) {
                categoryChoices.add(new CategoryChoice(category, chosenCategory(chosenCodes, category.code)));
            }
            request.setAttribute( "categoryChoices", categoryChoices );

            request.setAttribute("q", queryString == null ? "" : queryString);
            if (queryString != null) {
                logger.info("Searching for : " + queryString);
                int pageNumber = getIntParameter(request, "page", 1);

                reader = new StorageReader();
                reader.open();
                QueryBean queryBean = new QueryBean(queryString, chosenCodes);
                HitsBean hitsBean = new HitsBean(queryBean, reader, pageNumber);

                request.setAttribute("query", queryBean);
                request.setAttribute("hits", hitsBean);
                request.setAttribute("categories", Configuration.categories);
            }

            super.doGet(request, response);
            
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private boolean chosenCategory(String[] chosenCodes, String code)
    {
        if ( chosenCodes == null) {
            return false;
        }
        
        for (String chosenCode : chosenCodes) {
            if (code.equals(chosenCode)) {
                return true;
            }
        }
        return false;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    @Override
    public String getPage(HttpServletRequest request, HttpServletResponse respons)
    {
        return "/search.jsp";
    }

}
