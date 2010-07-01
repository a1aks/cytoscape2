package cytoscape.visual.parsers;

import java.io.IOException;
import java.net.URL;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.CyCustomGraphicsParser;
import cytoscape.visual.customgraphic.CyCustomGraphicsParserFactory;
import cytoscape.visual.customgraphic.CyCustomGraphicsParserFactoryImpl;
import cytoscape.visual.customgraphic.URLImageCustomGraphics;
import cytoscape.visual.customgraphic.URLImageCustomGraphicsParser;

public class GraphicsParser implements ValueParser<CyCustomGraphics<?>> {
	
	private static final String NULL_CG = "cytoscape.visual.customgraphic.NullCustomGraphics";
	
	private static final CyLogger logger = CyLogger.getLogger();
	
	// Maybe injected from outside if we use DI framework.
	private final CyCustomGraphicsParserFactory parserFactory;
	
	public GraphicsParser() {
		super();
		
		parserFactory = new CyCustomGraphicsParserFactoryImpl();
		// Register default parser
		parserFactory.registerParser(URLImageCustomGraphics.class, new URLImageCustomGraphicsParser());
		
		// TODO: dynamically add parsers using listener.
	}


	public CyCustomGraphics<?> parseStringValue(String value) {
		return parse(value);
	}
	
	private CyCustomGraphics<?> parse(String value) {
		if(value == null || value.equals(NULL_CG))
			return null;
		
		// Special case:  URL String.
		try {
			final URL url = new URL(value);
			CyCustomGraphics<?> graphics = Cytoscape.getVisualMappingManager().getCustomGraphicsPool().getBySourceURL(url);
			if(graphics == null) {
				graphics = new URLImageCustomGraphics(url.toString());
				Cytoscape.getVisualMappingManager().getCustomGraphicsPool().addGraphics(graphics.hashCode(), graphics, url);
			}
			return graphics;
		} catch (IOException e) {
			logger.warn("Invalid URL found. This will be ignored: " + value);
		}
		
		
		
		final String[] parts = value.split(",");
		// Extract class
		String className = parts[0];
		final CyCustomGraphicsParser parser = parserFactory.getParser(className);
		
		if(parser == null)		
			return null;
		
		return parser.getInstance(value);
	}

}
