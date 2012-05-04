/*
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
package cytoscape.filters;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.filters.view.FilterMainPanel;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.*;
import cytoscape.view.cytopanels.CytoPanelImp;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

/**
 * 
 */
public class FilterPlugin extends CytoscapePlugin {

	private static Vector<CompositeFilter> allFilterVect = null;
	private FilterIO filterIO = new FilterIO();
	private CyLogger logger = null;
	
	public static final String DYNAMIC_FILTER_THRESHOLD = "dynamicFilterThreshold";
	public static final int DEFAULT_DYNAMIC_FILTER_THRESHOLD = 1000;

	// Other plugin can turn on/off the FilterEvent
	public static boolean shouldFireFilterEvent = false;

	protected ImageIcon icon = new ImageIcon(getClass().getResource("/stock_filter-data-by-criteria.png"));
	protected ImageIcon icon2 = new ImageIcon(getClass().getResource("/stock_filter-data-by-criteria-16.png"));

	// Other plugin can get a handler to all the filters defined
	public static Vector<CompositeFilter> getAllFilterVect() {
		if (allFilterVect == null) {
			allFilterVect = new Vector<CompositeFilter>();
		}
		return allFilterVect;
	}
	
	/**
	 * Creates a new FilterPlugin object.
	 * 
	 * @param icon
	 *            DOCUMENT ME!
	 * @param csfilter
	 *            DOCUMENT ME!
	 */
	public FilterPlugin() {

		// Add a menuItem on "select" menu
		FilterMenuItemAction menuAction = new FilterMenuItemAction(icon2, this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction(
				(CytoscapeAction) menuAction);

		// Add an icon to tool-bar
		FilterPluginToolBarAction toolbarAction = new FilterPluginToolBarAction(
				icon, this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction(
				(CytoscapeAction) toolbarAction);

		if (allFilterVect == null) {
			allFilterVect = new Vector<CompositeFilter>();
		}

		logger = CyLogger.getLogger(FilterPlugin.class);
		
		restoreInitState();

		// initialize the filterMainPanel and add it to the control panel
		CytoPanelImp cytoPanelWest = (CytoPanelImp) Cytoscape.getDesktop()
		.getCytoPanel(SwingConstants.WEST);

		cytoPanelWest.add("Filters", new FilterMainPanel(allFilterVect));
	}


	/**
	 * DOCUMENT ME!
	 */
	public void onCytoscapeExit() {
		// Save global filter to "filters.prop"
		filterIO.saveGlobalPropFile(CytoscapeInit.getConfigFile("filters.props"));
	}

	public void restoreInitState() {
		final File globalFilterFile = CytoscapeInit.getConfigFile("filters.props");
		int[] loadCount = filterIO.getFilterVectFromPropFile(globalFilterFile);
		logger.debug("FilterPlugin: load " + loadCount[1] + " of " + loadCount[0] + " filters from filters.prop");

		if (loadCount[1] == 0) {
			final String DEFAULT_FILTERS_FILENAME = "/default_filters.props";
			final InputStream inputStream = FilterPlugin.class.getResourceAsStream(DEFAULT_FILTERS_FILENAME);
			if (inputStream == null) {
				System.err.println("FilterPlugin: Failed to read default filters from \""
				                   + DEFAULT_FILTERS_FILENAME + "\" in the plugin's jar file!");
				return;
			}

			final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			loadCount = filterIO.getFilterVectFromPropFile(inputStreamReader);
			logger.debug("FilterPlugin: load " + loadCount[1] + " of " + loadCount[0]
			             + " filters from " + DEFAULT_FILTERS_FILENAME);
		}
	}

	// override the following two methods to save state.
	/**
	 * DOCUMENT ME!
	 * 
	 * @param pStateFileList
	 *            DOCUMENT ME!
	 */
	public void restoreSessionState(List<File> pStateFileList) {
		filterIO.restoreSessionState(pStateFileList);	
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param pFileList
	 *            DOCUMENT ME!
	 */
	public void saveSessionStateFiles(List<File> pFileList) {
		filterIO.saveSessionStateFiles(pFileList);
	}
}
