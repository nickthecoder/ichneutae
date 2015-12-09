/*
A quick and easy way to test queries using Groovy as a scripting language.
*/
import uk.co.nickthecoder.ichneutae.*;
import uk.co.nickthecoder.ichneutae.model.*;

import java.nio.file.Paths;

import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

logger.trace( "Configuration begin : exampleConfig.groovy" );

logger.trace( "Configuring Lucene" );
Configuration.index = new NIOFSDirectory( Paths.get("exampleIndex" ) );
Configuration.analyzer = new EnglishAnalyzer();

Configuration.hitsPerPage=2;

logger.trace( "Testing" );
reader = new StorageReader();
reader.open();
queryBean = new QueryBean( "sugar" );
hitsBean = new HitsBean( queryBean, reader, 2 );

for ( hit in hitsBean.getHits() ) {
	logger.info( hit );
	for ( code in hit.categoryCodes ) {
	   logger.info( "Category : " + code );
   }
}
reader.close();
