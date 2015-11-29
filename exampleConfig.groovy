import uk.co.nickthecoder.ichneutae.*;
import uk.co.nickthecoder.ichneutae.filter.*;

import java.nio.file.Paths;

import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

logger.trace( "Configuration begin : exampleConfig.groovy" );

logger.trace( "Configuring Lucene" );
Configuration.index = new NIOFSDirectory( Paths.get("exampleIndex" ) );
Configuration.analyzer = new StandardAnalyzer();

// Remove documents not found during spidering.
Configuration.purgeDays = 0;

// Uncomment this line if all query words must match. The default is SHOULD
// Configuration.defaultOccur = org.apache.lucene.search.BooleanClause.Occur.MUST;

logger.trace( "Configuring filters" );
Configuration.setFilter(
    (
        URLFilter.equalsHost("nickthecoder.co.uk")
        .and(URLFilter.regexPath("/recipe/.*"))
    )
    .exclude(
        URLFilter.regexPath(".*/view/wiki/.*")
        .or(URLFilter.regexPath(".*/info/.*"))
        .or(URLFilter.regexPath(".*/edit/.*"))
    )
);

logger.trace( "Configuring origins" );
Configuration.setOrigins( "http://nickthecoder.co.uk/recipe/view" );

logger.trace( "Configuring categories" );

Configuration.add( new Category( "ingredient", "Ingredients" )
    .setFilter(
        URLFilter.regexPath("/recipe/view/ingredient/.*")
    )
);
Configuration.add( new Category( "recipe", "Recipes" )
    .setFilter(
        URLFilter.regexPath("/recipe/view/[^/]*")
    )
);


logger.trace( "Configuring look and feel" );
Configuration.hitsPerPage = 10;
Configuration.template = "/templates/default/layout.jsp";

logger.trace( "Configuration complete" );
