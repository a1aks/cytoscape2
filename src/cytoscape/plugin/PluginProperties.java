/**
 * 
 */
package cytoscape.plugin;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

/**
 * This class reads the plugin.props file that is expected to be in 
 * each plugin jar file and turns it into a PluginInfo object for
 * the PluginManager
 */
public class PluginProperties extends Properties {
	private String configFileName = "plugin.props";
	private String errorMsg;
	
	/**
	 * Properties in the plugin.props file
	 */
	public enum PluginProperty {
		NAME("pluginName", true), DESCRIPTION("pluginDescription", true),
		VERSION("pluginVersion", true), CYTOSCAPE_VERSION("cytoscapeVersion", true),
		CATEGORY("pluginCategory", true),
		PROJECT_URL("projectURL", false), AUTHORS("pluginAuthorsIntsitutions", false),
		RELEASE_DATE("releaseDate", false), UNIQUE_ID("uniqueID", false);
		
		private String propText;
		private boolean requiredProp;
	
		private PluginProperty(String prop, boolean required) {
			propText = prop;
			requiredProp = required;
		}

		public String toString() {
			return propText + ":" + requiredProp;
		}
		
		public String getPropertyKey() {
			return propText;
		}
		
		public boolean isRequired() {
			return requiredProp;
		}
		
	}

	/**
	 * The plugin.props file is expected to be in the jar file under the package directory.  
	 * It will not be found if it is anywhere else.
	 * @param Plugin
	 * @throws IOException
	 */
	public PluginProperties(CytoscapePlugin Plugin) throws IOException {
		String PackageName = Plugin.getClass().getPackage().getName();
		PackageName = PackageName.replace('.', '/'); // the package name has to be in the directory structure form
		readPluginProperties(Plugin.getClass().getClassLoader().getResourceAsStream(PackageName + "/" +  configFileName));
	}
	
	private void readPluginProperties(InputStream is) throws IOException {
		if (is == null || is.available() == 0) {
			// throw an error!
			String Msg = "";
			if (is == null) {
				Msg = "input stream is null";
			} else if (is.available() == 0) {
				Msg = "0 bytes in input stream";
			}
	
			IOException Error = new IOException("Unable to load "
					+ configFileName + ": " + Msg);
			throw Error;
		} else {
			load(is);
		}
	}

	public PluginInfo getPluginInfoObject() throws ManagerException {
		if (!expectedPropertiesPresent()) {
			throw new ManagerException("Required properties are missing from plugins.props file: " + errorMsg);
		}
		
		PluginInfo pi;
		if (containsKey(PluginProperty.UNIQUE_ID)) {
			pi = new PluginInfo(getProperty(PluginProperty.UNIQUE_ID.getPropertyKey()));
		} else {
			pi = new PluginInfo();
		}
		
		// required parameters
		pi.setName(getProperty(PluginProperty.NAME.getPropertyKey()));

		try {
			pi.setPluginVersion( Double.valueOf(getProperty(PluginProperty.VERSION.getPropertyKey())) );
		} catch (java.lang.NumberFormatException ne) { // skip it or set it to a default value??
			System.err.println(pi.getName() + " version is incorrectly formatted, format is: \\d+.\\d+. Version set to 0.1 to allow plugin to load");
			ne.printStackTrace();
			pi.setPluginVersion(0.1);
		}
		
		pi.setDescription(getProperty(PluginProperty.DESCRIPTION.getPropertyKey()));
		pi.setCategory(getProperty(PluginProperty.CATEGORY.getPropertyKey()));
		
		pi.setCytoscapeVersion(getProperty(PluginProperty.CYTOSCAPE_VERSION.getPropertyKey()));
		
		// optional parameters
		if (containsKey(PluginProperty.PROJECT_URL.getPropertyKey())) {
			pi.setProjectUrl(getProperty(PluginProperty.PROJECT_URL.getPropertyKey()));
		}
		
		if (containsKey(PluginProperty.AUTHORS.getPropertyKey())) {
			// split up the value and add each
			String AuthorProp = getProperty(PluginProperty.AUTHORS.getPropertyKey());
			String[] AuthInst = AuthorProp.split(";");

			for (String ai: AuthInst) {
				String[] CurrentAI = ai.split(":");
				if (CurrentAI.length != 2) {
					System.err.println("Author line '" + ai + "' incorrectly formatted. Please enter authors as 'Name1, Name2 and Name3: Institution");
					continue;
				}
				pi.addAuthor(CurrentAI[0], CurrentAI[1]);
			}
		}
		
		if (containsKey(PluginProperty.RELEASE_DATE.getPropertyKey())) {
			pi.setReleaseDate(getProperty(PluginProperty.RELEASE_DATE.getPropertyKey()));
		}
		return pi;
	}


	private boolean expectedPropertiesPresent() {
		for (PluginProperty pp : PluginProperty.values()) {
			if (pp.isRequired() && !containsKey(pp.getPropertyKey())) {
				errorMsg = pp.getPropertyKey();
				return false;
			}
		}
	return true;
	}
	
	
}
