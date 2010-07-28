/*
 File: CytoscapeInit.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.dialogs.logger.LoggerDialog;
import cytoscape.init.CyInitParams;
import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogger;
import cytoscape.logger.LogLevel;
import cytoscape.plugin.PluginManager;
import cytoscape.plugin.DownloadableInfo;
import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.ThemeInfo;
import cytoscape.plugin.ManagerException;
import cytoscape.plugin.Category;
import cytoscape.plugin.PluginStatus;
import cytoscape.util.FileUtil;
import cytoscape.util.NestedNetworkViewUpdater;
import cytoscape.util.shadegrown.WindowUtilities;
import cytoscape.view.CyNetworkView;

import org.cytoscape.equations.EqnParser;
import org.cytoscape.equations.Parser;


/**
 * <p>
 * Cytoscape Init is responsible for starting Cytoscape in a way that makes
 * sense.
 * </p>
 * <p>
 * The comments below are more hopeful than accurate. We currently do not
 * support a "headless" mode (meaning there is no GUI). We do, however, hope to
 * support this in the future.
 * </p>
 *
 * <p>
 * The two main modes of running Cytoscape are either in "headless" mode or in
 * "script" mode. This class will use the command-line options to figure out
 * which mode is desired, and run things accordingly.
 * </p>
 *
 * The order for doing things will be the following:<br>
 * <ol>
 * <li>deterimine script mode, or headless mode</li>
 * <li>get options from properties files</li>
 * <li>get options from command line ( these overwrite properties )</li>
 * <li>Load all Networks</li>
 * <li>Load all data</li>
 * <li>Load all Plugins</li>
 * <li>Initialize all plugins, in order if specified.</li>
 * <li>Start Desktop/Print Output exit.</li>
 * </ol>
 *
 * @since Cytoscape 1.0
 * @author Cytoscape Core Team
 */
public class CytoscapeInit implements PropertyChangeListener {
	private static final String SPLASH_SCREEN_LOCATION = "/cytoscape/images/CytoscapeSplashScreen.png";
	private static Properties properties;
	private static Properties visualProperties;
	private static CyLogger logger = null;
	private static NestedNetworkViewUpdater nestedNetworkViewUpdater;

	static {
		logger = CyLogger.getLogger(CytoscapeInit.class);
		logger.info("CytoscapeInit static initialization");
		initProperties();
	}

	private static CyInitParams initParams;

	// Most-Recently-Used directories and files
	private static Properties mrud;
	private static File mruf;

	// Error message
	private static String ErrorMsg = "";

	private PluginManager mgr;

	/**
	 * Creates a new CytoscapeInit object.
	 */
	public CytoscapeInit() {
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.CYTOSCAPE_EXIT, this);
	}

	/**
	 * Cytoscape Init must be initialized using the command line arguments.
	 *
	 * @param params  the arguments from the command line
	 * @return false, if we fail to initialize for some reason
	 */
	public boolean init(CyInitParams params) {
		long begintime = System.currentTimeMillis();
		if (logger == null)
			logger = CyLogger.getLogger(CytoscapeInit.class);

		try {
			initParams = params;

			// setup properties
			initProperties();

			// Reinitialize so we can pick up the debug flag
			logger = CyLogger.getLogger(CytoscapeInit.class);

			properties.putAll(initParams.getProps());
			visualProperties.putAll(initParams.getVizProps());

			// Build the OntologyServer.
			Cytoscape.buildOntologyServer();
			
			// Instantiate a NestedNetworkViewUpdater
			nestedNetworkViewUpdater = new NestedNetworkViewUpdater();
			
			// AJK: 02/22/2010 make Semantics.INTERACTION not user Editable
			CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
			edgeAttrs.setUserEditable(Semantics.INTERACTION, false);

			initPluginManager();

			// see if we are in headless mode
			// show splash screen, if appropriate

			// Initialize as GUI mode
			if ((initParams.getMode() == CyInitParams.GUI)
			    || (initParams.getMode() == CyInitParams.EMBEDDED_WINDOW)) {
				final ImageIcon image = new ImageIcon(Cytoscape.class
				                                          .getResource(SPLASH_SCREEN_LOCATION));
				WindowUtilities.showSplash(image, 8000);

				// Register the console logger, if we're not told not to
				String consoleLogger = properties.getProperty("logger.console");
				if (consoleLogger == null || Boolean.parseBoolean(consoleLogger)) {
					logger.addLogHandler(ConsoleLogger.getLogger(), LogLevel.LOG_DEBUG);
				} else {
					logger.info("Log information can now be found in the Help->Error Console menu");
				}

				// Register the logger dialog as a log handler
				logger.addLogHandler(LoggerDialog.getLoggerDialog(), LogLevel.LOG_DEBUG);

				// Create Desktop. This includes Vizmapper GUI initialization.
				Cytoscape.getDesktop();

				// set the wait cursor
				Cytoscape.getDesktop().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				setUpAttributesChangedListener();
			} else {
				// Force logging to the console
				logger.addLogHandler(ConsoleLogger.getLogger(), LogLevel.LOG_DEBUG);
			}

			logger.info("init mode: " + initParams.getMode());

			logger.info("loading plugins....");
			loadPlugins();

			logger.info("loading session...");
			boolean sessionLoaded = false;
			if ((initParams.getMode() == CyInitParams.GUI)
			    || (initParams.getMode() == CyInitParams.EMBEDDED_WINDOW)) {
				loadSessionFile();
				sessionLoaded = true;
			}

			logger.info("loading networks...");
			loadNetworks();

			logger.info("loading attributes...");
			loadAttributes();

			logger.info("loading expression files...");
			loadExpressionFiles();

			if ((initParams.getMode() == CyInitParams.GUI)
			    || (initParams.getMode() == CyInitParams.EMBEDDED_WINDOW)) {
				if(sessionLoaded == false) {
					logger.info("Initializing VizMapper...");
					initVizmapper();
				}
			}
		} finally {
			// Always restore the cursor and hide the splash, even there is
			// exception
			if ((initParams.getMode() == CyInitParams.GUI)
			    || (initParams.getMode() == CyInitParams.EMBEDDED_WINDOW)) {
				WindowUtilities.hideSplash();
				Cytoscape.getDesktop().setCursor(Cursor.getDefaultCursor());

				// to clean up anything that the plugins have messed up
				Cytoscape.getDesktop().repaint();
			}
		}

		long endtime = System.currentTimeMillis() - begintime;
		logger.info("Cytoscape initialized successfully in: " + endtime + " ms");
		Cytoscape.firePropertyChange(Cytoscape.CYTOSCAPE_INITIALIZED, null, null);
		
		return true;
	}

	/**
	 * Returns the CyInitParams object used to initialize Cytoscape.
	 */
	public static CyInitParams getCyInitParams() {
		return initParams;
	}

	/**
	 * Returns the properties used by Cytoscape, the result of cytoscape.props
	 * and command line options.
	 */
	public static Properties getProperties() {
		return properties;
	}

	/**
	 * @return the most recently used directory
	 */
	public static File getMRUD() {
		return new File(mrud.getProperty("fileOpenLocation", System.getProperty("user.home")));
	}

	/**
	 * @return the most recently used file
	 */
	public static File getMRUF() {
		return mruf;
	}

	/**
	 * @param mrud_new  the most recently used directory
	 */
	public static void setMRUD(final File mrud_new) {
		mrud.setProperty("fileOpenLocation", mrud_new.toString());
	}

	/**
	 * @param mruf_new  the most recently used file
	 */
	public static void setMRUF(File mruf_new) {
		mruf = mruf_new;
	}

	/**
	 * Within the .cytoscape directory create a version-specific directory
	 *
	 * @return the directory ".cytoscape/[cytoscape version]
	 */
	public static File getConfigVersionDirectory() {
		final File parent = getConfigDirectory();

		File VersionDir = new File(parent, (new CytoscapeVersion()).getMajorVersion());
		VersionDir.mkdir();

		return VersionDir;
	}

	/**
	 * If .cytoscape directory does not exist, it creates it and returns it
	 *
	 * @return the directory ".cytoscape" in the users home directory.
	 */
	public static File getConfigDirectory() {

		try {
			String dirName = properties.getProperty("alternative.config.dir",
			                                        System.getProperty("user.home"));
			File parent_dir = new File(dirName, ".cytoscape");

			if (parent_dir.mkdir())
				logger.info("Parent_Dir: " + parent_dir + " created.");

			return parent_dir;
		} catch (Exception e) {
			logger.warn("error getting config directory", e);
		}

		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param file_name
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static File getConfigFile(String file_name) {
		try {
			File parent_dir = getConfigDirectory();
			File file = new File(parent_dir, file_name);

			if (file.createNewFile())
				logger.info("Config file: " + file + " created.");

			return file;
		} catch (Exception e) {
			logger.warn("error getting config file:" + file_name, e);
		}

		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static Properties getVisualProperties() {
		return visualProperties;
	}

    /**
     * Load the Properties found in a given given file into a given Properties object.
     * @param defaultName the name of the properties file to use (.e.g., "cytoscape.props").
     * @param props the Properties object in which to insert the
     * property information found within defaultName.
     */
	public static void loadStaticProperties(String defaultName, Properties props) {
	    // TODO: This should probably be removed, since if
	    //       props==null, then the props set here will be lost:
		if (props == null) {
			logger.info("input props is null");
			props = new Properties();
		}

		String tryName = "";

		try {
			// load the props from the jar file
			tryName = "cytoscape.jar";

			// This somewhat unusual way of getting the ClassLoader is because
			// other methods don't work from WebStart.
			ClassLoader cl = Thread.currentThread().getContextClassLoader();

			URL vmu = null;

			if (cl != null)
				vmu = cl.getResource(defaultName);
			else
				logger.warn("ClassLoader for reading cytoscape.jar is null");

			if (vmu != null)
				// We'd like to use URLUtil.getBasicInputStream() to get
				// InputStream, but it is too early in the initialization of
				// Cytoscape and vmu is most likely out of a local resource, so
				// get
				// it directly:
				props.load(vmu.openStream());
			else
				logger.warn("couldn't read " + defaultName + " from " + tryName);

			// load the props file from $HOME/.cytoscape
			tryName = "$HOME/.cytoscape";

			File vmp = CytoscapeInit.getConfigFile(defaultName);

			if (vmp != null)
				props.load(new FileInputStream(vmp));
			else
				logger.warn("couldn't read " + defaultName + " from " + tryName);
		} catch (IOException ioe) {
			logger.warn("couldn't open '" + tryName + " " + defaultName
			             + "' file: "+ioe.getMessage()+" - creating a hardcoded default", ioe);
		}
	}

	private static void loadExpressionFiles() {
		// load expression data if specified
		List ef = initParams.getExpressionFiles();

		if ((ef != null) && (ef.size() > 0)) {
			for (Iterator iter = ef.iterator(); iter.hasNext();) {
				String expDataFilename = (String) iter.next();

				if (expDataFilename != null) {
					try {
						Cytoscape.loadExpressionData(expDataFilename, true);
					} catch (Exception e) {
						logger.error("Couldn't open expression file: '"+expDataFilename+"': "+e.getMessage(), e);
					}
				}
			}
		}
	}

	private boolean loadSessionFile() {
		String sessionFile = initParams.getSessionFile();

		try {
			String sessionName = "";

			if (sessionFile != null) {

				CytoscapeSessionReader reader = null;

				if (sessionFile.matches(FileUtil.urlPattern)) {
					URL u = new URL(sessionFile);
					reader = new CytoscapeSessionReader(u);
					sessionName = u.getFile();
				} else {
					Cytoscape.setCurrentSessionFileName(sessionFile);

					final File shortName = new File(sessionFile);
					String absolutePath = shortName.getAbsolutePath();
					if (absolutePath.startsWith("/"))
						absolutePath = "/" + absolutePath;
										
					final URL sessionURL = new URL("file:/" + absolutePath.replace("%", "%25"));
					
					reader = new CytoscapeSessionReader(sessionURL);					
				}

				if (reader != null) {
					reader.read();
					Cytoscape.getDesktop()
					         .setTitle("Cytoscape Desktop (Session Name: " + sessionName + ")");
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("couldn't create session from file '" + sessionFile + "': "+e.getMessage(), e);
		} finally {
			Cytoscape.getDesktop().getVizMapperUI().initVizmapperGUI();
			System.gc();
		}

		return false;
	}

	private static void initProperties() {
		if (properties == null) {
			properties = new Properties();
			loadStaticProperties("cytoscape.props", properties);
		}

		if (visualProperties == null) {
			visualProperties = new Properties();
			loadStaticProperties("vizmap.props", visualProperties);
		}

		if (mrud == null) {
			mrud = new Properties();
			final File propsFile = new File(CytoscapeInit.getConfigVersionDirectory(), "mostRecentlyUsedDirectory.props");
			if (propsFile != null) {
				try {
					final FileInputStream inputStream = new FileInputStream(propsFile);
					mrud.load(inputStream);
				} catch (final IOException e) {
					// Intentionally empty!
				}
			}
		}
	}

	private void setUpAttributesChangedListener() {
		/*
		 * This cannot be done in CytoscapeDesktop construction (like the other
		 * menus) because we need CytoscapeDesktop created first. This is
		 * because CytoPanel menu item listeners need to register for CytoPanel
		 * events via a CytoPanel reference, and the only way to get a CytoPanel
		 * reference is via CytoscapeDeskop:
		 * Cytoscape.getDesktop().getCytoPanel(...)
		 * Cytoscape.getDesktop().getCyMenus().initCytoPanelMenus(); Add a
		 * listener that will apply vizmaps every time attributes change
		 */
		final PropertyChangeListener attsChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED)) {
					// apply vizmaps
					final CyNetworkView currentView = Cytoscape.getCurrentNetworkView();
					if (currentView != Cytoscape.getNullNetworkView())
						Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
				}
			}
		};

		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(attsChangeListener);
	}

	// Load all requested networks
	private void loadNetworks() {
		for (Iterator i = initParams.getGraphFiles().iterator(); i.hasNext();) {
			String net = (String) i.next();
			logger.info("Load: " + net);

			CyNetwork network = null;

			boolean createView = false;

			if ((initParams.getMode() == CyInitParams.GUI)
			    || (initParams.getMode() == CyInitParams.EMBEDDED_WINDOW))
				createView = true;

			if (net.matches(FileUtil.urlPattern)) {
				try {
					network = Cytoscape.createNetworkFromURL(new URL(net), createView);
				} catch (Exception mue) {
					logger.error("Couldn't load network from URL '"+net+"': " + mue.getMessage(), mue);
				}
			} else {
				try {
					network = Cytoscape.createNetworkFromFile(net, createView);
				} catch (Exception mue) {
					logger.error("Couldn't load network from file '"+net+"': " + mue.getMessage(), mue);
				}
			}

			// This is for browser and other plugins.
			Object[] ret_val = new Object[3];
			ret_val[0] = network;
			ret_val[1] = net;
			ret_val[2] = new Integer(0);

			Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, ret_val);
		}
	}

	// load any specified data attribute files
	private void loadAttributes() {
		try {
			Cytoscape.loadAttributes((String[]) initParams.getNodeAttributeFiles()
			                                              .toArray(new String[] {  }),
			                         (String[]) initParams.getEdgeAttributeFiles()
			                                              .toArray(new String[] {  }));
		} catch (Throwable ex) {
			logger.error("failure loading specified attributes: "+ex.getMessage(), ex);
		}
	}

	private void initVizmapper() {
		Cytoscape.getDesktop().getVizMapperUI().initVizmapperGUI();
	}
	
	/** 
	 * This is separated from the rest of the plugin manager because by getting 
	 * the manager early, it can test for webstart before menus are created. 
	 * This is a little hacky.
	 */
	private void initPluginManager() {
		try {
			mgr = PluginManager.getPluginManager();
		} catch (Throwable tp) {
			logger.warn("Failed to start plugin manager.", tp);
		}
	}

	private void loadPlugins() {
		try {
			try {
				logger.info("Updating plugins...");
				mgr.delete();
			} catch (ManagerException me) {
				logger.warn("Error updating plugins: "+me.getMessage(), me);
			}

			mgr.install();

			//
			// TODO smart plugin loading. If there are multiple of the same
			// plugin (this will only work in the .cytoscape directory) load the
			// newest version first. Should be able to examine the directories
			// for this information. All installed plugins are named like
			// 'MyPlugin-1.0' currently this isn't necessary as old version are
			// not kept around
			//
			List<String> installedPlugins = new ArrayList<String>();

			// load from those listed on the command line
			installedPlugins.addAll(initParams.getPlugins());

			// Get all directories where plugins have been installed
			// going to have to be a little smart...themes contain their plugins
			// in subdirectories
			for (DownloadableInfo dInfo : mgr.getDownloadables(PluginStatus.CURRENT)) {
				if (dInfo.getCategory().equals(Category.CORE.getCategoryText()))
					continue;

				// TODO get rid of switches
				switch (dInfo.getType()) { 
					case PLUGIN:
						installedPlugins.add(((PluginInfo) dInfo).getInstallLocation());

						break;

					case THEME:
						ThemeInfo tInfo = (ThemeInfo) dInfo;
						for (PluginInfo plugin : tInfo.getPlugins()) 
							installedPlugins.add(plugin.getInstallLocation());

						break;
				}
			}

			mgr.loadPlugins(installedPlugins);

			for (Throwable t : mgr.getLoadingErrors()) 
				logger.warn("Plugin loading error: "+t.toString(),t);

			mgr.clearErrorList();

		} catch (Exception e) {
			logger.error("Plugin system initialization error: "+e.toString(),e);
		}
	}

	public void propertyChange(final PropertyChangeEvent e) {
		if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_EXIT)) {
			final File propsFile = new File(CytoscapeInit.getConfigVersionDirectory(), "mostRecentlyUsedDirectory.props");
			try {
				mrud.store(new FileOutputStream(propsFile), "");
			}
			catch (final IOException ioe) {
				System.err.println("Failed to save \"mostRecentlyUsedDirectory.props\"!");
			}
		}
	}
}
