/*
 File: CreateNetworkViewAction.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.actions;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.util.CytoscapeAction;

import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

import javax.swing.event.MenuEvent;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

import cytoscape.task.util.TaskManager;


/**
 *
 */
public class CreateNetworkViewAction extends CytoscapeAction {
	/**
	 * Creates a new CreateNetworkViewAction object.
	 */
	public CreateNetworkViewAction() {
		super("Create View");
		setPreferredMenu("Edit");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_V, ActionEvent.ALT_MASK);
	}

	/**
	 * Creates a new CreateNetworkViewAction object.
	 *
	 * @param label  DOCUMENT ME!
	 */
	public CreateNetworkViewAction(boolean label) {
		super();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
		createViewFromCurrentNetwork(cyNetwork);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param cyNetwork DOCUMENT ME!
	 */
	public static void createViewFromCurrentNetwork(CyNetwork cyNetwork) {
		NumberFormat formatter = new DecimalFormat("#,###,###");

		if (cyNetwork.getNodeCount() > Integer.parseInt(CytoscapeInit.getProperties()
                                                        .getProperty("secondaryViewThreshold"))) {
			int n = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
			                                      "Network contains "
			                                      + formatter.format(cyNetwork.getNodeCount())
			                                      + " nodes and "
			                                      + formatter.format(cyNetwork.getEdgeCount())
			                                      + " edges.  "
			                                      + "\nRendering a network this size may take several "
			                                      + "minutes.\n" + "Do you wish to proceed?",
			                                      "Rendering Large Network",
			                                      JOptionPane.YES_NO_OPTION);

			if (n == JOptionPane.NO_OPTION) 
				return;
		} 
		
		// Create Task
		CreateNetworkViewTask task = new CreateNetworkViewTask(cyNetwork);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();

		jTaskConfig.displayCancelButton(false);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}

	/**
	 * Sets the state of the action before rendering the menu. 
	 */
	public void menuSelected(MenuEvent e) {
		CyNetwork currNet = Cytoscape.getCurrentNetwork();
		if ( currNet == null || currNet == Cytoscape.getNullNetwork() ) {
			setEnabled(false);
			return;
		}
		CyNetworkView currView = Cytoscape.getNetworkView(currNet.getIdentifier());
		if ( currView == null || currView == Cytoscape.getNullNetworkView() )
			setEnabled(true);
		else
			setEnabled(false);
	}

} //End of SaveSessionAction


class CreateNetworkViewTask implements Task {
	private CyNetwork network;
	private TaskMonitor taskMonitor;

	CreateNetworkViewTask(CyNetwork network) {
		this.network = network;
	}

	public void run() {
		taskMonitor.setStatus("Creating network view ...");
		taskMonitor.setPercentCompleted(-1);

		try {
			Cytoscape.createNetworkView(network);
		} catch (Exception e) {
			taskMonitor.setException(e, "Could not create network view for network: " + network.getTitle());
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Network view successfully create for:  " + network.getTitle());
	}

	public void halt() { }

	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	public String getTitle() {
		return "Creating Network View";
	}
} 


