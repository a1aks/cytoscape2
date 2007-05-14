/*
 File: PluginManager.java 
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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

package cytoscape.plugin;

import cytoscape.*;

import cytoscape.util.FileUtil;
import cytoscape.util.URLUtil;
import cytoscape.util.ZipUtil;
import cytoscape.task.TaskMonitor;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * @author skillcoy
 * 
 */
public class PluginManager {
	protected PluginTracker pluginTracker;

	private static PluginManager pluginMgr = null;

	private static File tempDir;

	private static Set<java.net.URL> pluginURLs;

	private static Set<String> loadedPlugins;

	private static HashMap<String, PluginInfo> initializedPlugins;

	private static Set<String> resourcePlugins;

	private static URLClassLoader classLoader;

	private static boolean usingWebstart;

	/**
	 * Replaces CytoscapeInit.getClassLoader()
	 * 
	 * @return URLClassLoader used to load plugins.
	 */
	public static URLClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * Replaces CytoscapeInit.getResourcePlugins()
	 * 
	 * @return Set<String> of resource plugins
	 */
	public static Set<String> getResourcePlugins() {
		return resourcePlugins;
	}

	/**
	 * Replaces CytoscapeInit.getPluginURLs()
	 * 
	 * @return Set<URL> of plugin URL's
	 */
	public static Set<java.net.URL> getPluginURLs() {
		return pluginURLs;
	}

	/**
	 * Returns true/false based on the System property. This is what is checked
	 * to find out if install/delete/download methods are permitted.
	 * 
	 * @return true if Cytoscape is in webstart
	 */
	public static boolean usingWebstartManager() {
		return usingWebstart;
	}

	/**
	 * Get the PluginManager object.
	 * 
	 * @return PluginManager
	 */
	public static PluginManager getPluginManager() {
		if (pluginMgr == null) {
			pluginMgr = new PluginManager(null);
		}
		return pluginMgr;
	}

	/**
	 * @param loc
	 *            Location of plugin download/install directory. If this method
	 *            is not called the default is .cytoscape/[cytoscape
	 *            version]/plugins
	 */
	public void setPluginManageDirectory(String loc) {
		tempDir = new File(loc);
	}

	public File getPluginManageDirectory() {
		return tempDir;
	}

	/*
	 * Just checks the system property 'javawebstart.version' which is only set
	 * when running as a webstart.
	 */
	private static void setWebstart() {
		System.out.println("set webstart");
		if (System.getProperty("javawebstart.version") != null
				&& System.getProperty("javawebstart.version").length() > 0) {
			System.out.println("USING WEBSTART: "
					+ System.getProperty("javawebstart.version"));
			usingWebstart = true;
		} else {
			usingWebstart = false;
		}
	}

	/**
	 * This should ONLY be used by tests!!
	 * 
	 * @param Tracker
	 * @return
	 */
	protected static PluginManager getPluginManager(PluginTracker Tracker) {
		if (pluginMgr == null) {
			pluginMgr = new PluginManager(Tracker);
		}
		return pluginMgr;
	}

	/**
	 * This is used in testing to isolate each test case. DO NOT USE THIS IN
	 * CYTOSCAPE RUNTIME CODE
	 */
	protected void resetManager() {
		pluginMgr = null;
	}

	// create plugin manager
	private PluginManager(PluginTracker Tracker) {
		setWebstart();
		try {
			if (Tracker != null) {
				pluginTracker = Tracker;
			} else {
				if (usingWebstart) {
					pluginTracker = new PluginTracker(File.createTempFile(
							"track_webstart_plugins_", ".xml"));
				} else {
//					pluginTracker = new PluginTracker(CytoscapeInit
//							.getConfigDirectory(), "track_plugins.xml");
						pluginTracker = new PluginTracker(CytoscapeInit.getConfigVersionDirectory(), "track_plugins.xml");
				}
			}
			tempDir = new File(CytoscapeInit.getConfigVersionDirectory(), "plugins");
//			tempDir = new File(CytoscapeInit.getConfigDirectory(),
//					CytoscapeVersion.version + File.separator + "plugins"
//							+ File.separator);

			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}
		} catch (java.io.IOException E) {
			E.printStackTrace(); // TODO do something useful with error
		}
		pluginURLs = new HashSet<java.net.URL>();
		loadedPlugins = new HashSet<String>();
		initializedPlugins = new HashMap<String, PluginInfo>();
		resourcePlugins = new HashSet<String>();
	}

	/**
	 * Get a list of plugins by status. CURRENT: currently installed INSTALL:
	 * plugins to be installed DELETE: plugins to be deleted
	 * 
	 * @param Status
	 * @return
	 */
	public List<PluginInfo> getPlugins(PluginTracker.PluginStatus Status) {
		return pluginTracker.getListByStatus(Status);
	}

	/**
	 * Calls the given url, expects document describing plugins available for
	 * download
	 * 
	 * @param Url
	 * @return List of PluginInfo objects
	 */
	public List<PluginInfo> inquire(String Url) throws IOException,
			org.jdom.JDOMException {
		List<PluginInfo> Plugins = null;
		PluginFileReader Reader = new PluginFileReader(Url);
		Plugins = Reader.getPlugins();
		return Plugins;
	}

	// TODO need to remove any plugin from the list that doesn't register this
	// round...
	/**
	 * Registers a currently installed plugin with tracking object. Only useful
	 * if the plugin was not installed via the install process.
	 * 
	 * @param Plugin
	 * @param JarFileName
	 */
	protected void register(CytoscapePlugin Plugin, String JarFileName) {
		PluginInfo InfoObj = null;
		try {
			if (Plugin.getPluginInfoObject() == null) {
				InfoObj = new PluginInfo();
				InfoObj.setName(Plugin.getClass().getName());
				InfoObj.setPluginClassName(Plugin.getClass().getName());
				
				if (JarFileName != null)
					InfoObj.addFileName(JarFileName);
			} else {
				InfoObj = Plugin.getPluginInfoObject();
				InfoObj.setPluginClassName(Plugin.getClass().getName());

				if (JarFileName != null) {
					InfoObj.addFileName(JarFileName);
				}
			}
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} finally {
   			initializedPlugins.put(InfoObj.getPluginClassName(), InfoObj);
   			// I think we can safely assume it's a jar file if it's registering
   			// since only CytoscapePlugin registers and at that point all we
   			// know is it's a jar
   			if (InfoObj.getFileType() == null) {
   				InfoObj.setFiletype(PluginInfo.FileType.JAR);
   			}
 				pluginTracker.addPlugin(InfoObj, PluginTracker.PluginStatus.CURRENT);
		}
	}

	private void cleanCurrentList() {
		List<PluginInfo> CurrentList = getPlugins(PluginTracker.PluginStatus.CURRENT);
		for (PluginInfo info : CurrentList) {
			if (!initializedPlugins.containsKey(info.getPluginClassName())) {
				pluginTracker.removePlugin(info,
						PluginTracker.PluginStatus.CURRENT);
			}
		}
	}

	/**
	 * Sets all plugins on the "install" list to "current"
	 */
	public void install() {
		for (PluginInfo info : getPlugins(PluginTracker.PluginStatus.INSTALL)) {
			install(info);
		}
	}

	/**
	 * Change the given plugin from "install" to "current" status
	 * 
	 * @param obj
	 */
	public void install(PluginInfo obj) {
		pluginTracker.removePlugin(obj, PluginTracker.PluginStatus.INSTALL);
		pluginTracker.addPlugin(obj, PluginTracker.PluginStatus.CURRENT);
	}

	/**
	 * Marks the given object for deletion the next time Cytoscape is restarted.
	 * 
	 * @param Obj
	 */
	public void delete(PluginInfo Obj) throws WebstartException {
		checkWebstart();
		pluginTracker.addPlugin(Obj, PluginTracker.PluginStatus.DELETE);
	}

	/**
	 * Takes all objects on the "to-delete" list and deletes them. This can only
	 * occur at start up, CytoscapeInit should be the only class to call this.
	 */
	public void delete() throws ManagerException, WebstartException {
		checkWebstart();

		String ErrorMsg = "Failed to delete all files for the following plugins:\n";
		List<String> DeleteFailed = new ArrayList<String>();

		List<PluginInfo> Plugins = pluginTracker
				.getListByStatus(PluginTracker.PluginStatus.DELETE);

		for (PluginInfo CurrentPlugin : Plugins) {
			boolean deleteOk = false;

			// needs the list of all files installed
			deleteOk = deletePlugin(CurrentPlugin);

			pluginTracker.removePlugin(CurrentPlugin,
					PluginTracker.PluginStatus.DELETE);

			pluginTracker.removePlugin(CurrentPlugin,
					PluginTracker.PluginStatus.CURRENT);

			if (!deleteOk) {
				DeleteFailed.add(CurrentPlugin.getName() + " "
						+ CurrentPlugin.getPluginVersion());
			}
		}

		// any files that failed to delete should get noted
		if (DeleteFailed.size() > 0) {
			for (String Msg : DeleteFailed) {
				ErrorMsg += ("-" + Msg + "\n");
			}

			throw new ManagerException(ErrorMsg);
		}
	}

	// removes the plugin, all files in it's install location will be removed
	private boolean deletePlugin(PluginInfo info) {
		boolean Deleted = false;
		if (info.getInstallLocation() != null && info.getInstallLocation().length() > 0) {
			File Installed = new File(info.getInstallLocation());
			Deleted = recursiveDeleteFiles(Installed);
		} else {
			for (String f: info.getFileList()) {
				Deleted = (new File(f)).delete();
			}
		}
		return Deleted;
	}

	private boolean recursiveDeleteFiles(File directory) {
		boolean delete = false;
		for (File f : directory.listFiles()) {
			if (f.isFile()) {
				delete = f.delete();
			}
			if (f.isDirectory()) {
				delete = recursiveDeleteFiles(f);
			}
		}
		delete = directory.delete();
		return delete;
	}

	private void checkWebstart() throws WebstartException {
		if (usingWebstart) {
			throw new WebstartException();
		}
	}

	/**
	 * Get list of plugins that would update the given plugin.
	 * 
	 * @param Plugin
	 * @return List<PluginInfo>
	 * @throws ManagerException
	 */
	public List<PluginInfo> findUpdates(PluginInfo Plugin) throws IOException,
			org.jdom.JDOMException {
		List<PluginInfo> UpdatablePlugins = new ArrayList<PluginInfo>();
		Set<PluginInfo> Seen = new HashSet<PluginInfo>();
		Seen.add(Plugin);

		if (Plugin.getProjectUrl() == null
				|| Plugin.getProjectUrl().length() <= 0) {
			return UpdatablePlugins;
		}

		for (PluginInfo New : inquire(Plugin.getProjectUrl())) {
			if (New.getID().equals(Plugin.getID())
					&& Plugin.isNewerPluginVersion(New)) {
				if (!Seen.contains(New)) {
					UpdatablePlugins.add(New);
				} else {
					Seen.add(New);
				}
			}
		}
		return UpdatablePlugins;
	}

	/**
	 * Finds the given version of the new object, sets the old object for
	 * deletion and downloads new object to temporary directory
	 * 
	 * @param Current
	 *            PluginInfo object currently installed
	 * @param New
	 *            PluginInfo object to install
	 * @throws IOException
	 *             Fails to download the file.
	 * @throws ManagerException
	 *             If the plugins don't match or the new one is not a newer
	 *             version.
	 */
	public void update(PluginInfo Current, PluginInfo New) throws IOException,
			ManagerException, WebstartException {
		update(Current, New, null);
	}

	/**
	 * Finds the given version of the new object, sets the old object for
	 * deletion and downloads new object to temporary directory
	 * 
	 * @param Current
	 *            PluginInfo object currently installed
	 * @param New
	 *            PluginInfo object to install
	 * @param taskMonitor
	 *            TaskMonitor for downloads
	 * @throws IOException
	 *             Fails to download the file.
	 * @throws ManagerException
	 *             If the plugins don't match or the new one is not a newer
	 *             version.
	 */
	public void update(PluginInfo Current, PluginInfo New,
			cytoscape.task.TaskMonitor taskMonitor) throws IOException,
			ManagerException, WebstartException {
		// find new plugin, download, add to install list
		if (Current.getProjectUrl() == null) {
			throw new ManagerException(
					Current.getName()
							+ " does not have a project url.\nCannot auto-update this plugin.");
		}

		if (Current.getID().equals(New.getID())
				&& Current.getProjectUrl().equals(New.getProjectUrl())
				&& Current.isNewerPluginVersion(New)) {
			//pluginTracker.addPlugin(Current, PluginTracker.PluginStatus.DELETE);
			delete(Current);
			download(New, taskMonitor);
		} else {
			throw new ManagerException(
					"Failed to update '"
							+ Current.getName()
							+ "', the new plugin did not match what is currently installed\n"
							+ "or the version was not newer than what is currently installed.");
		}
	}

	/**
	 * Downloads given object to the temporary directory.
	 * 
	 * @param Obj
	 *            PluginInfo object to be downloaded
	 * @return File downloaded
	 */
	public PluginInfo download(PluginInfo Obj) throws IOException,
			ManagerException {
		return download(Obj, null);
	}

	/**
	 * Downloads given object to the temporary directory. Uses a task monitor if
	 * available.
	 * 
	 * @param Obj
	 *            PluginInfo object to be downloaded
	 * @param taskMonitor
	 *            TaskMonitor
	 * @param tempDirectory
	 *            Download to a different temporary directory. Default is
	 *            .cytoscape/plugins/[cytoscape version number]
	 * @return File downloaded
	 */
	public PluginInfo download(PluginInfo Obj, TaskMonitor taskMonitor)
			throws IOException, ManagerException {

		File PluginDir = new File(tempDir, Obj.getName() + "-"
				+ Obj.getPluginVersion());
		if (!PluginDir.exists()) {
			PluginDir.mkdirs();
		}

		File Download = null;
		String ClassName = null;
		Download = new File(PluginDir, createFileName(Obj));
		URLUtil.download(Obj.getUrl(), Download, taskMonitor);

		ClassName = getPluginClass(Download.getAbsolutePath(), 
				Obj.getFileType());

		if (ClassName != null) {
			Obj.setPluginClassName(ClassName);
		} else {
			Download.delete();
			Download.getParentFile().delete();
			ManagerException E = new ManagerException(
					Obj.getName()
							+ " does not define the attribute 'Cytoscape-Plugin' in the jar manifest file.\n"
							+ "This plugin cannot be auto-installed.  Please install manually or contact the plugin author.");
			throw E;
		}

		switch (Obj.getFileType()) {
		case JAR: // do nothing, it's installed
			break;
		case ZIP:
			List<String> UnzippedFiles = ZipUtil.unzip(Download
					.getAbsolutePath(), Download.getParent(), taskMonitor);
			Obj.setFileList(UnzippedFiles);
			break;
		}

		Obj.setInstallLocation(PluginDir.getAbsolutePath());
		Obj.addFileName(Download.getAbsolutePath());
		pluginTracker.addPlugin(Obj, PluginTracker.PluginStatus.INSTALL);

		return Obj;
	}

	/*
	 * Methods for loading plugins when Cytoscape starts up. These have been
	 * moved from CytoscapeInit
	 */

	public void loadPlugin(PluginInfo p) throws MalformedURLException,
			IOException, ClassNotFoundException {
		Set<URL> ToLoad = new HashSet<URL>();

		for (String FileName : p.getFileList()) {
			if (FileName.endsWith(".jar")) {
				ToLoad.add(jarURL(FileName));
			}
		}
		loadURLPlugins(ToLoad);
	}

	/**
	 * Parses the plugin input strings and transforms them into the appropriate
	 * URLs or resource names. The method first checks to see if the
	 */
	public void loadPlugins(List<String> p) throws MalformedURLException,
			IOException, ClassNotFoundException {
		Set<String> plugins = new HashSet<String>();

		if (p != null) {
			plugins.addAll(p);
		}

		// Parse the plugin strings and determine whether they're urls,
		// files, directories, class names, or manifest file names.
		for (String currentPlugin : plugins) {
			File f = new File(currentPlugin);

			// If the file name ends with .jar add it to the list as a url.
			if (currentPlugin.endsWith(".jar")) {
				// If the name doesn't match a url, turn it into one.
				if (!currentPlugin.matches(FileUtil.urlPattern)) {
					System.out.println(" - file: " + f.getAbsolutePath());
					pluginURLs.add(jarURL(f.getAbsolutePath()));
				} else {
					System.out.println(" - url: " + f.getAbsolutePath());
					pluginURLs.add(jarURL(currentPlugin));
				}
			} else if (!f.exists()) {
				// If the file doesn't exists, assume
				// that it's a resource plugin.
				System.out.println(" - classpath: " + f.getAbsolutePath());
				resourcePlugins.add(currentPlugin);
			} else if (f.isDirectory()) {
				// If the file is a directory, load
				// all of the jars in the directory.
				System.out.println(" - directory: " + f.getAbsolutePath());

				for (String fileName : f.list()) {
					if (!fileName.endsWith(".jar")) {
						continue;
					}
					pluginURLs.add(jarURL(f.getAbsolutePath()
							+ System.getProperty("file.separator") + fileName));
				}
			} else {
				// Assume the file is a manifest (i.e. list of jar names)
				// and make urls out of them.
				System.out.println(" - file manifest: " + f.getAbsolutePath());

				String text = FileUtil.getInputString(currentPlugin);

				String[] allLines = text.split(System
						.getProperty("line.separator"));
				for (String pluginLoc : allLines) {
					if (pluginLoc.endsWith(".jar")) {
						if (pluginLoc.matches(FileUtil.urlPattern)) {
							pluginURLs.add(jarURL(pluginLoc));
						} else {
							// TODO this should have a better error
							// perhaps, throw an exception??
							System.err.println("Plugin location specified in "
									+ currentPlugin + " is not a valid url: "
									+ pluginLoc + " -- NOT adding it.");
						}
					}
				}
			}
		}
		// now load the plugins in the appropriate manner
		loadURLPlugins(pluginURLs);
		loadResourcePlugins(resourcePlugins);
		cleanCurrentList();
	}

	/**
	 * Load all plugins by using the given URLs loading them all on one
	 * URLClassLoader, then interating through each Jar file looking for classes
	 * that are CytoscapePlugins
	 */
	private void loadURLPlugins(Set<URL> pluginUrls) throws IOException {
		URL[] urls = new URL[pluginUrls.size()];
		pluginUrls.toArray(urls);

		// the creation of the class loader automatically loads the plugins
		classLoader = new URLClassLoader(urls, Cytoscape.class.getClassLoader());

		// iterate through the given jar files and find classes that are
		// assignable from CytoscapePlugin
		for (int i = 0; i < urls.length; ++i) {
			System.out.println("");
			System.out.println("attempting to load plugin url: ");
			System.out.println(urls[i]);

			JarURLConnection jc = (JarURLConnection) urls[i].openConnection();
			JarFile jar = jc.getJarFile();

			// if the jar file is null, do nothing
			if (jar == null) {
				continue;
			}

			// try to get class name from the manifest file
			String className = getPluginClass(jar.getName(),
					PluginInfo.FileType.JAR);

			if (className != null) {
				Class pc = getPluginClass(className);

				if (pc != null) {
					System.out.println("Loading from manifest");
					loadPlugin(pc, jar.getName());
					continue;
				}
			}

			// new-school failed, so revert to old school. Search through the
			// jar entries
			Enumeration entries = jar.entries();

			if (entries == null) {
				System.out.println("Jar file " + jar.getName()
						+ " has no entries");
				continue;
			}

			int totalPlugins = 0;
			while (entries.hasMoreElements()) {
				// get the entry
				String entry = entries.nextElement().toString();

				if (entry.endsWith("class")) {
					// convert the entry to an assignable class name
					entry = entry.replaceAll("\\.class$", "");
					// A regex to match the two known types of file
					// separators. We can't use File.separator because
					// the system the jar was created on is not
					// necessarily the same is the one it is running on.
					entry = entry.replaceAll("/|\\\\", ".");

					Class pc = getPluginClass(entry);

					if (pc == null) {
						continue;
					}

					totalPlugins++;
					loadPlugin(pc, jar.getName());
					break;
				}
			}
			if (totalPlugins == 0) {
				System.out
						.println("No plugin found in specified jar - assuming it's a library.");
			}
		}
		System.out.println("");
	}

	// these are jars that do not extend CytoscapePlugin but may be used by jars
	// that do
	private void loadResourcePlugins(Set<String> resourcePlugins)
			throws ClassNotFoundException {
		// attempt to load resource plugins
		for (String resource : resourcePlugins) {
			System.out.println("");
			System.out.println("attempting to load plugin resourse: "
					+ resource);

			// try to get the class
			Class rclass = null;
			rclass = Class.forName(resource);
			loadPlugin(rclass, null);
		}
		System.out.println("");
	}

	/*
	 * Actually load the plugin
	 */
	private void loadPlugin(Class plugin, String PluginJarFile) {
		if (CytoscapePlugin.class.isAssignableFrom(plugin)
				&& !loadedPlugins.contains(plugin.getName())) {
			try {
				Object obj = CytoscapePlugin.loadPlugin(plugin, PluginJarFile);
				loadedPlugins.add(plugin.getName());
				register((CytoscapePlugin) obj, PluginJarFile);
			} catch (InstantiationException inse) {
				inse.printStackTrace();
			} catch (IllegalAccessException ille) {
				ille.printStackTrace();
			}

		} else if (loadedPlugins.contains(plugin.getName())) {
			// TODO warn user class of this name has already been loaded and
			// can't be loaded again
			System.err.println("A plugin with the name '" + plugin.getName()
					+ "' is already loaded, skipping.");
		}
	}

	/**
	 * Determines whether the class with a particular name extends
	 * CytoscapePlugin by attempting to load the class first.
	 * 
	 * @param name
	 *            the name of the putative plugin class
	 */
	private Class getPluginClass(String name) {
		Class c = null;

		try {
			c = classLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();

			return null;
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();

			return null;
		}

		if (CytoscapePlugin.class.isAssignableFrom(c))
			return c;
		else

			return null;
	}

	// creates a URL object from a jar file name
	private static URL jarURL(String urlString) throws MalformedURLException {
		String uString;
		if (urlString.matches(FileUtil.urlPattern)) {
			uString = "jar:" + urlString + "!/";
		} else {
			uString = "jar:file:" + urlString + "!/";
		}
		return new URL(uString);
	}

	private String createFileName(PluginInfo Obj) {
		return Obj.getName() + "." + Obj.getFileType().toString();
	}

	/*
	 * Iterate through all class files, return the subclass of CytoscapePlugin.
	 * Similar to CytoscapeInit, however only plugins with manifest files that
	 * describe the class of the CytoscapePlugin are valid.
	 */
	private String getPluginClass(String FileName, PluginInfo.FileType Type)
			throws IOException {
		String PluginClassName = null;

		switch (Type) {
		case JAR:
			JarFile Jar = new JarFile(FileName);
			PluginClassName = getManifestAttribute(Jar.getManifest());
			Jar.close();
			break;

		case ZIP:
			List<ZipEntry> Entries = ZipUtil
					.getAllFiles(FileName, "\\w+\\.jar");

			for (ZipEntry Entry : Entries) {
				String EntryName = Entry.getName();

				InputStream is = ZipUtil.readFile(FileName, EntryName);
				JarInputStream jis = new JarInputStream(is);
				PluginClassName = getManifestAttribute(jis.getManifest());
				jis.close();
				is.close();
			}
		}
		;
		return PluginClassName;
	}

	/*
	 * Gets the manifest file value for the Cytoscape-Plugin attribute
	 */
	private String getManifestAttribute(Manifest m) {
		String Value = null;
		if (m != null) {
			Value = m.getMainAttributes().getValue("Cytoscape-Plugin");
		}
		return Value;
	}

}