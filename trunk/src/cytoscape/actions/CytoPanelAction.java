
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

package cytoscape.actions;

import cytoscape.Cytoscape;

import cytoscape.util.CytoscapeAction;

import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.view.cytopanels.CytoPanelName;
import cytoscape.view.cytopanels.CytoPanel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.SwingConstants;
import javax.swing.JCheckBoxMenuItem;


/**
 *
 */
public class CytoPanelAction extends CytoscapeAction {

	protected static String SHOW = "Show";
	protected static String HIDE = "Hide";

	protected String title;
	protected int position;

	/**
	 * @deprecated Use other constructor instead. Will be removed 4/2008.
	 */
	public CytoPanelAction(JCheckBoxMenuItem menuItem, CytoPanel cytoPanel) {
		this(cytoPanel.getTitle(), cytoPanel.getCompassDirection(), menuItem.isSelected());
	}

	public CytoPanelAction(CytoPanelName cp, boolean show) {
		this(cp.getTitle(), cp.getCompassDirection(), show);
	}

	/**
	 * Base class for displaying cytopanel menu items. 
	 *
	 * @param title The title that will be coupled with "show" and "hide" in the menu. 
	 * @param position The SwingConstants.SOUTH,EAST,WEST,etc. position of the cyto panel.
	 * @param show Whether the cytopanel is initially shown at startup or not. Only used
	 * to define the initial title.
	 */
	private CytoPanelAction(String title,int position, boolean show) {
		super(show ? HIDE + " " + title : SHOW + " " + title);
		this.title = title;
		this.position = position;
		setPreferredMenu("View");
	}

	/**
	 * Toggles the cytopanel state.  
	 *
	 * @param ev Triggering event - not used. 
	 */
	public void actionPerformed(ActionEvent ev) {
	
		CytoPanelState curState = Cytoscape.getDesktop().getCytoPanel(position).getState();

		if (curState == CytoPanelState.HIDE) {
			Cytoscape.getDesktop().getCytoPanel(position).setState(CytoPanelState.DOCK);
			putValue(Action.NAME, HIDE + " " + title);

		} else {
			Cytoscape.getDesktop().getCytoPanel(position).setState(CytoPanelState.HIDE);
			putValue(Action.NAME, SHOW + " " + title);
		}
	} 
}
