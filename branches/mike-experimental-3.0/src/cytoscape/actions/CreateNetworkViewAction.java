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

import org.cytoscape.GraphPerspective;


/**
 *
 */
public class CreateNetworkViewAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339869385438L;
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
		GraphPerspective cyNetwork = Cytoscape.getCurrentNetwork();
		createViewFromCurrentNetwork(cyNetwork);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param cyNetwork DOCUMENT ME!
	 */
	public static void createViewFromCurrentNetwork(GraphPerspective cyNetwork) {
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

			if (n == JOptionPane.YES_OPTION) {
				Cytoscape.createNetworkView(cyNetwork);
			} else {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
				                              "Create View Request Cancelled by User.");
			}
		} else {
			Cytoscape.createNetworkView(cyNetwork);
		}
	}

	/**
	 * Sets the state of the action before rendering the menu. 
	 */
	public void menuSelected(MenuEvent e) {
		GraphPerspective currNet = Cytoscape.getCurrentNetwork();
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

}
