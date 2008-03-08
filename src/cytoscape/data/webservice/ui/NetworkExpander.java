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
package cytoscape.data.webservice.ui;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.DatabaseSearchResult;
import cytoscape.data.webservice.NetworkImportWebServiceClient;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;

import cytoscape.layout.CyLayouts;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

import cytoscape.task.util.TaskManager;

import ding.view.NodeContextMenuListener;

import giny.model.Node;

import giny.view.NodeView;

import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;


/**
 * Context menu for expanding node using external database (web service)
 *
 * @author kono
 * @since Cytoscape 2.6
 * @version 0.3
 */
public class NetworkExpander implements PropertyChangeListener, NodeContextMenuListener {
	// Default layout algorithm name in property.
	private static final String LAYOUT_PROP = "expanderDefaultLayout";
	private static final String DEF_LAYOUT = "force-directed";

	// This map manages list of expander-compatible clients.
	// Key is the display name, and value is client ID.
	private Map<String, String> clientMap;
	private JMenu rootMenu;
	private String defLayout;

	/**
	 * Creates a new NetworkExpander object.
	 * This is a context menu for nodes.
	 */
	public NetworkExpander() {
		// Listening to event from core.
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);

		// Set layout algorithm
		defLayout = CytoscapeInit.getProperties().getProperty(LAYOUT_PROP);

		if (defLayout == null)
			defLayout = DEF_LAYOUT;

		clientMap = new HashMap<String, String>();

		final List<WebServiceClient> clients = WebServiceClientManager.getAllClients();

		rootMenu = new JMenu("Get neighbors of selected node(s) from external databases");

		for (WebServiceClient client : clients) {
			if (client.isCompatibleType(ClientType.NETWORK)) {
				JMenu menu = new JMenu(client.getDisplayName());
				addMenuItem(client, menu);
				clientMap.put(client.getDisplayName(), client.getClientID());
			}
		}
	}

	private JMenuItem getDefaultExpander() {
		return null;
	}

	private void addDefaultMenuItem() {
	}

	private void addMenuItem(final WebServiceClient client, final JMenu clientRootMenu) {
		final JMenuItem expandMenu = new JMenuItem(new AbstractAction("expand by IDs") {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Start expanding network: " + e.getActionCommand());

					final CyWebServiceEvent evt = new CyWebServiceEvent(clientMap.get(client
					                                                                                                                        .getDisplayName()),
					                                                    WSEventType.SEARCH_DATABASE,
					                                                    buildStringQuery(),
					                                                    WSEventType.EXPAND_NETWORK);

					SearchTask task = new SearchTask(evt);

					// Configure JTask Dialog Pop-Up Box
					final JTaskConfig jTaskConfig = new JTaskConfig();
					jTaskConfig.setOwner(Cytoscape.getDesktop());
					jTaskConfig.displayCloseButton(true);
					jTaskConfig.displayStatus(true);
					jTaskConfig.setAutoDispose(true);

					// Execute Task in New Thread; pops open JTask Dialog Box.
					TaskManager.executeTask(task, jTaskConfig);
				}
			});

		clientRootMenu.add(expandMenu);

		rootMenu.add(clientRootMenu);
	}

	private String buildStringQuery() {
		final StringBuilder builder = new StringBuilder();
		final Set<Node> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		for (Node node : selectedNodes) {
			builder.append(node.getIdentifier() + " ");
		}

		return builder.toString();
	}

	/**
	 *  Catch result from the service.
	 *
	 * @param evt DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		Object resultObj = evt.getNewValue();

		if (evt.getPropertyName().equals(CyWebServiceEvent.WSResponseType.SEARCH_FINISHED.toString())
		    && ((DatabaseSearchResult) resultObj).getNextMove().equals(WSEventType.EXPAND_NETWORK)) {
			System.out.println("Search result from " + evt.getSource() + ", Number of result = "
			                   + evt.getNewValue() + ", Source name = " + evt.getOldValue());

			String[] message = {
			                       ((DatabaseSearchResult) resultObj).getResultSize()
			                       + " interactions found.",
			                       
			"Do you want to add new nodes and edges to " + Cytoscape.getCurrentNetwork().getTitle()
			                       + "?"
			                   };
			int value = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), message,
			                                          "Expand network", JOptionPane.YES_NO_OPTION);

			if (value == JOptionPane.YES_OPTION) {
				
				CyWebServiceEvent evt2 = new CyWebServiceEvent(evt.getOldValue().toString(),
				                                               WSEventType.EXPAND_NETWORK,
				                                               ((DatabaseSearchResult) resultObj).getResult());

				try {
					WebServiceClientManager.getCyWebServiceEventSupport().fireCyWebServiceEvent(evt2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (evt.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)
		           && evt.getSource() instanceof NetworkImportWebServiceClient) {
			String[] message = { "Neighbours loaded.", "Do you want to layout the network now?" };
			int value = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), message,
			                                          "Expansion complete",
			                                          JOptionPane.YES_NO_OPTION);

			if (value == JOptionPane.YES_OPTION) {
				CyLayouts.getLayout(defLayout).doLayout();
			}
		} else if (evt.getPropertyName().equals(Cytoscape.PREFERENCES_UPDATED)) {
			defLayout = CytoscapeInit.getProperties().getProperty("expanderDefaultLayout");

			if (defLayout == null) {
				defLayout = "force-directed";
			}
		}
	}

	/**
	 * @param nodeView The clicked NodeView
	 * @param menu popup menu to add the Bypass menu
	 */
	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
		if (menu == null)
			menu = new JPopupMenu();

		menu.add(this.rootMenu);
	}

	class SearchTask implements Task {
		private CyWebServiceEvent evt;
		private TaskMonitor taskMonitor;

		public SearchTask(CyWebServiceEvent evt) {
			this.evt = evt;
		}

		public String getTitle() {
			// TODO Auto-generated method stub
			return "Expanding Network";
		}

		public void halt() {
			// TODO Auto-generated method stub
		}

		public void run() {
			taskMonitor.setStatus("Loading neighbours...");
			taskMonitor.setPercentCompleted(-1);

			// this even will load the file
			try {
				WebServiceClientManager.getCyWebServiceEventSupport().fireCyWebServiceEvent(evt);
			} catch (Exception e) {
				taskMonitor.setException(e, "Failed to load neighbours.");

				return;
			}

			taskMonitor.setPercentCompleted(100);

			Cytoscape.getDesktop().setFocus(Cytoscape.getCurrentNetwork().getIdentifier());

			String curNetID = Cytoscape.getCurrentNetwork().getIdentifier();

			Cytoscape.getNetworkView(curNetID)
			         .setVisualStyle(Cytoscape.getVisualMappingManager().getVisualStyle().getName());
			Cytoscape.getNetworkView(curNetID).redrawGraph(false, true);
		}

		public void setTaskMonitor(TaskMonitor arg0) throws IllegalThreadStateException {
			this.taskMonitor = arg0;
		}
	}
}
