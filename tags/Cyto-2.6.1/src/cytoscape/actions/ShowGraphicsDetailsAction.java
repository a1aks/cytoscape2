/*
 File: ShowGraphicsDetailsAction.java

 Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

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

import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.CyGraphAllLOD;
import cytoscape.ding.DingNetworkView;
import cytoscape.render.stateful.GraphLOD;

import java.awt.event.ActionEvent;

import javax.swing.event.MenuEvent;
import javax.swing.Action;


/**
 *
 */
public class ShowGraphicsDetailsAction extends CytoscapeAction {
	boolean showFlag = true;

	/**
	 * Creates a new ShowGraphicsDetailsAction object.
	 */
	public ShowGraphicsDetailsAction() {
		super("Show Graphics Details");
		setPreferredMenu("View");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_S, ActionEvent.ALT_MASK);
	}

	/**
	 * Creates a new ShowGraphicsDetailsAction object.
	 *
	 * @param label  DOCUMENT ME!
	 */
	public ShowGraphicsDetailsAction(boolean label) {
		super();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		CyNetworkView currView = Cytoscape.getCurrentNetworkView();
		if (!showFlag) {
			((DingNetworkView)currView).setGraphLOD(new CyGraphLOD());
		} else {
			((DingNetworkView)currView).setGraphLOD(new CyGraphAllLOD());
		}
	}

	/**
	 * Sets the state of the action before rendering the menu. 
	 */
	public void menuSelected(MenuEvent e) {
		CyNetworkView currView = Cytoscape.getCurrentNetworkView();
		if ( currView == null || currView == Cytoscape.getNullNetworkView() )
			setEnabled(false);
		else {
			setEnabled(true);
			// Get the current graph LOD
			GraphLOD lod = ((DingNetworkView)currView).getGraphLOD();
			if (CyGraphLOD.class.isInstance(lod)) {
				putValue(Action.NAME, "Show Graphics Details");
				showFlag = true;
			} else {
				putValue(Action.NAME, "Hide Graphics Details");
				showFlag = false;
			}
		}
	}

}
