/*
 File: CloneGraphInNewWindowAction.java

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

//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;
import cytoscape.util.CytoscapeAction;

import java.awt.event.ActionEvent;

import javax.swing.event.MenuEvent;


/**
 *
 */
public class CloneGraphInNewWindowAction extends CytoscapeAction {
	/**
	 * Creates a new CloneGraphInNewWindowAction object.
	 */
	public CloneGraphInNewWindowAction() {
		super("Clone current network");
		setPreferredMenu("File.New.Network");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		CyNetwork current_network = Cytoscape.getCurrentNetwork();
		VisualStyle vs = Cytoscape.getCurrentNetworkView().getVisualStyle();
		String viewName = CytoscapeInit.getProperties().getProperty("defaultVisualStyle"); 
		if ( vs != null )
			viewName = vs.getName();

		CyNetwork new_network = Cytoscape.createNetwork(current_network.getNodeIndicesArray(),
		                                                current_network.getEdgeIndicesArray(),
		                                                current_network.getTitle() + " copy", 
														null,
														true);

		CyNetworkView view = Cytoscape.getNetworkView(new_network.getIdentifier());
		if ( view != null || view != Cytoscape.getNullNetworkView() )
			view.setVisualStyle(viewName);
	}

	public void menuSelected(MenuEvent e) {
		enableForNetworkAndView();
	}
}